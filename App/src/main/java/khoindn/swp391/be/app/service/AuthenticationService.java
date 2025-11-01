package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.exception.exceptions.*;
import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.LoginUser;
import khoindn.swp391.be.app.model.Request.RegisterUserReq;
import khoindn.swp391.be.app.model.Response.UsersResponse;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.repository.IAuthenticationRepository;
import khoindn.swp391.be.app.repository.IUserRepository;
import khoindn.swp391.be.app.repository.IUserRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private IAuthenticationRepository iAuthenticationRepository;
    @Autowired
    private IUserRepository iUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TokenService tokenService;
    @Autowired
    private IUserRoleRepository iUserRoleRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = iAuthenticationRepository.findUsersByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    public Users register(RegisterUserReq users) {
        // Kiểm tra email
        if (iAuthenticationRepository.existsByEmail((users.getEmail()))) {
            throw new EmailDuplicatedException("Email đã được sử dụng");
        }

        // Kiểm tra CCCD
        if (iAuthenticationRepository.existsByCccd((users.getCccd()))) {
            throw new CCCDDuplicatedException("CCCD đã được sử dụng");
        }

        // Kiểm tra GPLX
        if (iAuthenticationRepository.existsByGplx((users.getGplx()))) {
            throw new GPLXDuplicatedException("GPLX đã được sử dụng");
        }

        // Kiểm tra phone
        if (iAuthenticationRepository.existsByPhone((users.getPhone()))) {
            throw new PhoneDuplicatedException("Số điện thoại đã được sử dụng");

        }

        if (!iUserRoleRepository.existsUserRoleByRoleId((users.getRoleId()))) {
            throw new RoleIsNotExistedException("Vai trò đã tồn tại");
        }

        //process login from register controller

        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users user = modelMapper.map(users, Users.class);
        user.setId(null);
        user.setRole(iUserRoleRepository.findUserRoleByRoleId(users.getRoleId()));

        //create key pairs for user

        try {

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            System.out.println("✅ Private & Public Key đã được tạo.");

            //set public key to user to save to database
            byte[] publicKey = keyPair.getPublic().getEncoded();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey);
            user.setPublicKey(publicKeyString);

            //transmit private ket to byte to send email to this user
            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

            // Truyền dữ liệu cho Thymeleaf
            Context context = new Context();
            context.setVariable("fullName", user.getHovaTen());
            context.setVariable("systemName", "EcoShare Management");
            context.setVariable("privateKeyBase64",
                    Base64.getEncoder().encodeToString(privateKeyBytes));

            String htmlContent = templateEngine.process("sendPrivateKey", context);

            EmailDetailReq contentSender = new EmailDetailReq();
            contentSender.setEmail(user.getEmail());
            contentSender.setSubject("[EcoShare][Important] Your Private Key");
            contentSender.setTemplate(htmlContent);
            emailService.sendEmail(contentSender);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        //encode old password to new password
        System.out.println("Req: " + users);

        System.out.println("User: " + user);


            // save to DB
            return iAuthenticationRepository.save(user);
    }

    public UsersResponse login(LoginUser loginUser) {
        // logic and authorized

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()));
        Users users = (Users) authentication.getPrincipal();
//        if (loginUser.getRoleId() != null && !loginUser.getRoleId().equals(users.getRole().getRoleId())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sai loại tài khoản");
//        }
        //map account --> accountResponse
        UsersResponse usersResponse = modelMapper.map(users, UsersResponse.class);
        String token = tokenService.generateToken(users);
        usersResponse.setToken(token);
        return usersResponse;
    }

    public Users getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Principal type: " +
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());


        if (principal instanceof Users) {
            return (Users) principal;
        } else {
            throw new AuthenticationException("User is not logged in or token is invalid");
        }
    }


}

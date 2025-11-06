package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.exception.exceptions.*;
import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.LoginUser;
import khoindn.swp391.be.app.model.Request.RegisterUserReq;
import khoindn.swp391.be.app.model.Response.UsersResponse;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.repository.IAuthenticationRepository;
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
// (Đã xóa "implements UserDetailsService" -> Rất Tốt, giữ nguyên)
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private IAuthenticationRepository iAuthenticationRepository;
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


    public Users register(RegisterUserReq users) {
        // (Phần kiểm tra (validation) giữ nguyên)


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
            System.out.println(users.getRoleId());
            throw new RoleIsNotExistedException("Vai trò ko tồn tại");
        }
        // ... (các hàm kiểm tra cccd, phone, role...)

        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users user = modelMapper.map(users, Users.class);
        user.setId(null);
        user.setRole(iUserRoleRepository.findUserRoleByRoleId(users.getRoleId()));

        try {

            // 1. Tạo Key Pair
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            System.out.println("✅ Private & Public Key đã được tạo.");

            // 2. Set public key cho user
            byte[] publicKey = keyPair.getPublic().getEncoded();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey);
            user.setPublicKey(publicKeyString);

            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

            // 3. Chuẩn bị nội dung email
            Context context = new Context();
            context.setVariable("fullName", user.getHovaTen());
            context.setVariable("systemName", "EcoShare Management"); // (Bạn có thể đổi tên này)
            context.setVariable("privateKeyBase64",
                    Base64.getEncoder().encodeToString(privateKeyBytes));

            String htmlContent = templateEngine.process("sendPrivateKey", context);

            // 4. Tạo biến 'contentSender'
            EmailDetailReq contentSender = new EmailDetailReq();
            contentSender.setEmail(user.getEmail());
            contentSender.setSubject("[EcoShare][Important] Your Private Key"); // (Bạn có thể đổi tiêu đề)
            contentSender.setTemplate(htmlContent);


            // Dòng này của bạn bây giờ đã có thể chạy
            emailService.sendEmail(contentSender);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Req: " + users);
        System.out.println("User: " + user);
        return iAuthenticationRepository.save(user);
    }

    public UsersResponse login(LoginUser loginUser) {
        // (Logic đăng nhập của bạn giữ nguyên)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()));
        Users users = (Users) authentication.getPrincipal();

        UsersResponse usersResponse = modelMapper.map(users, UsersResponse.class);
        String token = tokenService.generateToken(users);
        usersResponse.setToken(token);
        System.out.println(usersResponse);
        return usersResponse;
    }

    public Users getCurrentAccount() {
        // (Logic này giữ nguyên)
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Principal type: " +
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());

        if (principal instanceof Users) {
            return (Users) principal;
        } else {
            throw new AuthenticationException("User is not logged in or token is invalid");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = iAuthenticationRepository.findUsersByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }
}
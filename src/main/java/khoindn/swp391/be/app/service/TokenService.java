package khoindn.swp391.be.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.repository.IAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.function.Function;


@Service
@Transactional
public class TokenService {
    private final String SECRET_KEY = "khoimapu2k56h7l8o9p3r4s5t6u7v8w9x0y1z2a3b4c5d6e7f8g9h0i1j2k3l4m5n6";
    @Autowired
    IAuthenticationRepository authenticationRepository;

    //generate and validate token
// thêm ở đầu file (bạn đã có)

    public SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);  // thử decode Base64
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            // fallback: dùng bytes UTF-8 (sẽ cần StandardCharsets)
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8); // <<< dùng ở đây
            if (keyBytes.length < 32) {
                throw new IllegalStateException("SECRET_KEY must be at least 32 bytes (256 bits) for HS256.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }


    public String generateToken(Users account) {
        long now = System.currentTimeMillis();
        long exp = now + 1000 * 60 * 60 * 24; // 24 giờ

        return Jwts.builder()
                .subject(String.valueOf(account.getId()))
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(getSignInKey())
                .compact();
    }


    public Users extractToken(String token) {
        String value = extractClaim(token, Claims::getSubject);
        int id = Integer.parseInt(value);
        Users user = authenticationRepository.findUserById(id);   // SỬA: gán vào biến
        if (user == null) {                                       // + THÊM
            throw new IllegalArgumentException("User not found for id: " + id);
        }
        return user;                                              // SỬA: trả biến user
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser().
                verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

}

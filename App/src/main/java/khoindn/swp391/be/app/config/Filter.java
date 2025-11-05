package khoindn.swp391.be.app.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import khoindn.swp391.be.app.exception.exceptions.AuthenticationException;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    TokenService tokenService;

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/chat",
            "POST:/auth/register",
            "POST:/auth/login/**",
            "POST:/email/send-otp",
            "POST:/Schedule/**",
            "GET:/swagger-ui/**",
            "GET:/v3/api-docs/**",
            "GET:/swagger-resources/**",
            "GET:/api/fund-payment/success/**",
            "GET:/api/fund-fee/**"
            );

    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();


        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;

            String allowedMethod = parts[0];
            String allowedUri = parts[1];

            return matcher.match(allowedUri, uri);
        });
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Filter is called");
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        String uri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println(method + "-" + uri);
        if (isPublicAPI(uri, method)) {
            //Api public => access
            System.out.println("This is a public API");
            filterChain.doFilter(request, response);
        } else {
            Users user = null;
            //Api private (theo role)=> check token
            String token = getToken(request);

            if (token != null) {
                user = tokenService.extractToken(token);
            } else {
                resolver.resolveException(request, response, null, new AuthenticationException("Token is missing"));
            }

            //Luu thong tin nguoi dang request
            //Luu session lai

            UsernamePasswordAuthenticationToken
                    authenToken =
                    new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());

            authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenToken);


            // Co token
            // Nen phai verify lại token

            try {
                tokenService.extractToken(token);
            } catch (ExpiredJwtException e) {
                resolver.resolveException(request, response, null, new AuthenticationException("Token is expired!"));
            } catch (MalformedJwtException e) {
                resolver.resolveException(request, response, null, new AuthenticationException("Invalid Token!"));

            }
            // 1. Token hết hạn
            // 2. Token sai

            filterChain.doFilter(request, response);
        }
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }

}

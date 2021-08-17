package eu.silktrader.kairos.auth;

import eu.silktrader.kairos.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var jwt = extractJwt(request);

        // set authentication state when the JWT's contents are validated
        if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
            var userDetails = userService.loadUserByUsername(jwtService.getUserNameFromJwt(jwt));
            var authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // forward the request down the chain in any case
        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        return (StringUtils.hasText(token) && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }

}

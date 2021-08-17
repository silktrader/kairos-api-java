package eu.silktrader.kairos.auth;

import eu.silktrader.kairos.exception.KairosException;
import eu.silktrader.kairos.user.User;
import eu.silktrader.kairos.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.time.Instant;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void signUp(SignUpDto signUpDto) {

        var user = new User();
        user.setCreated(Instant.now());
        user.setName(signUpDto.getName());
        user.setEmail(signUpDto.getEmail());

        // hash the password before storing it
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        this.userRepository.save(user);
    }

    public AuthenticationDto signIn(SignInDto signInDto) throws GeneralSecurityException {
        // set the authentication state for the user through static method
        var authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInDto.name(), signInDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // generate a token and respond with it
        return new AuthenticationDto(jwtService.generateToken(authenticate), signInDto.name());
    }
    
    public User getCurrentUser() {
        var principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByName(principal.getUsername())
                .orElseThrow(() -> new KairosException("User " + principal.getUsername() + " not found"));
    }

}

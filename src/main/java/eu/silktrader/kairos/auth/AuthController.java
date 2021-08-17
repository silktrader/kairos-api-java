package eu.silktrader.kairos.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpDto signupDto) {
        authService.signUp(signupDto);
        return new ResponseEntity<>(signupDto.getName() + " registered successfully", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public AuthenticationDto signIn(@RequestBody SignInDto signinDto) throws GeneralSecurityException {
        return authService.signIn(signinDto);

    }


}

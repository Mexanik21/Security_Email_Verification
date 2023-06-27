package com.example.security_full.registration;


import com.example.security_full.event.RegistrationCompleteEvent;
import com.example.security_full.registration.token.VerificationToken;
import com.example.security_full.registration.token.VerificationTokenRepository;
import com.example.security_full.user.IUserService;
import com.example.security_full.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {
    private final IUserService iUserService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = iUserService.registerUser(registrationRequest);
        //publisher reg
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success! Please, check your email for to complete your registration";
    }

    @GetMapping("/verifyYourEmail")
    public String verifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "This token already verified, please, login.";
        }
        String verificationResult = iUserService.validationToken(token);

        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    private String applicationUrl(HttpServletRequest request) {
        return  "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}

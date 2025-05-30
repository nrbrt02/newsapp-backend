package news.app.newsApp.controller;

import news.app.newsApp.dto.TwoFactorAuthDto;
import news.app.newsApp.dto.JwtResponse;
import news.app.newsApp.dto.MessageResponse;
import news.app.newsApp.model.User;
import news.app.newsApp.service.TwoFactorAuthService;
import news.app.newsApp.service.UserService;
import news.app.newsApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/2fa")
public class TwoFactorAuthController {

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/verify-login")
    public ResponseEntity<?> verifyLoginCode(@RequestBody TwoFactorAuthDto request) {
        if (twoFactorAuthService.verifyCode(request.getEmail(), request.getCode())) {
            User user = userService.findByEmail(request.getEmail());
            JwtResponse response = authService.completeAuthentication(user.getUsername());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Invalid or expired verification code");
    }

    @PostMapping("/verify-reset-password")
    public ResponseEntity<?> verifyResetPasswordCode(@RequestBody TwoFactorAuthDto request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest().body(new MessageResponse("New password must be at least 6 characters long"));
        }

        if (twoFactorAuthService.verifyCode(request.getEmail(), request.getCode())) {
            try {
                userService.resetPassword(request.getEmail(), request.getNewPassword());
                return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Failed to reset password: " + e.getMessage()));
            }
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired verification code"));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            twoFactorAuthService.generateAndSendVerificationCode(user);
            return ResponseEntity.ok("Verification code has been sent to your email");
        }
        return ResponseEntity.badRequest().body("User not found");
    }
} 
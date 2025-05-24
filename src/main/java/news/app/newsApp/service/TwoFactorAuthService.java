package news.app.newsApp.service;

import news.app.newsApp.model.User;
import news.app.newsApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TwoFactorAuthService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Map<String, LocalDateTime> codeExpiryTimes = new HashMap<>();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 5;

    public void generateAndSendVerificationCode(User user) {
        String code = generateVerificationCode();
        String email = user.getEmail();
        
        // Store the code and its expiry time
        verificationCodes.put(email, code);
        codeExpiryTimes.put(email, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        // Send the code via email
        sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        LocalDateTime expiryTime = codeExpiryTimes.get(email);

        if (storedCode == null || expiryTime == null) {
            return false;
        }

        // Check if code has expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            verificationCodes.remove(email);
            codeExpiryTimes.remove(email);
            return false;
        }

        // Verify the code
        boolean isValid = storedCode.equals(code);
        
        // If valid, remove the code
        if (isValid) {
            verificationCodes.remove(email);
            codeExpiryTimes.remove(email);
        }

        return isValid;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    private void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code + "\n\n" +
                       "This code will expire in " + CODE_EXPIRY_MINUTES + " minutes.\n" +
                       "If you didn't request this code, please ignore this email.");
        
        mailSender.send(message);
    }

    public void clearVerificationCode(String email) {
        verificationCodes.remove(email);
        codeExpiryTimes.remove(email);
    }
} 
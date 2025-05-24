package news.app.newsApp.service;

import news.app.newsApp.model.User;
import news.app.newsApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TwoFactorAuthService {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthService.class);

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
        
        logger.info("Generated verification code for email: {}, code: {}", email, code);

        // Send the code via email
        sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        LocalDateTime expiryTime = codeExpiryTimes.get(email);

        logger.info("Verifying code for email: {}, provided code: {}, stored code: {}, expiry time: {}", 
                   email, code, storedCode, expiryTime);

        if (storedCode == null || expiryTime == null) {
            logger.warn("No stored code found for email: {}", email);
            return false;
        }

        // Check if code has expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            logger.warn("Code has expired for email: {}", email);
            verificationCodes.remove(email);
            codeExpiryTimes.remove(email);
            return false;
        }

        // Verify the code
        boolean isValid = storedCode.equals(code);
        logger.info("Code verification result for email {}: {}", email, isValid);
        
        // If valid, remove the code
        if (isValid) {
            verificationCodes.remove(email);
            codeExpiryTimes.remove(email);
            logger.info("Code removed after successful verification for email: {}", email);
        }

        return isValid;
    }

    public String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        String generatedCode = code.toString();
        logger.info("Generated new verification code: {}", generatedCode);
        return generatedCode;
    }

    private void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code + "\n\n" +
                       "This code will expire in " + CODE_EXPIRY_MINUTES + " minutes.\n" +
                       "If you didn't request this code, please ignore this email.");
        
        mailSender.send(message);
        logger.info("Verification email sent to: {}", email);
    }

    public void sendPasswordResetEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("You have requested to reset your password.\n\n" +
                       "Your verification code is: " + code + "\n\n" +
                       "This code will expire in " + CODE_EXPIRY_MINUTES + " minutes.\n" +
                       "If you didn't request this password reset, please ignore this email and ensure your account is secure.");
        
        mailSender.send(message);
        logger.info("Password reset email sent to: {}", email);
    }

    public void storeVerificationCode(String email, String code) {
        verificationCodes.put(email, code);
        codeExpiryTimes.put(email, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        logger.info("Stored verification code for email: {}", email);
    }

    public void clearVerificationCode(String email) {
        verificationCodes.remove(email);
        codeExpiryTimes.remove(email);
        logger.info("Verification code cleared for email: {}", email);
    }
} 
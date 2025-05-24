package news.app.newsApp.dto;

import lombok.Data;

@Data
public class TwoFactorAuthDto {
    private String email;
    private String code;
    private String newPassword;  // Only used for password reset
} 
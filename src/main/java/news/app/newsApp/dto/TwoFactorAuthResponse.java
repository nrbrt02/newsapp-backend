package news.app.newsApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorAuthResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean requiresTwoFactor;
    private String message;
} 
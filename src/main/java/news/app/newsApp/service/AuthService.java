package news.app.newsApp.service;

import news.app.newsApp.dto.AuthRequest;
import news.app.newsApp.dto.JwtResponse;
import news.app.newsApp.dto.RegisterRequest;
import news.app.newsApp.dto.TwoFactorAuthResponse;
import news.app.newsApp.exception.ResourceAlreadyExistsException;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.UserRepository;
import news.app.newsApp.security.JwtTokenProvider;
import news.app.newsApp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    public TwoFactorAuthResponse authenticateUser(AuthRequest loginRequest) {
        // First, authenticate the user's credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Get the user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate and send 2FA code
        twoFactorAuthService.generateAndSendVerificationCode(user);

        // Return a response indicating 2FA is required
        return new TwoFactorAuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", ""),
                true,
                "Please check your email for the verification code"
        );
    }

    public JwtResponse completeAuthentication(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create authentication object
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Generate the final JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "")
        );
    }

    public User registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhone(signUpRequest.getPhone());
        user.setRole(User.Role.READER); // Default role
        user.setIsActive(true);

        return userRepository.save(user);
    }
}
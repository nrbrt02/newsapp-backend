package news.app.newsApp.service;

import news.app.newsApp.dto.AuthRequest;
import news.app.newsApp.dto.MessageResponse;
import news.app.newsApp.dto.UserDto;
import news.app.newsApp.exception.ResourceNotFoundException;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.UserRepository;
import news.app.newsApp.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private JwtTokenProvider jwtUtils;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserDto.class);
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public UserDto getUserProfile() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserDto.class);
    }
    
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User currentUser = getCurrentUser();
        
        // Only admins can create users
        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new AccessDeniedException("Only administrators can create new users");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Validate required fields
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        User user = modelMapper.map(userDto, User.class);
        
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        // Set default values if not provided
        if (user.getRole() == null) {
            user.setRole(User.Role.READER); // Default role
        }
        
        if (user.getIsActive() == null) {
            user.setIsActive(true); // Default active status
        }
        
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Only admin or the user themselves can update their profile
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        // Only admin can change roles
        if (userDto.getRole() != null && !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new AccessDeniedException("Only administrators can change user roles");
        }

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        if (userDto.getProfilePic() != null) {
            user.setProfilePic(userDto.getProfilePic());
        }
        // Handle password update with proper hashing
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getRole() != null && currentUser.getRole().equals(User.Role.ADMIN)) {
            user.setRole(userDto.getRole());
        }
        if (userDto.getIsActive() != null && currentUser.getRole().equals(User.Role.ADMIN)) {
            user.setIsActive(userDto.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        User currentUser = getCurrentUser();
        
        // Only admin or the user themselves can delete their account
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You don't have permission to delete this user");
        }
        
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto changeUserRole(Long id, User.Role role) {
        User currentUser = getCurrentUser();
        
        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new AccessDeniedException("Only administrators can change user roles");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public ResponseEntity<?> login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate and send 2FA code
            twoFactorAuthService.generateAndSendVerificationCode(user);

            return ResponseEntity.ok(new MessageResponse("Verification code has been sent to your email"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid username or password"));
        }
    }

    public ResponseEntity<?> requestPasswordReset(String email) {
        User user = findByEmail(email);
        if (user != null) {
            // Generate and send 2FA code
            twoFactorAuthService.generateAndSendVerificationCode(user);
            return ResponseEntity.ok(new MessageResponse("Verification code has been sent to your email"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
    }

    public void resetPassword(String email, String newPassword) {
        User user = findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Map<String, Object> generateAuthResponse(User user) {
        // Create Authentication object for JWT
        org.springframework.security.core.userdetails.UserDetails userDetails =
            org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtUtils.generateToken(authentication);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        return response;
    }
}
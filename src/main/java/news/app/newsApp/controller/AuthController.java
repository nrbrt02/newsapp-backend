package news.app.newsApp.controller;

import jakarta.validation.Valid;
import news.app.newsApp.dto.AuthRequest;
import news.app.newsApp.dto.JwtResponse;
import news.app.newsApp.dto.RegisterRequest;
import news.app.newsApp.dto.UserDto;
import news.app.newsApp.dto.TwoFactorAuthResponse;
import news.app.newsApp.model.User;
import news.app.newsApp.service.AuthService;
import news.app.newsApp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TwoFactorAuthResponse> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        TwoFactorAuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.registerUser(registerRequest);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return userService.requestPasswordReset(email);
    }
}
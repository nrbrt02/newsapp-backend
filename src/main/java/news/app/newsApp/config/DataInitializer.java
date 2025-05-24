package news.app.newsApp.config;

import news.app.newsApp.model.User;
import news.app.newsApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Check if admin user exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("nrbrt2002@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("Pa$$w0rd!"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setIsActive(true);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(adminUser);
            
            System.out.println("Admin user has been created.");
        }
    }
}
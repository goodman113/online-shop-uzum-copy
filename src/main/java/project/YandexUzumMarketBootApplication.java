package project;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.model.User;
import project.model.enums.Role;
import project.repository.repository.UserRepository;
import project.repository.repository.VendorRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class YandexUzumMarketBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(YandexUzumMarketBootApplication.class, args);
    }
//    @Bean
    public CommandLineRunner init(PasswordEncoder passwordEncoder,
                                  UserRepository userRepository) {
        return args -> {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("admin");
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
            user.setVendorProfile(null);
            user.setPhone("112223344");
            userRepository.save(user);
        };

    }
}

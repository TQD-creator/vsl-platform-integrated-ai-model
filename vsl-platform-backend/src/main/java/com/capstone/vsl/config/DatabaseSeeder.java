package com.capstone.vsl.config;

import com.capstone.vsl.entity.Alphabet;
import com.capstone.vsl.entity.Role;
import com.capstone.vsl.entity.User;
import com.capstone.vsl.repository.AlphabetRepository;
import com.capstone.vsl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AlphabetRepository alphabetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedAlphabet();
    }

    /**
     * Seed initial users (Admin and User)
     */
    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Database is empty. Seeding initial users...");

            // Create Admin user
            var admin = User.builder()
                    .username("admin")
                    .email("admin@vsl-platform.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: username=admin, password=admin123");

            // Create regular User
            var user = User.builder()
                    .username("user")
                    .email("user@vsl-platform.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            log.info("User created: username=user, password=user123");

            log.info("User seeding completed successfully.");
        } else {
            log.info("Database already contains users. Skipping user seed.");
        }
    }

    /**
     * Seed alphabet data (letters a-z and numbers 0-9)
     */
    private void seedAlphabet() {
        if (alphabetRepository.count() == 0) {
            log.info("Alphabet table is empty. Seeding alphabet data...");

            // Seed letters a-z
            for (char c = 'a'; c <= 'z'; c++) {
                var alphabet = Alphabet.builder()
                        .character(String.valueOf(c))
                        .imageUrl("https://example.com/gestures/" + c + ".png")
                        .type("LETTER")
                        .build();
                alphabetRepository.save(alphabet);
            }
            log.info("Seeded 26 letters (a-z)");

            // Seed numbers 0-9
            for (char c = '0'; c <= '9'; c++) {
                var alphabet = Alphabet.builder()
                        .character(String.valueOf(c))
                        .imageUrl("https://example.com/gestures/" + c + ".png")
                        .type("NUMBER")
                        .build();
                alphabetRepository.save(alphabet);
            }
            log.info("Seeded 10 numbers (0-9)");

            log.info("Alphabet seeding completed successfully.");
        } else {
            log.info("Alphabet table already contains data. Skipping alphabet seed.");
        }
    }
}


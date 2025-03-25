package com.sarva.distributed;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sarva.distributed.repository.UserRepository;
import com.sarva.distributed.models.User;


@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
public void run(String... args) throws Exception {
    if (userRepository.count() == 0) {
        List<User> users = Arrays.asList(
            new User("Alice", "ali123", "sara@gmail.com"),
            new User("Sarva", "ney", "vanan@gmail.com"),
            new User("John", "john456", "john@gmail.com"),
            new User("sjr", "ney", "22pc32@psgtech.ac.in")
             // Both sports
        );

        userRepository.saveAll(users);
        System.out.println("User data seeded successfully.");
    }
}

}

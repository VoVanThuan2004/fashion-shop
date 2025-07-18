package com.example.clothes_website;

import com.example.clothes_website.entity.Role;
import com.example.clothes_website.entity.UserRole;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.repository.RoleRepository;
import com.example.clothes_website.repository.UserRepository;
import com.example.clothes_website.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra đã có role: admin, user chưa
        Optional<Role> roleAdminTest = roleRepository.findByRoleName("ADMIN");
        Optional<Role> roleUserTest = roleRepository.findByRoleName("USER");

        if (roleAdminTest.isPresent() && roleUserTest.isPresent()) {
            System.out.println("Account admin already exists");
        }
        else {
            // Tạo role
            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");
            roleAdmin.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            roleAdmin = roleRepository.save(roleAdmin);

            Role roleUser = new Role();
            roleUser.setRoleName("USER");
            roleUser.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            roleRepository.save(roleUser);

            // Tạo tài khoản admin mặc định
            Users users = new Users();
            users.setFullName("Admin");
            users.setPassword(passwordEncoder.encode("Admin123456"));
            users.setEmail("admin@gmail.com");
            users.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            users.setActive(true);
            users = userRepository.save(users);

            // Tạo User Role
            UserRole userRole = new UserRole();
            userRole.setUser(users);
            userRole.setRole(roleAdmin);
            userRoleRepository.save(userRole);

            System.out.println("Account admin created successfully!!");

        }
    }

}

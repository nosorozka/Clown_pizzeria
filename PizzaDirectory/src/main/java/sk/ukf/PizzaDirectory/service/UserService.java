package sk.ukf.PizzaDirectory.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ukf.PizzaDirectory.dto.UserRegistrationDto;
import sk.ukf.PizzaDirectory.entity.Role;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.exception.EmailAlreadyExistsException;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.RoleRepository;
import sk.ukf.PizzaDirectory.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail(), true);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_USER not found"));

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAddress(dto.getAddress());
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}


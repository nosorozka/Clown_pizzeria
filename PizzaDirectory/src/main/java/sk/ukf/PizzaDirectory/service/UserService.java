package sk.ukf.PizzaDirectory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.ukf.PizzaDirectory.dto.UserRegistrationDto;
import sk.ukf.PizzaDirectory.entity.Order;
import sk.ukf.PizzaDirectory.entity.Role;
import sk.ukf.PizzaDirectory.entity.RoleName;
import sk.ukf.PizzaDirectory.entity.User;
import sk.ukf.PizzaDirectory.exception.EmailAlreadyExistsException;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.OrderRepository;
import sk.ukf.PizzaDirectory.repository.RoleRepository;
import sk.ukf.PizzaDirectory.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                      OrderRepository orderRepository, PasswordEncoder passwordEncoder,
                      FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findByDeletedFalse(pageable);
    }

    /** Returns all users including deactivated (for admin list). */
    @Transactional(readOnly = true)
    public Page<User> findAllIncludingDeleted(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail(), true);
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
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
        User user = findById(id);
        softDelete(id);
    }

    public void softDelete(Integer id) {
        User user = findById(id);
        user.softDelete();
        userRepository.save(user);
    }

    /** Permanently deletes user from DB (and related orders where user is customer). No recovery. */
    public void deletePermanently(Integer id) {
        User user = findById(id);
        for (Order o : orderRepository.findByCookIdOrderByCreatedAtDesc(id)) {
            o.setCook(null);
            orderRepository.save(o);
        }
        for (Order o : orderRepository.findByCourierIdOrderByCreatedAtDesc(id)) {
            o.setCourier(null);
            orderRepository.save(o);
        }
        for (Order o : orderRepository.findByUserIdOrderByCreatedAtDesc(id)) {
            orderRepository.delete(o);
        }
        if (user.getAvatarPath() != null) {
            fileStorageService.deleteFile(user.getAvatarPath());
        }
        userRepository.delete(user);
    }

    public void activate(Integer id) {
        User user = findById(id);
        user.setDeleted(false);
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changeEmail(Integer userId, String newEmail, String password) {
        User user = findById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        String trimmed = newEmail == null ? null : newEmail.trim();
        if (trimmed == null || trimmed.isEmpty()) {
            throw new IllegalArgumentException("New email is required");
        }
        if (trimmed.equalsIgnoreCase(user.getEmail())) {
            return;
        }
        if (userRepository.existsByEmail(trimmed)) {
            throw new EmailAlreadyExistsException(trimmed, true);
        }
        user.setEmail(trimmed);
        userRepository.save(user);
    }

    public User updateProfile(Integer userId, String firstName, String lastName,
                             java.time.LocalDate dateOfBirth, String address) {
        User user = findById(userId);
        
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        user.setDateOfBirth(dateOfBirth);
        user.setAddress(address);
        
        return userRepository.save(user);
    }

    public User uploadAvatar(Integer userId, MultipartFile file) {
        User user = findById(userId);
        
        // Delete old avatar if exists
        if (user.getAvatarPath() != null) {
            fileStorageService.deleteFile(user.getAvatarPath());
        }
        
        // Save new avatar
        String filename = fileStorageService.storeFile(file);
        user.setAvatarPath(filename);
        
        return userRepository.save(user);
    }

    public void updateAddress(User user, String address) {
        user.setAddress(address);
        userRepository.save(user);
    }

    public boolean canMakeOrders(User user) {
        // Any authenticated user (USER, COOK, COURIER, ADMIN) can place orders
        return user.getRole() != null && user.isActive();
    }

    public void changeRole(Integer userId, RoleName roleName) {
        User user = findById(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + roleName + " not found"));
        user.setRole(role);
        userRepository.save(user);
    }
}


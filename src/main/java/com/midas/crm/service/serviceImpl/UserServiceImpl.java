package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        // Inicializar el usuario ADMIN
        initializeAdminUser();
    }

    /*@Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    */

    // Método para inicializar el usuario ADMIN
    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("12345678")); // Cambia a una contraseña segura
            adminUser.setNombre("Andree");
            adminUser.setApellido("Prueba");
            adminUser.setTelefono("123456789");
            adminUser.setSede("Chiclayo");
            adminUser.setEmail("admin@midas.pe");
            adminUser.setFechaCreacion(LocalDateTime.now());
            adminUser.setRole(Role.ADMIN);
            adminUser.setEstado("A");

            userRepository.save(adminUser);
            System.out.println("Usuario ADMIN creado exitosamente.");
        } else {
            System.out.println("El usuario ADMIN ya existe.");
        }
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User saveUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Longitud de la contraseña cifrada: " + encodedPassword.length());
        user.setPassword(encodedPassword);
        user.setRole(Role.ADMIN);
        user.setEstado("A"); // Estado "A" de Activo
        user.setFechaCreacion(LocalDateTime.now());
        User userCreated = userRepository.save(user);
        String jwt = jwtProvider.generateToken(userCreated);
        userCreated.setToken(jwt);
        return userCreated;
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }
    @Override
    public Optional<User> findByEmail(String email) {return userRepository.findByEmail(email);}

    @Transactional
    @Override
    public void changeRole(Role newRole, String username)
    {
        userRepository.updateUserRole(username, newRole);
    }

    @Override
    public User findByUsernameReturnToken(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe:" + username));

        String jwt = jwtProvider.generateToken(user);
        user.setToken(jwt);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            return null;
        }
    }
    @Override
    public Optional<User> getdByUsernameOrEmail(String nombreOrEmail)
    {
        return userRepository.findByUsernameOrEmail(nombreOrEmail, nombreOrEmail);
    }

    public void updateTokenPassword(User user, String newTokenPassword) {
        user.setTokenPassword(newTokenPassword);
        userRepository.save(user);
    }

    @Override
    public Optional<User> getByTokenPassword(String tokenPassword) {
        return userRepository.findByTokenPassword(tokenPassword);
    }

    @Transactional
    @Override
    public void saveUsers(List<User> users) {
        for (User user : users) {
            if (userRepository.existsByUsername(user.getUsername())) {
                continue;
            }

            user.setRole(Role.ASESOR);
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encriptar contraseña
            user.setEstado("A"); // Estado Activo por defecto
            user.setFechaCreacion(LocalDateTime.now()); // Fecha de creación actual
            userRepository.save(user);
        }
    }

    @Override
    public User updateUser(Long id, User updateUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Llamar al nuevo método para actualizar los atributos
            User updatedUser = updateUserAttributes(existingUser, updateUser);

            return userRepository.save(updatedUser);
        } else {
            return null;
        }
    }

    // Nuevo método para actualizar atributos de manera más genérica
    private User updateUserAttributes(User existingUser, User updateUser) {
        if (updateUser.getNombre() != null) {
            existingUser.setNombre(updateUser.getNombre());
        }
        if (updateUser.getApellido() != null) {
            existingUser.setApellido(updateUser.getApellido());
        }
        if (updateUser.getUsername() != null) {
            existingUser.setUsername(updateUser.getUsername());
        }
        if (updateUser.getTelefono() != null) {
            existingUser.setTelefono(updateUser.getTelefono());
        }
        if (updateUser.getEmail() != null) {
            existingUser.setEmail(updateUser.getEmail());
        }
        if (updateUser.getTokenPassword() != null) {
            existingUser.setTokenPassword(updateUser.getTokenPassword());
        }
        return existingUser;
    }
}
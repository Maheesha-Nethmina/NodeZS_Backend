package com.example.NodeZS_Backend.Service;

import com.example.NodeZS_Backend.DTO.UserDTO;
import com.example.NodeZS_Backend.Entity.User;
import com.example.NodeZS_Backend.Repository.UserRepository;
import com.example.NodeZS_Backend.Util.VarList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Requirement 4.1: Proper separation of concerns.
     * Handles the logic for creating a new user.
     */
    public String saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.RSP_DUPLICATED;
        } else {
            User user = new User();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());

            // FIX: Encrypt the password and save only the encrypted version
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            userRepository.save(user);
            return VarList.RSP_SUCCESS;
        }
    }

    /**
     * UPDATED: Returns UserDTO so the frontend can display the user's name.
     * Compares hashed credentials using BCrypt.
     */
    public UserDTO loginUser(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (user != null) {
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                UserDTO responseDTO = new UserDTO();
                // FIX: Map the User ID from the Entity to the DTO
                responseDTO.setId(user.getId());
                responseDTO.setName(user.getName());
                responseDTO.setEmail(user.getEmail());
                return responseDTO;
            }
        }
        return null;
    }

    /**
     * Handles logout logic.
     */
    public String logoutUser() {
        return VarList.RSP_SUCCESS;
    }
}
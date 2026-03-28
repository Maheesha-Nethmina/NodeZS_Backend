package com.example.NodeZS_Backend.Service;

import com.example.NodeZS_Backend.DTO.UserDTO;
import com.example.NodeZS_Backend.Entity.User;
import com.example.NodeZS_Backend.Repository.UserRepository;
import com.example.NodeZS_Backend.Util.VarList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Requirement: User Registration
     */
    public String saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.RSP_DUPLICATED;
        } else {
            User user = new User();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            // Mapping password from DTO to Entity
            user.setPassword(userDTO.getPassword());
            userRepository.save(user);
            return VarList.RSP_SUCCESS;
        }
    }

    /**
     * Requirement: User Login Authentication
     */
    public String loginUser(UserDTO userDTO) {
        // Fetch user from database via Repository
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (user != null) {
            // Compare plain-text password (as per core requirements)
            if (user.getPassword().equals(userDTO.getPassword())) {
                return VarList.RSP_SUCCESS;
            } else {
                return VarList.RSP_NOT_AUTHORISED; // Invalid password
            }
        }
        return VarList.RSP_NO_DATA_FOUND; // Email not found
    }

    /**
     * Placeholder for Logout logic
     */
    public String logoutUser() {
        return VarList.RSP_SUCCESS;
    }
}
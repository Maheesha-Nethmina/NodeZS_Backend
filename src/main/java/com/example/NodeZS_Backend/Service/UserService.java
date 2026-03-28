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
            // Mapping the password sent from the React frontend
            user.setPassword(userDTO.getPassword());
            userRepository.save(user);
            return VarList.RSP_SUCCESS;
        }
    }

    /**
     * Logic for user authentication.
     * Compares credentials provided by the LoginPage.
     */
    public String loginUser(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (user != null) {
            // Requirement 3.1: Task Assignment uses email as the identifier.
            if (user.getPassword().equals(userDTO.getPassword())) {
                return VarList.RSP_SUCCESS;
            } else {
                return VarList.RSP_NOT_AUTHORISED;
            }
        }
        return VarList.RSP_NO_DATA_FOUND;
    }

    /**
     * Handles logout logic.
     * Currently returns success to confirm the request was processed.
     */
    public String logoutUser() {
        // You can add logic here to log the logout time or audit the event
        return VarList.RSP_SUCCESS;
    }
}
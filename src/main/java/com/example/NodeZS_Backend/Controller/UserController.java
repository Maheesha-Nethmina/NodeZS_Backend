package com.example.NodeZS_Backend.Controller;

import com.example.NodeZS_Backend.DTO.UserDTO;
import com.example.NodeZS_Backend.Service.UserService;
import com.example.NodeZS_Backend.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin // Requirement: Allows React frontend to connect [cite: 22]
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Handles User Registration
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = userService.saveUser(userDTO);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "User Registered Successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created [cite: 48]
            } else if (res.equals(VarList.RSP_DUPLICATED)) {
                response.put("code", VarList.RSP_DUPLICATED);
                response.put("message", "Email Already Exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request [cite: 48]
            } else {
                response.put("code", VarList.RSP_FAIL);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Error [cite: 48]
        }
    }

    /**
     * UPDATED: Handles User Login and returns User Content.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            // FIX: Service now returns UserDTO content
            UserDTO authUser = userService.loginUser(userDTO);

            if (authUser != null) {
                response.put("code", VarList.RSP_SUCCESS); // "00"
                response.put("message", "Login Successful");

                // FIX: Send user name and email to React frontend
                response.put("content", authUser);

                // For the Bonus JWT requirement [cite: 80]
                response.put("token", "dummy-jwt-token");
                return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK
            } else {
                response.put("code", VarList.RSP_NOT_AUTHORISED);
                response.put("message", "Invalid Credentials");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
            }
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles User Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", VarList.RSP_SUCCESS);
        response.put("message", "Logged out successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
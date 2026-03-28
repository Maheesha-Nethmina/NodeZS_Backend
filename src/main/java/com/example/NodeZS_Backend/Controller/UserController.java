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
@CrossOrigin // Essential for React (port 5173/3000) to talk to Spring (8080)
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = userService.saveUser(userDTO);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "User Registered Successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else if (res.equals(VarList.RSP_DUPLICATED)) {
                response.put("code", VarList.RSP_DUPLICATED);
                response.put("message", "Email Already Exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                response.put("code", VarList.RSP_FAIL);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = userService.loginUser(userDTO);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Login Successful");
                response.put("token", "dummy-jwt-token"); // Required for your React logic
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (res.equals(VarList.RSP_NOT_AUTHORISED)) {
                response.put("code", VarList.RSP_NOT_AUTHORISED);
                response.put("message", "Invalid Password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                response.put("code", VarList.RSP_NO_DATA_FOUND);
                response.put("message", "Invalid Email or Password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", VarList.RSP_SUCCESS);
        response.put("message", "Logged out successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
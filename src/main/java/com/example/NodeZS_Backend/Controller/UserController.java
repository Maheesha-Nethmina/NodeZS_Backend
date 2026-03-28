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
@CrossOrigin // Allows React frontend to connect
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
}
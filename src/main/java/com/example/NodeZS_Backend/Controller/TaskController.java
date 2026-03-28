package com.example.NodeZS_Backend.Controller;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Service.TaskService;
import com.example.NodeZS_Backend.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/task")
@CrossOrigin // Allows React connection
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveTask(@RequestBody TaskDTO taskDTO) {
        Map<String, Object> response = new HashMap<>();

        // Server-side validation for required title [cite: 49, 65]
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            response.put("code", VarList.RSP_FAIL);
            response.put("message", "Title is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String res = taskService.saveTask(taskDTO);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Task Created Successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
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
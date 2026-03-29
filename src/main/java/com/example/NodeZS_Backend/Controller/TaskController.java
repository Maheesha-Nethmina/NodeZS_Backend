package com.example.NodeZS_Backend.Controller;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Enum.Status;
import com.example.NodeZS_Backend.Service.TaskService;
import com.example.NodeZS_Backend.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * Requirement 3.1: Handles User Task Creation.
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveTask(@RequestBody TaskDTO taskDTO) {
        Map<String, Object> response = new HashMap<>();

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
                return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    /**
     * Requirement 82 UPDATED: Handles Pagination, Filtering, and Sorting.
     * Receives 'sortBy' (dueDate or priority) from Frontend.
     * Fixed Argument Logic: Corrected to match updated TaskService signature.
     */
    @GetMapping("/getAllPaged")
    public ResponseEntity<Map<String, Object>> getAllTasksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "dueDate") String sortBy) {

        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            // Pass pageable, status, and sortBy to the manual sorting logic in Service
            Map<String, Object> content = taskService.getAllTasks(pageable, status, sortBy);

            if (content != null) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Tasks retrieved successfully");
                response.put("content", content);
                return new ResponseEntity<>(response, HttpStatus.OK);
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

    /**
     * Fetch tasks created by the logged-in user using userId.
     * Used for the /my-tasks page.
     */
    @GetMapping("/getMyTasks")
    public ResponseEntity<Map<String, Object>> getMyTasks(
            @RequestParam int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);

            Map<String, Object> content = taskService.getTasksByUserId(userId, pageable);

            if (content != null) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Personal tasks retrieved by User ID");
                response.put("content", content);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Requirement 3.3: Update status and assignment.
     */
    @PutMapping("/updateStatus")
    public ResponseEntity<Map<String, Object>> updateStatus(@RequestBody TaskDTO taskDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = taskService.updateTaskStatus(taskDTO.getTaskid(), taskDTO.getStatus(), taskDTO.getAssigneeEmail());

            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Task Assigned and Status Updated");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update existing task details (Edit modal logic).
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateTask(@RequestBody TaskDTO taskDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = taskService.updateTask(taskDTO);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Task updated successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete selected task.
     */
    @DeleteMapping("/delete/{taskid}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable int taskid) {
        Map<String, Object> response = new HashMap<>();
        try {
            String res = taskService.deleteTask(taskid);
            if (res.equals(VarList.RSP_SUCCESS)) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("message", "Task deleted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch tasks where the user is the assignee.
     * Powers the Selection page with Checkbox updates.
     */
    @GetMapping("/getAssignedTasks")
    public ResponseEntity<Map<String, Object>> getAssignedTasks(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            // Service handles Status -> Priority sort for assigned selections
            Map<String, Object> content = taskService.getTasksByAssigneeEmail(email, pageable);

            if (content != null) {
                response.put("code", VarList.RSP_SUCCESS);
                response.put("content", content);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("code", VarList.RSP_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
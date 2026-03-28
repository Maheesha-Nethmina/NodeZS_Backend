package com.example.NodeZS_Backend.Controller;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Enum.Status;
import com.example.NodeZS_Backend.Service.TaskService;
import com.example.NodeZS_Backend.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * UPDATED Requirement 82: Handles Pagination, Filtering, and Fixed Sorting.
     * status: optional (null displays All)
     * sortBy: "dueDate" (ASC) or "priority" (DESC for High->Medium->Low)
     */
    @GetMapping("/getAllPaged")
    public ResponseEntity<Map<String, Object>> getAllTasksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "dueDate") String sortBy) {

        Map<String, Object> response = new HashMap<>();
        try {
            Sort sort;
            // Logic: Priority needs DESC to show High > Medium > Low alphabetically
            // Due Date needs ASC to show soonest deadlines first
            if ("priority".equalsIgnoreCase(sortBy)) {
                sort = Sort.by("priority").descending();
            } else {
                sort = Sort.by("dueDate").ascending();
            }

            Pageable pageable = PageRequest.of(page, size, sort);

            // Fetch data from service (Service handles null status as "fetch all")
            Map<String, Object> content = taskService.getAllTasks(pageable, status);

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
}
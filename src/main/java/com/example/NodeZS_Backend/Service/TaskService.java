package com.example.NodeZS_Backend.Service;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Entity.Task;
import com.example.NodeZS_Backend.Enum.Status;
import com.example.NodeZS_Backend.Repository.TaskRepository;
import com.example.NodeZS_Backend.Util.VarList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Requirement 3.1: Create a new task with required and optional fields.
     */
    public String saveTask(TaskDTO taskDTO) {
        try {
            Task task = new Task();
            task.setTitle(taskDTO.getTitle()); // Required
            task.setDescription(taskDTO.getDescription()); // Optional
            task.setDueDate(taskDTO.getDueDate()); // Optional
            task.setPriority(taskDTO.getPriority()); // Enum: LOW/MEDIUM/HIGH

            // Ensure status is TODO on creation if not provided
            if (taskDTO.getStatus() == null) {
                task.setStatus(Status.TODO);
            } else {
                task.setStatus(taskDTO.getStatus());
            }

            taskRepository.save(task);
            return VarList.RSP_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.RSP_ERROR;
        }
    }

    /**
     * Requirement 82 & Filtering: Fetch tasks with Pagination and Status filtering.
     * Maps Entities to DTOs to ensure a consistent JSON response structure.
     */
    public Map<String, Object> getAllTasks(Pageable pageable, Status status) {
        try {
            Page<Task> taskPage;

            // Requirement: Ability to filter by status (To Do, In Progress, Done)
            if (status != null) {
                // This calls the custom method in your TaskRepository
                taskPage = taskRepository.findByStatus(status, pageable);
            } else {
                // If no status is provided, fetch all tasks with sorting/pagination applied
                taskPage = taskRepository.findAll(pageable);
            }

            List<TaskDTO> taskDTOList = new ArrayList<>();
            for (Task task : taskPage.getContent()) {
                TaskDTO dto = new TaskDTO();
                dto.setTaskid(task.getTaskid());
                dto.setTitle(task.getTitle());
                dto.setDescription(task.getDescription());
                dto.setStatus(task.getStatus());
                dto.setPriority(task.getPriority());
                dto.setDueDate(task.getDueDate());
                dto.setCreatedAt(task.getCreatedAt());
                dto.setCompletedAt(task.getCompletedAt()); // Included completedAt for DONE tasks
                taskDTOList.add(dto);
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tasks", taskDTOList);
            responseData.put("totalPages", taskPage.getTotalPages());
            responseData.put("totalElements", taskPage.getTotalElements());
            responseData.put("currentPage", taskPage.getNumber());

            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Requirement 3.3: Update task status and record the assignee email.
     */
    public String updateTaskStatus(int taskid, Status status, String email) {
        try {
            Task task = taskRepository.findById(taskid).orElse(null);
            if (task != null) {
                task.setStatus(status);
                // Requirement 3.1: Update the assigneeEmail with the logged-in user's email
                task.setAssigneeEmail(email);

                // Optional: If status is DONE, set completion time
                if (status == Status.DONE) {
                    task.setCompletedAt(java.time.LocalDateTime.now());
                }

                taskRepository.save(task);
                return VarList.RSP_SUCCESS;
            }
            return VarList.RSP_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.RSP_ERROR;
        }
    }
}
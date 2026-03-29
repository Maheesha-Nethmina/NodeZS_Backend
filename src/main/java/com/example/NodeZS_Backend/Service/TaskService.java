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
     * Requirement 3.1 UPDATED: Create a new task with creator's User ID.
     */
    public String saveTask(TaskDTO taskDTO) {
        try {
            Task task = new Task();
            // CRITICAL UPDATE: Store the ID of the user creating the task
            task.setUserId(taskDTO.getUserId());

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
     * Maps Entities to DTOs including the userId.
     */
    public Map<String, Object> getAllTasks(Pageable pageable, Status status) {
        try {
            Page<Task> taskPage;

            // Requirement: Ability to filter by status (To Do, In Progress, Done)
            if (status != null) {
                taskPage = taskRepository.findByStatus(status, pageable);
            } else {
                taskPage = taskRepository.findAll(pageable);
            }

            List<TaskDTO> taskDTOList = new ArrayList<>();
            for (Task task : taskPage.getContent()) {
                TaskDTO dto = new TaskDTO();
                dto.setTaskid(task.getTaskid());
                dto.setUserId(task.getUserId()); // NEW: Map UserID to DTO
                dto.setTitle(task.getTitle());
                dto.setDescription(task.getDescription());
                dto.setStatus(task.getStatus());
                dto.setPriority(task.getPriority());
                dto.setDueDate(task.getDueDate());
                dto.setCreatedAt(task.getCreatedAt());
                dto.setCompletedAt(task.getCompletedAt());
                dto.setAssigneeEmail(task.getAssigneeEmail());
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

    /**
     * NEW: Fetch tasks assigned specifically to a user (by email).
     * Supports pagination for the "My Tasks" view.
     */
    /**
     * UPDATED: Fetch tasks created/owned by a specific user using their ID.
     */
    public Map<String, Object> getTasksByUserId(int userId, Pageable pageable) {
        try {
            // Querying by userId instead of assigneeEmail
            Page<Task> taskPage = taskRepository.findByUserId(userId, pageable);

            List<TaskDTO> taskDTOList = new ArrayList<>();
            for (Task task : taskPage.getContent()) {
                TaskDTO dto = new TaskDTO();
                dto.setTaskid(task.getTaskid());
                dto.setUserId(task.getUserId());
                dto.setTitle(task.getTitle());
                dto.setDescription(task.getDescription());
                dto.setStatus(task.getStatus());
                dto.setPriority(task.getPriority());
                dto.setDueDate(task.getDueDate());
                dto.setCreatedAt(task.getCreatedAt());
                dto.setCompletedAt(task.getCompletedAt());
                dto.setAssigneeEmail(task.getAssigneeEmail());
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
     * Update selected task details.
     */
    public String updateTask(TaskDTO taskDTO) {
        try {
            Task task = taskRepository.findById(taskDTO.getTaskid()).orElse(null);
            if (task != null) {
                task.setTitle(taskDTO.getTitle());
                task.setDescription(taskDTO.getDescription());
                task.setPriority(taskDTO.getPriority());
                task.setDueDate(taskDTO.getDueDate());
                task.setStatus(taskDTO.getStatus());

                taskRepository.save(task);
                return VarList.RSP_SUCCESS;
            }
            return VarList.RSP_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.RSP_ERROR;
        }
    }

    /**
     * Delete selected task.
     */
    public String deleteTask(int taskid) {
        try {
            if (taskRepository.existsById(taskid)) {
                taskRepository.deleteById(taskid);
                return VarList.RSP_SUCCESS;
            }
            return VarList.RSP_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.RSP_ERROR;
        }
    }


    /**
     * NEW: Fetch tasks assigned to a specific user (by assigneeEmail).
     * This powers the Selection page.
     */
    /**
     * UPDATED for Selection Page: Fetch tasks assigned to a specific user.
     * Applied Hierarchical Sorting: Status (TODO -> IN_PROGRESS -> DONE)
     * then Priority (HIGH -> MEDIUM -> LOW).
     */
    public Map<String, Object> getTasksByAssigneeEmail(String email, Pageable pageable) {
        try {
            // 1. Fetch all assigned tasks for this email
            List<Task> allAssigned = taskRepository.findByAssigneeEmail(email);

            // 2. Apply the custom sorting logic
            allAssigned.sort((a, b) -> {
                // First: Sort by Status (TODO < IN_PROGRESS < DONE)
                int statusCompare = a.getStatus().ordinal() - b.getStatus().ordinal();
                if (statusCompare != 0) return statusCompare;

                // Second: Sort by Priority (HIGH < MEDIUM < LOW)
                return a.getPriority().ordinal() - b.getPriority().ordinal();
            });

            // 3. Handle Manual Pagination
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allAssigned.size());
            List<Task> pagedTasks = (start <= allAssigned.size()) ? allAssigned.subList(start, end) : new ArrayList<>();

            // 4. Map to DTOs
            List<TaskDTO> taskDTOList = new ArrayList<>();
            for (Task task : pagedTasks) {
                TaskDTO dto = new TaskDTO();
                dto.setTaskid(task.getTaskid());
                dto.setUserId(task.getUserId());
                dto.setTitle(task.getTitle());
                dto.setDescription(task.getDescription());
                dto.setStatus(task.getStatus());
                dto.setPriority(task.getPriority());
                dto.setDueDate(task.getDueDate());
                dto.setAssigneeEmail(task.getAssigneeEmail());
                taskDTOList.add(dto);
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tasks", taskDTOList);
            responseData.put("totalPages", (int) Math.ceil((double) allAssigned.size() / pageable.getPageSize()));
            responseData.put("totalElements", allAssigned.size());
            responseData.put("currentPage", pageable.getPageNumber());

            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
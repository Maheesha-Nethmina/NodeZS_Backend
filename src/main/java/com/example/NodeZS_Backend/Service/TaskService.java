package com.example.NodeZS_Backend.Service;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Entity.Task;
import com.example.NodeZS_Backend.Enum.Status;
import com.example.NodeZS_Backend.Repository.TaskRepository;
import com.example.NodeZS_Backend.Util.VarList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * Creates a new task with creator's User ID.
     */
    public String saveTask(TaskDTO taskDTO) {
        try {
            Task task = new Task();
            task.setUserId(taskDTO.getUserId());
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setDueDate(taskDTO.getDueDate());
            task.setPriority(taskDTO.getPriority());

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
     * Dashboard Fetch: Includes Status filtering and Manual Sort by Priority or Due Date.
     * FIX: Calls taskRepository.findByStatus(status) which returns a List.
     */
    public Map<String, Object> getAllTasks(Pageable pageable, Status status, String sortBy) {
        try {
            List<Task> allTasks;
            if (status != null) {
                allTasks = taskRepository.findByStatus(status);
            } else {
                allTasks = taskRepository.findAll();
            }

            // --- CUSTOM SORTING ---
            allTasks.sort((a, b) -> {
                if ("priority".equalsIgnoreCase(sortBy)) {
                    return a.getPriority().ordinal() - b.getPriority().ordinal();
                } else {
                    if (a.getDueDate() == null && b.getDueDate() == null) return 0;
                    if (a.getDueDate() == null) return 1;
                    if (b.getDueDate() == null) return -1;
                    return a.getDueDate().compareTo(b.getDueDate());
                }
            });

            return manualPagination(allTasks, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Selection Page: Fetch tasks assigned to a specific user via email.
     * Logic: Status (TODO -> IN_PROGRESS -> DONE) then Priority.
     */
    public Map<String, Object> getTasksByAssigneeEmail(String email, Pageable pageable) {
        try {
            List<Task> allAssigned = taskRepository.findByAssigneeEmail(email);

            allAssigned.sort((a, b) -> {
                int statusCompare = a.getStatus().ordinal() - b.getStatus().ordinal();
                if (statusCompare != 0) return statusCompare;
                return a.getPriority().ordinal() - b.getPriority().ordinal();
            });

            return manualPagination(allAssigned, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * My Tasks Page: Fetch tasks created by a specific user using their ID.
     */
    public Map<String, Object> getTasksByUserId(int userId, Pageable pageable) {
        try {
            List<Task> myCreatedTasks = taskRepository.findByUserId(userId);

            // Default Sort: Due Date
            myCreatedTasks.sort((a, b) -> {
                if (a.getDueDate() == null && b.getDueDate() == null) return 0;
                if (a.getDueDate() == null) return 1;
                if (b.getDueDate() == null) return -1;
                return a.getDueDate().compareTo(b.getDueDate());
            });

            return manualPagination(myCreatedTasks, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Shared Helper: Manual Pagination and DTO Mapping.
     */
    private Map<String, Object> manualPagination(List<Task> allItems, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allItems.size());

        List<Task> pagedList = new ArrayList<>();
        if (start < allItems.size()) {
            pagedList = allItems.subList(start, end);
        }

        List<TaskDTO> taskDTOList = new ArrayList<>();
        for (Task task : pagedList) {
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
        responseData.put("totalPages", (int) Math.ceil((double) allItems.size() / pageable.getPageSize()));
        responseData.put("totalElements", allItems.size());
        responseData.put("currentPage", pageable.getPageNumber());

        return responseData;
    }

    /**
     * Requirement 3.3 UPDATED: Update task status and record the assignee email.
     * NEW FEATURE: If email is null or empty, it unassigns the task and sets status to TODO.
     */
    public String updateTaskStatus(int taskid, Status status, String email) {
        try {
            Task task = taskRepository.findById(taskid).orElse(null);
            if (task != null) {
                // Check for unassignment request (empty email)
                if (email == null || email.trim().isEmpty()) {
                    task.setAssigneeEmail(null);
                    task.setStatus(Status.TODO); // Make it available for others
                    task.setCompletedAt(null);    // Reset completion time
                } else {
                    task.setStatus(status);
                    task.setAssigneeEmail(email);

                    // Handle completion timestamp
                    if (status == Status.DONE) {
                        task.setCompletedAt(LocalDateTime.now());
                    } else {
                        task.setCompletedAt(null);
                    }
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
     * Update full task details.
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
     * Delete a task.
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
}
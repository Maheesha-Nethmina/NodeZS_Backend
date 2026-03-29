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

    //create new task (userid saved)
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

    //Retrieves tasks for the dashboard with status filtering and custom sorting by priority or due date.
    public Map<String, Object> getAllTasks(Pageable pageable, Status status, String sortBy) {
        try {
            List<Task> allTasks;
            if (status != null) {
                allTasks = taskRepository.findByStatus(status);
            } else {
                allTasks = taskRepository.findAll();
            }

            // make sorting
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

    //Loads tasks assigned to a specific user and sorts them by their current status and priority level.
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

   //display the task by using userid
    public Map<String, Object> getTasksByUserId(int userId, Pageable pageable) {
        try {
            List<Task> myCreatedTasks = taskRepository.findByUserId(userId);

            // display accourding to due date
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

   //Handles manual pagination by calculating list offsets and mapping the resulting sub-list into a standard response format.
    private Map<String, Object> manualPagination(List<Task> allItems, Pageable pageable) {
   //Calculate the start and end indices for the current page slice
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allItems.size());

        List<Task> pagedList = new ArrayList<>();
        if (start < allItems.size()) {
            pagedList = allItems.subList(start, end);
        }
    //Convert Entity objects to DTOs to keep the API layer clean
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
     //Wrap data with metadata (totals, pages) for the frontend pagination UI
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("tasks", taskDTOList);
        responseData.put("totalPages", (int) Math.ceil((double) allItems.size() / pageable.getPageSize()));
        responseData.put("totalElements", allItems.size());
        responseData.put("currentPage", pageable.getPageNumber());

        return responseData;
    }

    //update the task status
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

    //update task details
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

    //delete task
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
package com.example.NodeZS_Backend.Service;

import com.example.NodeZS_Backend.DTO.TaskDTO;
import com.example.NodeZS_Backend.Entity.Task;
import com.example.NodeZS_Backend.Enum.Status;
import com.example.NodeZS_Backend.Repository.TaskRepository;
import com.example.NodeZS_Backend.Util.VarList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public String saveTask(TaskDTO taskDTO) {
        try {
            Task task = new Task();
            task.setTitle(taskDTO.getTitle()); // Required [cite: 31, 56]
            task.setDescription(taskDTO.getDescription()); // Optional [cite: 31, 56]
            task.setDueDate(taskDTO.getDueDate()); // Optional [cite: 31, 56]
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
            // Log the error to your console to see exactly what failed
            e.printStackTrace();
            return VarList.RSP_ERROR;
        }
    }
}
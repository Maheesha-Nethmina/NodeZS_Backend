package com.example.NodeZS_Backend.DTO;

import com.example.NodeZS_Backend.Enum.Priority;
import com.example.NodeZS_Backend.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private int taskid;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDateTime dueDate;
    private String assigneeEmail;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
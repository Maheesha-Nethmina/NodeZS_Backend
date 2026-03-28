package com.example.NodeZS_Backend.Entity;

import com.example.NodeZS_Backend.Enum.Priority;
import com.example.NodeZS_Backend.Enum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskid;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING) // Enum: TODO | IN_PROGRESS | DONE
    private Status status = Status.TODO;
    @Enumerated(EnumType.STRING) // Enum: LOW | MEDIUM | HIGH
    private Priority priority;

    private LocalDateTime dueDate;
    @Column(name = "assignee_email", nullable = true) // Explicitly allow null [cite: 56]
    private String assigneeEmail;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private int userId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Requirement 3.3 auto-set
    }
}
package com.example.NodeZS_Backend.Repository;

import com.example.NodeZS_Backend.Entity.Task;
import com.example.NodeZS_Backend.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // --- Pageable Methods (Used when DB-level sorting is enough) ---
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByUserId(int userId, Pageable pageable);
    Page<Task> findByAssigneeEmail(String email, Pageable pageable);

    // --- List Methods (CRITICAL: Used for manual sorting in TaskService) ---

    // Fixed the error: This allows calling findByStatus with only 1 argument
    List<Task> findByStatus(Status status);

    // Used for fetching tasks created by a specific user for manual sorting
    List<Task> findByUserId(int userId);

    // Used for fetching assigned tasks for the Selection page manual sorting
    List<Task> findByAssigneeEmail(String email);
}
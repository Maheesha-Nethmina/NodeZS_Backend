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

    /**
     * Requirement: Ability to filter by status (To Do, In Progress, Done).
     * This method supports both the Status filter and Pagination/Sorting.
     * * @param status The status to filter by (Enum)
     * @param pageable Contains page number, size, and Sort object (due date or priority)
     * @return A Page of Tasks
     */
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByUserId(int userId, Pageable pageable);
    Page<Task> findByAssigneeEmail(String email, Pageable pageable);
    List<Task> findByAssigneeEmail(String email);
}
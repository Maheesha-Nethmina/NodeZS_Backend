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

    //allows calling findByStatus with only 1 argument
    List<Task> findByStatus(Status status);
    //fetching tasks created by a specific user for manual sorting
    List<Task> findByUserId(int userId);
    //fetching assigned tasks for the Selection page manual sorting
    List<Task> findByAssigneeEmail(String email);
}
package com.example.NodeZS_Backend.Repository;

import com.example.NodeZS_Backend.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    // Integer matches your 'int taskId' requirement
}
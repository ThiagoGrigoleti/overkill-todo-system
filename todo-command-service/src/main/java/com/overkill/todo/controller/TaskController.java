package com.overkill.todo.controller;

import com.overkill.todo.dto.TaskDTO;
import com.overkill.todo.service.TaskProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskProducer taskProducer;

    public TaskController(TaskProducer taskProducer) {
        this.taskProducer = taskProducer;
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody TaskDTO taskDTO) {
        taskProducer.sendToQueue(taskDTO);
        
        return ResponseEntity.accepted()
                .body("task recebida com sucesso e está sendo processada");
    }
}
package com.overkill.todo.service;

import com.overkill.todo.config.RabbitMQConfig;
import com.overkill.todo.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {

    private static final Logger log = LoggerFactory.getLogger(TaskProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public TaskProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToQueue(TaskDTO task) {
        log.info("mandando tarefa pra fila: {}", task.getTitle());

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME, 
            RabbitMQConfig.ROUTING_KEY, 
            task
        );
        
        log.info("tarefa enviada com sucesso");
    }
}
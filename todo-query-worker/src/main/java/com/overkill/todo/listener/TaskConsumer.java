package com.overkill.todo.listener;

import com.overkill.todo.model.TaskEntity;
import com.overkill.todo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class TaskConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskConsumer.class);

    private final TaskRepository taskRepository;
    private final StringRedisTemplate redisTemplate;

    public TaskConsumer(TaskRepository taskRepository, StringRedisTemplate redisTemplate) {
        this.taskRepository = taskRepository;
        this.redisTemplate = redisTemplate;
    }

    @RabbitListener(queues = "task.created.queue")
    public void consumeTask(Map<String, String> taskMessage) {
        log.info("Recebendo tarefa: {}", taskMessage);

        try {
            Thread.sleep(1000);

            TaskEntity entity = new TaskEntity();
            entity.setTitle(taskMessage.get("title"));
            entity.setDescription(taskMessage.get("description"));
            entity.setStatus("COMPLEX_PROCESSING_DONE");
            entity.setProcessedAt(LocalDateTime.now());

            TaskEntity savedTask = taskRepository.save(entity);
            log.info("Tarefa salva no Banco com ID: {}", savedTask.getId());

            String redisKey = "task:" + savedTask.getId();
            redisTemplate.opsForValue().set(redisKey, savedTask.getTitle());
            log.info("Cache atualizado no Redis");

        } catch (InterruptedException e) {
            log.error("Erro no processamento", e);
            Thread.currentThread().interrupt();
        }
    }
}
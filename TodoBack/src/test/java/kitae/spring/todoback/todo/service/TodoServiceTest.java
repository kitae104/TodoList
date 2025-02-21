package kitae.spring.todoback.todo.service;

import kitae.spring.todoback.todo.dto.TodoDto;
import kitae.spring.todoback.todo.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    public void makeTodoList(){
        UUID uuid = UUID.randomUUID();
        for (int i = 0; i < 30; i++) {
            TodoDto todoDto = TodoDto.builder()
                    .code(uuid.toString())
                    .name("오늘 할 일" + i)
                    .seq(i)
                    .status(false)
                    .build();
            todoService.addTodo(todoDto);
        }
    }

    @Test
    void getTodoList() {
        makeTodoList();
    }
}
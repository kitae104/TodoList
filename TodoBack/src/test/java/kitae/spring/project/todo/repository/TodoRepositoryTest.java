package kitae.spring.project.todo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void completeAllTest() {
        int result = todoRepository.completeAll();
        System.out.println("result = " + result);
    }
}
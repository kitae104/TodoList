package kitae.spring.todoback.todo.controller;

import kitae.spring.todoback.todo.dto.TodoDto;
import kitae.spring.todoback.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("")
    public ResponseEntity<?> getTodosList(
        @RequestParam(defaultValue = "0") int page, // 현재 페이지
        @RequestParam(defaultValue = "10") int size // 크기
    ) {
        try {
            Page<TodoDto> todoDtoList = todoService.getTodoList(page, size);
            return new ResponseEntity<>(todoDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable Long id) {
        try {
            TodoDto todoDto = todoService.getTodoById(id);
            return new ResponseEntity<>(todoDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getTodoByCode(@PathVariable String code) {
        try {
            TodoDto todoDto = todoService.getTodoByCode(code);
            return new ResponseEntity<>(todoDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<?> addTodo(@RequestBody TodoDto todoDto) {
        try {
            log.info("=================todoDto = " + todoDto);
            boolean result = todoService.addTodo(todoDto);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateTodo(@RequestBody TodoDto todoDto) {
        log.info("todoDto = " + todoDto);
        try {
            boolean result = todoService.updateTodoById(todoDto);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodoById(@PathVariable Long id) {
        try {
            boolean result = todoService.deleteTodoById(id);
            if (!result) {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

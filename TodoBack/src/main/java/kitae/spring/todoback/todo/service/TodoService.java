package kitae.spring.todoback.todo.service;

import kitae.spring.todoback.todo.dto.TodoDto;
import kitae.spring.todoback.todo.entity.Todo;
import kitae.spring.todoback.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    // 할일 목록 조회
    public Page<TodoDto> getTodoList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // pageable 객체 생성
        Page<Todo> todoListPage = todoRepository.findAllByOrderByStatusAscSeqAsc(pageable);
        Page<TodoDto> todoDtoListPage = todoListPage.map(todo -> modelMapper.map(todo, TodoDto.class));
        System.out.println("todoDtoListPage = " + todoDtoListPage);
        return todoDtoListPage;
    }

    // 할일 상세 조회
    public TodoDto getTodoById(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 할일이 없습니다."));
        TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
        return todoDto;
    }

    // 할일 코드 상세 조회
    public TodoDto getTodoByCode(String code) {
        Todo todo = todoRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("해당 할일이 없습니다."));
        TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
        return todoDto;
    }

    // 할일 추가
    public boolean addTodo(TodoDto todoDto) {
        Todo todo = Todo.builder()
                .code(todoDto.getCode())
                .name(todoDto.getName())
                .status(todoDto.getStatus())
                .seq(todoDto.getSeq())
                .build();
        Todo savedTodo = todoRepository.save(todo);
        log.info("savedTodo = " + savedTodo);
        if(savedTodo != null) {
            return true;
        } else {
            return false;
        }
    }

    // 할일 수정
    public boolean updateTodoById(TodoDto todoDto) {
        Todo todo = todoRepository.findById(todoDto.getId()).orElseThrow(() -> new IllegalArgumentException("해당 할일이 없습니다."));
        todo.setCode(todoDto.getCode());
        todo.setName(todoDto.getName());
        todo.setStatus(todoDto.getStatus());
        todo.setSeq(todoDto.getSeq());
        Todo updatedTodo = todoRepository.save(todo);
        log.info("updatedTodo = " + updatedTodo);
        if(updatedTodo != null) {
            return true;
        } else {
            return false;
        }
    }

    // 할일 삭제
    public boolean deleteTodoById(Long id) {
        todoRepository.deleteById(id);
        return true;
    }
}
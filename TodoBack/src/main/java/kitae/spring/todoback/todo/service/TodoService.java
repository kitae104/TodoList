package kitae.spring.todoback.todo.service;

import kitae.spring.todoback.todo.dto.TodoDto;
import kitae.spring.todoback.todo.entity.Todo;
import kitae.spring.todoback.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    // 할일 목록 조회
    public Page<TodoDto> getTodoList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // pageable 객체 생성
        Page<Todo> todoListPage = todoRepository.findAll(pageable);
        Page<TodoDto> todoDtoListPage = todoListPage.map(todo -> modelMapper.map(todo, TodoDto.class));

        // status, seq 순으로 정렬
        List<TodoDto> sortedList = todoDtoListPage.getContent().stream()
                .sorted(Comparator.comparing(TodoDto::getStatus)  // status 기준 오름차순
                        .thenComparing(TodoDto::getSeq)) // seq 기준 오름차순
                .collect(Collectors.toList());

        // 새로운 Page 객체 반환 (페이지 정보 유지)
        return new PageImpl<>(sortedList, todoDtoListPage.getPageable(), todoDtoListPage.getTotalElements());
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
        UUID uuid = UUID.randomUUID();
        Todo todo = Todo.builder()
                .code(uuid.toString())
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
//        log.info("변경전 todo = " + todo);
//        todo.setCode(todoDto.getCode());
//        todo.setName(todoDto.getName());
        todo.setStatus(todoDto.getStatus());
//        todo.setSeq(todoDto.getSeq());
        Todo updatedTodo = todoRepository.save(todo);
//        log.info("변경 후 updatedTodo = " + updatedTodo);
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

    // 모든 할일 완료
    public boolean completeAll() {
        int result = todoRepository.completeAll();
        if(result == 0) {
            return false;
        } else {
            return true;
        }
    }

    // 모든 할 일 삭제
    public boolean deleteAll() {
        todoRepository.deleteAll();
        return true;
    }
}
package kitae.spring.project.board.controller;

import kitae.spring.project.board.dto.BoardDto;
import kitae.spring.project.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;

  // 게시판 리스트 조회
  @GetMapping("")
  public ResponseEntity<?> getTodosList(
          @RequestParam(defaultValue = "0") int page, // 현재 페이지
          @RequestParam(defaultValue = "10") int size // 크기
  ) {
    try {
      Page<BoardDto> boardDtoList = boardService.getBoardList(page, size);
      return new ResponseEntity<>(boardDtoList, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 게시판 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> getTodoById(@PathVariable Long id) {
    try {
      BoardDto boardDto = boardService.getBoardById(id);
      return new ResponseEntity<>(boardDto, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 게시판 추가
  @PostMapping()
  public ResponseEntity<?> addTodo(@RequestBody BoardDto boardDto) {
    try {
      boolean result = boardService.addBoard(boardDto);
      if (result) {
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateTodo(@RequestBody BoardDto boardDto) {
    log.info("BoardDto = " + boardDto);
    try {
      Long id = boardDto.getId();
      boolean result = false;

      if(id == null) {
        throw new IllegalArgumentException("id is null");
      } else {
        log.info("updateTodoById");
        result = boardService.updateBoardById(boardDto);
      }
      if (result) {
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping({"/{id}", ""})
  public ResponseEntity<?> deleteTodoById(@PathVariable(value="id", required = false) Long id) {
    try {
      boolean result = false;
      if(id == null) {
        log.info("deleteAll");
        result = boardService.deleteAll();
      } else {
        log.info("deleteTodoById");
        result = boardService.deleteBoardById(id);
      }
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

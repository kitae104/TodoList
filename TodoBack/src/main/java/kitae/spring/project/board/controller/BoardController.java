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
}

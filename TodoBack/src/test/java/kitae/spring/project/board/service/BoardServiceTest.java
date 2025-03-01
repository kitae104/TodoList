package kitae.spring.project.board.service;

import kitae.spring.project.board.dto.BoardDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardServiceTest {

  @Autowired
  private BoardService boardService;

  public void makeBoardList(){
    for (int i = 1; i < 23; i++) {
      BoardDto boardDto = BoardDto.builder()
              .title("제목" + i)
              .content("내용" + i)
              .writer("작성자" + i)
              .build();
      boardService.insertBoard(boardDto);
    }
  }

  @Test
  void getBoardList() {
    makeBoardList();
  }

}
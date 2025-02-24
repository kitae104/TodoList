package kitae.spring.project.board.service;

import kitae.spring.project.board.dto.BoardDto;
import kitae.spring.project.board.entity.Board;
import kitae.spring.project.board.repository.BoardRepository;
import kitae.spring.project.todo.dto.TodoDto;
import kitae.spring.project.todo.entity.Todo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final ModelMapper modelMapper;

  public Page<BoardDto> getBoardList(int page, int size) {
    Pageable pageable = PageRequest.of(page, size); // pageable 객체 생성
    Page<Board> boardListPage = boardRepository.findAll(pageable);
    Page<BoardDto> boardDtoListPage = boardListPage.map(board -> modelMapper.map(board, BoardDto.class));

    // status, seq 순으로 정렬
    List<BoardDto> sortedList = boardDtoListPage.getContent().stream()
            .sorted(Comparator.comparing(BoardDto::getId))  // status 기준 오름차순
            //.thenComparing(BoardDto::getSeq)) // seq 기준 오름차순
            .collect(Collectors.toList());

    // 새로운 Page 객체 반환 (페이지 정보 유지)
    return new PageImpl<>(sortedList, boardDtoListPage.getPageable(), boardDtoListPage.getTotalElements());
  }

  public boolean addBoard(BoardDto boardDto) {
    Board board = modelMapper.map(boardDto, Board.class);
    Board savedBoard = boardRepository.save(board);
    if(savedBoard == null) {
      return false;
    } else {
      return true;
    }
  }
}

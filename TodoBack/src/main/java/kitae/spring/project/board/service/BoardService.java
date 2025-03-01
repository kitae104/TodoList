package kitae.spring.project.board.service;

import kitae.spring.project.board.dto.BoardDto;
import kitae.spring.project.board.entity.Board;
import kitae.spring.project.board.repository.BoardRepository;
import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.entity.FileType;
import kitae.spring.project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final ModelMapper modelMapper;
  private final FileService fileService;

  public Page<BoardDto> getBoardList(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("regTime").descending()); // pageable 객체 생성
    Page<Board> boardListPage = boardRepository.findAll(pageable);
    Page<BoardDto> boardDtoListPage = boardListPage.map(board -> modelMapper.map(board, BoardDto.class));

    // status, seq 순으로 정렬
    List<BoardDto> sortedList = boardDtoListPage.getContent().stream()
//            .sorted(Comparator.comparing(BoardDto::getId))  // status 기준 오름차순
//            .thenComparing(BoardDto::getSeq)) // seq 기준 오름차순
            .collect(Collectors.toList());

    // 새로운 Page 객체 반환 (페이지 정보 유지)
    return new PageImpl<>(sortedList, boardDtoListPage.getPageable(), boardDtoListPage.getTotalElements());
  }

  @Transactional
  public boolean insertBoard(BoardDto boardDto) {
    Board board = modelMapper.map(boardDto, Board.class);
    Board savedBoard = boardRepository.save(board);
//    log.info("savedBoard: " + savedBoard);
    if(savedBoard == null) {
      return false;
    } else {
      String parentTable = "board";
      Long parentNo = savedBoard.getId();

      List<FileDto> uploadFileList = new ArrayList();

      MultipartFile mainFile = boardDto.getMainFile();
      if(mainFile != null && !mainFile.isEmpty()) {
        FileDto mainFileDto = FileDto.builder()
                .parentTable(parentTable)
                .parentNo(parentNo)
                .type("MAIN")
                .file(mainFile)
                .seq(0L)
                .build();
        uploadFileList.add(mainFileDto);
      }

      List<MultipartFile> fileList = boardDto.getFileList();
      if(fileList != null && !fileList.isEmpty()) {
        for(MultipartFile file : fileList) {
          if(file.isEmpty()) {
            continue;
          }
          FileDto fileDto = FileDto.builder()
                  .parentTable(parentTable)
                  .parentNo(parentNo)
                  .type("SUB")
                  .file(file)
                  .seq(0L)
                  .build();
          uploadFileList.add(fileDto);
        }
      }
      int result = 0;
//      log.info("uploadFileList: " + uploadFileList);
      try {
        result += fileService.fileUpload(uploadFileList);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return result > 0;
    }
  }

  public BoardDto getBoardById(Long id) {
    Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
    BoardDto boardDto = modelMapper.map(board, BoardDto.class);
    return boardDto;
  }

  public boolean updateBoardById(BoardDto boardDto) {
    Board board = boardRepository.findById(boardDto.getId()).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
    board.setTitle(boardDto.getTitle());
    board.setWriter(boardDto.getWriter());
    board.setContent(boardDto.getContent());
    Board updatedBoard = boardRepository.save(board);
    if(updatedBoard == null) {
      return false;
    } else {
      return true;
    }
  }

  public boolean deleteAll() {
    boardRepository.deleteAll();
    return true;
  }

  public boolean deleteBoardById(Long id) {
    boardRepository.deleteById(id);
    return true;
  }
}

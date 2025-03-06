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
    log.info("boardListPage: " + boardListPage.getSize());
    for (Board board : boardListPage) {
      log.info("board: " + board);
    }
    Page<BoardDto> boardDtoListPage = boardListPage.map(board -> modelMapper.map(board, BoardDto.class));

    // status, seq 순으로 정렬
    List<BoardDto> sortedList = boardDtoListPage.getContent().stream()
//            .sorted(Comparator.comparing(BoardDto::getId))  // status 기준 오름차순
//            .thenComparing(BoardDto::getSeq)) // seq 기준 오름차순
            .collect(Collectors.toList());

    for (BoardDto boardDto : sortedList) {
      FileEntity mainFile = fileService.getMainFile(boardDto.getId());
      FileDto mainFileDto = modelMapper.map(mainFile, FileDto.class);
      boardDto.setFile(mainFileDto);
    }

    // 새로운 Page 객체 반환 (페이지 정보 유지)
    return new PageImpl<>(sortedList, boardDtoListPage.getPageable(), boardDtoListPage.getTotalElements());
  }

  @Transactional
  public boolean insertBoard(BoardDto boardDto) {
    // 게시글 저장
    Board board = modelMapper.map(boardDto, Board.class);
    Board savedBoard = boardRepository.save(board);
    if(savedBoard == null) {
      return false;
    } else {
      // 첨부 파일 저장
      int result = fileUpload(boardDto, savedBoard);
      return result > 0;
    }
  }

  /**
   * 파일 업로드 처리
   * @param boardDto
   * @param savedBoard
   * @return
   */
  private int fileUpload(BoardDto boardDto, Board savedBoard) {
    String parentTable = "board";
    Long parentId = savedBoard.getId();

    List<FileDto> uploadFileList = new ArrayList();

    // 메인 파일 저장
    MultipartFile mainFile = boardDto.getMainFile();
    if(mainFile != null && !mainFile.isEmpty()) {
      FileDto mainFileDto = FileDto.builder()
              .parentTable(parentTable)
              .parentId(parentId)
              .type("MAIN")
              .file(mainFile)
              .seq(0L)
              .build();
      uploadFileList.add(mainFileDto);
    }

    // 서브 파일 저장
    List<MultipartFile> fileList = boardDto.getFileList();
    if(fileList != null && !fileList.isEmpty()) {
      for(MultipartFile file : fileList) {
        if(file.isEmpty()) {
          continue;
        }
        FileDto fileDto = FileDto.builder()
                .parentTable(parentTable)
                .parentId(parentId)
                .type("SUB")
                .file(file)
                .seq(0L)
                .build();
        uploadFileList.add(fileDto);
      }
    }
    int result = 0;
    log.info("uploadFileList: " + uploadFileList);
    try {
      result += fileService.fileUpload(uploadFileList);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
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
      // 첨부 파일 저장
      int result = fileUpload(boardDto, updatedBoard);
      return true;
    }
  }

  public boolean deleteAll() {
    boardRepository.deleteAll();
    return true;
  }

  public boolean deleteBoardById(Long id) {
    boardRepository.deleteById(id);

    // 첨부 파일 삭제(부모 테이블과 부모 번호로 파일 목록 삭제)
    FileDto fileDto = FileDto.builder()
            .parentTable("board")
            .parentId(id)
            .build();
    int deletedCount = fileService.deleteByParent(fileDto);
    log.info(deletedCount + "건의 파일 삭제 완료");
    return true;
  }
}

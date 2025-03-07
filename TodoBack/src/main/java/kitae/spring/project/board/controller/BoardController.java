package kitae.spring.project.board.controller;

import kitae.spring.project.board.dto.BoardDto;
import kitae.spring.project.board.service.BoardService;
import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;
  private final FileService fileService;

  /**
   * 게시판 리스트 조회
   * @param page
   * @param size
   * @return
   */
  @GetMapping("")
  public ResponseEntity<?> getBoardList(
          @RequestParam(value = "page", required = false, defaultValue = "0") int page, // 현재 페이지
          @RequestParam(value = "size", required = false, defaultValue = "5") int size // 크기
  ) {
    log.info("getBoardList page : " + page + ", size : " + size);
    try {
      Page<BoardDto> boardDtoList = boardService.getBoardList(page-1, size);
      log.info("boardDtoList: " + boardDtoList);
      return new ResponseEntity<>(boardDtoList, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 게시판 상세 조회
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getBoardById(@PathVariable Long id) {
    try {
      BoardDto boardDto = boardService.getBoardById(id);

      // 파일 리스트 조회
      FileDto fileDto = new FileDto();
      fileDto.setParentTable("board");
      fileDto.setParentId(boardDto.getId());
      List<FileDto> fileList = fileService.listByParent(fileDto);
      Map<String, Object> response = new HashMap<>();
      response.put("board", boardDto);
      response.put("fileList", fileList);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /*
   * @RequestBody 붙일 때 안 붙일 때 차이
   * - @RequestBody ⭕ : application/json, application/xml
   * - @RequestBody ❌ : multipart/form-data, applcation/x-www-form-urlencoded
   *
   */
  // 조건부 게시판 추가(application/json) 인 경우
  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> insertBoardJson(@RequestBody BoardDto boardDto) {
    log.info("게시글 등록 - application/json 경우 " + boardDto);
    try {
      boolean result = boardService.insertBoard(boardDto);
      if (result) {
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 조건부 게시판 추가(multipart/form-data) 인 경우
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> insertBoardFormData(BoardDto boardDto) {
    log.info("게시글 등록 - multipart/form-data 경우 " + boardDto);
    try {
      boolean result = boardService.insertBoard(boardDto);
      if (result) {
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping(value ="", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateBoardJson(@RequestBody BoardDto boardDto) {
    log.info("BoardDto = " + boardDto);
    try {
      Long id = boardDto.getId();
      boolean result = false;

      if(id == null) {
        throw new IllegalArgumentException("id is null");
      } else {
        log.info("updateBoardById");
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

  @PutMapping(value ="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateBoardForm(BoardDto boardDto) {
    log.info("BoardDto = " + boardDto);
    try {
      Long id = boardDto.getId();
      boolean result = false;

      if(id == null) {
        throw new IllegalArgumentException("id is null");
      } else {
        log.info("updateBoardById");
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
  public ResponseEntity<?> deleteBoardById(@PathVariable(value="id", required = false) Long id) {
    try {
      boolean result = false;
      if(id == null) {
        log.info("deleteAll");
        result = boardService.deleteAll();
      } else {
        log.info("deleteBoardById");
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

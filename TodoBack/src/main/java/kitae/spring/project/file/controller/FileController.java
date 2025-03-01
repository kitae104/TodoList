package kitae.spring.project.file.controller;

import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/file")
public class FileController {
  private final FileService fileService;
  private final ModelMapper modelMapper;

  @GetMapping("")
  public ResponseEntity<?> getFileLists(
//          @RequestParam(defaultValue = "0") int page, // 현재 페이지
//          @RequestParam(defaultValue = "10") int size // 크기
  ) {
    try {
      List<FileEntity> fileList = fileService.getFileList();
      List<FileDto> fileDtoList = fileList.stream()
              .map(file -> modelMapper.map(file, FileDto.class))
              .toList();
      return new ResponseEntity<>(fileDtoList, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 게시판 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> getFileById(@PathVariable Long id) {
    try {
      FileDto boardDto = fileService.getFileById(id);
      return new ResponseEntity<>(boardDto, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 게시판 추가
  @PostMapping()
  public ResponseEntity<?> addFile(@RequestBody FileDto fileDto) {
    try {
      boolean result = fileService.insertFile(fileDto);
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
  public ResponseEntity<?> updateFileById(@RequestBody FileDto fileDto) {
    log.info("FileDto = " + fileDto);
    try {
      Long id = fileDto.getId();
      boolean result = false;

      if(id == null) {
        throw new IllegalArgumentException("id is null");
      } else {
        log.info("updateFileById");
        result = fileService.updateFileById(fileDto);
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

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteFileById(@PathVariable(value="id", required = false) Long id) {
    try {
      boolean result = false;
      if(id == null) {
        throw new IllegalArgumentException("id is null");
      } else {
        log.info("deleteFileById");
        result = fileService.deleteFileById(id);
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
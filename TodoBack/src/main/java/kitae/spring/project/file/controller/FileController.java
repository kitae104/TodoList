package kitae.spring.project.file.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/file")
public class FileController {
  private final FileService fileService;
  private final ModelMapper modelMapper;
  private final ResourceLoader resourceLoader;

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

  @DeleteMapping("")
  public ResponseEntity<?> deleteFilesByIdList(@RequestBody List<Long> idList) {
    log.info("idList = " + idList);
    try {
      boolean result = false;
      if(idList == null || idList.isEmpty()) {
        throw new IllegalArgumentException("idList가 null 이거나 비어있습니다.");
      } else {
        result = fileService.deleteFilesByIdList(idList);
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

  // 파일 다운로드
  @GetMapping("/download/{id}")
  public void fileDownload(@PathVariable("id") Long id, HttpServletResponse response) {
    try {
      fileService.fileDownload(id, response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // 썸 네일
  @GetMapping("/img/{id}")
  public void thumbnailImg(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    FileDto fileDto;
    try {
      fileDto = fileService.getFileById(id); // 예외 처리 때문에 진행 안되는 경우가 있어서 따로 처리 진행
    } catch (Exception e) {
      fileDto = null;
    }
    String filePath = fileDto != null ? fileDto.getFilePath() : null;
    File imgFile;

    // 파일 경로가 null 또는 파일이 존재하지 않는 경우
    Resource resource = resourceLoader.getResource("classpath:static/images/lion.png");
    if(filePath == null || !(imgFile = new File(filePath)).exists()) {
      // no-image.png 적용
      imgFile = resource.getFile();
      filePath = imgFile.getPath();
    }

    String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
    String mineType = MimeTypeUtils.parseMimeType("image/" + ext).toString();
    MediaType mediaType = MediaType.valueOf(mineType);

    if(mediaType == null){
      // 이미지 타입이 아닌 경우
      response.setContentType(MediaType.IMAGE_PNG_VALUE);
      imgFile = resource.getFile();
    } else {
      // 이미지 타입인 경우
      response.setContentType(mediaType.toString());
    }

    FileInputStream fis = new FileInputStream(imgFile);
    ServletOutputStream sos = response.getOutputStream();
    FileCopyUtils.copy(fis, sos);
    fis.close();
    sos.close();
  }
}
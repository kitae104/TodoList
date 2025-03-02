package kitae.spring.project.file.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.entity.FileType;
import kitae.spring.project.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

  @Value("${uploadPath}")
  private String uploadPath;

  private final FileRepository fileRepository;
  private final ModelMapper modelMapper;

  public List<FileEntity> getFileList() {

    return fileRepository.findAll(Sort.by(Sort.Direction.DESC, "regTime"));
  }

  public boolean insertFile(FileDto fileDto) {
    FileEntity file = modelMapper.map(fileDto, FileEntity.class);
    FileEntity savedFile = fileRepository.save(file);
    if(savedFile == null) {
      return false;
    } else {
      return true;
    }
  }

  public FileDto getFileById(Long id) {
    FileEntity file = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));
    FileDto fileDto = modelMapper.map(file, FileDto.class);
    return fileDto;
  }

  public boolean updateFileById(FileDto fileDto) {
    FileEntity file = fileRepository.findById(fileDto.getId()).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + fileDto.getId()));
    file = modelMapper.map(fileDto, FileEntity.class);
    FileEntity savedFile = fileRepository.save(file);
    if (savedFile == null) {
      return false;
    } else {
      return true;
    }
  }

  public boolean deleteFileById(Long id) {
    // 파일 삭제
    if(!deleteFile(id)){
      return false;
    }
    // DB 삭제
    fileRepository.deleteById(id);
    return true;
  }

  // 해당 파일 지우기
  private boolean deleteFile(Long id) {
    FileDto fileDto = getFileById(id);
    String filePath = fileDto.getFilePath();
    File deleteFile = new File(filePath);
    if(!deleteFile.exists()) {
      log.info("파일이 존재하지 않습니다. filePath = " + filePath);
      return false;
    }
    boolean deleted = deleteFile.delete();
    if(!deleted) {
      log.info("파일 삭제 실패. filePath = " + filePath);
      return false;
    } else {
      log.info("파일 삭제 성공. filePath = " + filePath);
      return true;
    }
  }

  // 부모 테이블과 부모 번호로 파일 목록 조회
  public List<FileDto> listByParent(FileDto fileDto) {
    List<FileEntity> fileEntityList = fileRepository.findByParent(fileDto.getParentTable(), fileDto.getParentNo());
    List<FileDto> fileDtoList = fileEntityList.stream()
            .map(fileEntity -> modelMapper.map(fileEntity, FileDto.class))
            .toList();
    return fileDtoList;
  }

  // 부모 테이블과 부모 번호로 파일 목록 삭제
  public int deleteByParent(FileDto fileDto){
    List<FileDto> fileDtoList = listByParent(fileDto);

    // 파일 삭제
    for(FileDto file : fileDtoList) {
      deleteFile(file.getId());
    }
    // DB 삭제
    return fileRepository.deleteByParent(fileDto.getParentTable(), fileDto.getParentNo());
  }

  // 단일 파일 업로드
  public int uploadFile(FileDto fileDto) throws IOException {

    int result = 0;
    MultipartFile file = fileDto.getFile();
    if(file.isEmpty()){
      return result;
    }

    // 파일 시스템에 등록
    // 파일 정보 : 원본파일명, 파일 용량, 파일 데이터, 파일명, 파일경로
    String originName = file.getOriginalFilename();
    long fileSize = file.getSize();
    byte[] fileData = file.getBytes();
    String newFileName = UUID.randomUUID().toString() + "_" + originName;
    String filePath = uploadPath + "/" + newFileName;
//    log.info("filePath = " + filePath);
    File uploadFile = new File(filePath);
    FileCopyUtils.copy(fileData, uploadFile); // 파일 복사

    // DB에 등록
    FileEntity fileEntity = FileEntity.builder()
            .parentTable(fileDto.getParentTable())
            .parentNo(fileDto.getParentNo())
            .type(fileDto.getType() == "MAIN" ? FileType.MAIN : FileType.SUB)
            .fileName(newFileName)
            .originName(originName)
            .filePath(filePath)
            .fileSize(fileSize)
            .seq(fileDto.getSeq())
            .build();
//    log.info("fileEntity = " + fileEntity);
    FileEntity savedFile = null;
    try{
      savedFile = fileRepository.save(fileEntity);
//      log.info("savedFile = " + savedFile);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    if(savedFile != null) {
      return 1;
    } else {
      return 0;
    }
  }

  // 다중 파일 업로드
  public int fileUpload(List<FileDto> fileDtoList) throws IOException {
    int result = 0;
    if( fileDtoList == null || fileDtoList.isEmpty()) {
      return result;
    }

    for(FileDto fileDto : fileDtoList) {
      result += uploadFile(fileDto);
    }
    return result;
  }

  // 파일 다운로드
  public int fileDownload(Long id, HttpServletResponse response) throws IOException {
    FileEntity fileEntity = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));

    // 파일 입력
    String filePath = fileEntity.getFilePath();
    String originName = fileEntity.getOriginName();
    File downloadFile = new File(filePath);
    FileInputStream fis = new FileInputStream(downloadFile);
    // 파일 출력
    ServletOutputStream sos = response.getOutputStream();

    // 파일 다운로드 응답 헤더 세팅
    // - Content-Type           : applcation/octet-stream
    // - Content-Disposition    : attachment; filename="파일명.확장자"
    // 한글 파일명 인코딩(한글 파일 다운로드 시 깨짐 방지)
    originName = URLEncoder.encode(originName, "UTF-8");
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + originName + "\"");

    // 다운로드
    int result = FileCopyUtils.copy(fis, sos);

    fis.close();
    sos.close();
    return result;
  }

  // 여러 파일 지우기
  public boolean deleteFilesByIdList(List<Long> idList) {
    if(idList == null || idList.isEmpty()) {
      return false;
    }
    // 파일 삭제
    for (Long id : idList) {
      deleteFile(id);
    }
    // DB 삭제
    return fileRepository.deleteFilesById(idList) > 0;
  }
}

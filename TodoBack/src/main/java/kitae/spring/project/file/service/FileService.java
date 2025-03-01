package kitae.spring.project.file.service;

import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.entity.FileType;
import kitae.spring.project.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    fileRepository.deleteById(id);
    return true;
  }

  // 부모 테이블과 부모 번호로 파일 목록 조회
  public List<FileEntity> listByParent(FileEntity file) {
    return fileRepository.findByParent(file.getParentTable(), file.getParentNo());
  }

  // 부모 테이블과 부모 번호로 파일 목록 삭제
  public int deleteByParent(FileEntity file){
    return fileRepository.deleteByParent(file.getParentTable(), file.getParentNo());
  }

  // 파일 업로드
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
            .build();
    result = fileRepository.save(fileEntity) == null ? 0 : 1;
    return result;
  }
}

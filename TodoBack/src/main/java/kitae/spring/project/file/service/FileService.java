package kitae.spring.project.file.service;

import kitae.spring.project.file.dto.FileDto;
import kitae.spring.project.file.entity.File;
import kitae.spring.project.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;
  private final ModelMapper modelMapper;

  public List<File> getFileList() {

    return fileRepository.findAll(Sort.by(Sort.Direction.DESC, "regTime"));
  }

  public boolean insertFile(FileDto fileDto) {
    File file = modelMapper.map(fileDto, File.class);
    File savedFile = fileRepository.save(file);
    if(savedFile == null) {
      return false;
    } else {
      return true;
    }
  }

  public FileDto getFileById(Long id) {
    File file = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));
    FileDto fileDto = modelMapper.map(file, FileDto.class);
    return fileDto;
  }

  public boolean updateFileById(FileDto fileDto) {
    File file = fileRepository.findById(fileDto.getId()).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + fileDto.getId()));
    file = modelMapper.map(fileDto, File.class);
    File savedFile = fileRepository.save(file);
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

  public List<File> listByParent(File file) {
    return fileRepository.findByParent(file.getParentTable(), file.getParentNo());
  }

  public int deleteByParent(File file){
    return fileRepository.deleteByParent(file.getParentTable(), file.getParentNo());
  }
}

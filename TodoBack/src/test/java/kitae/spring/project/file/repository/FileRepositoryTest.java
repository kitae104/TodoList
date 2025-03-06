package kitae.spring.project.file.repository;

import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.entity.FileType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FileRepositoryTest {

  @Autowired
  private FileRepository fileRepository;

  @Test
  void selectMainFile() {
    FileEntity fileEntity = fileRepository.selectMainFile(1L);
    System.out.println(fileEntity);
  }

  @Test
  void findByIdWithFile() {
    fileRepository.findByIdWithFile(1L).forEach(System.out::println);
  }

  @Test
  void selectByType() {
    FileEntity fileEntity = fileRepository.selectByType("board", 1L, FileType.MAIN);
    System.out.println("selectByType : " + fileEntity);
  }

  @Test
  void listByType() {
    fileRepository.listByType("board", 1L, FileType.SUB).forEach(System.out::println);
  }
}
package dev.hyunlab.hyunlib.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import dev.hyunlab.hyunlib.dto.AtchmnflDto;

public interface AtchmnflService {

    Optional<AtchmnflDto> findAtchmnfl(String atchmnflId);

    List<AtchmnflDto> findAllByAtchmnflGroupId(String atchmnflGroupId);

    /**
     * atchmnflGroupId 필수
     * 
     * @param atchmnflDto
     * @return atchmnflId
     */
    String createAtchmnfl(AtchmnflDto atchmnflDto);

    /**
     * 
     * @param paths
     * @param originalFilenames
     * @return atchmnflGroupId
     * @throws Exception
     */
    String uploadPaths(List<Path> paths, List<String> originalFilenames) throws Exception;

    boolean deleteAtchmnfl(String atchmnflId, String updaterId);

    String findUploadFilePath();

    String createFilePath();

    Path getRootPath();

    Path getPath(AtchmnflDto dto);

    /**
     * 파일 경로 반환, 파일이 존재하지 않으면 null 반환
     * 
     * @param atchmnflGroupId
     * @param index           0부터 시작, 범위를 벗어나면 null 반환
     * @return
     */
    Path getPath(String atchmnflGroupId, int index);

    /**
     * 파일까지 강제 삭제
     * 
     * @param atchmnflGroupId
     */
    void forceDeletesByAtchmnflGroupId(String atchmnflGroupId);

}

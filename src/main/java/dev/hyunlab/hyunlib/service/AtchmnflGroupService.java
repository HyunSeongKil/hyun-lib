
package dev.hyunlab.hyunlib.service;

import dev.hyunlab.hyunlib.dto.AtchmnflGroupDto;

import java.util.Optional;

public interface AtchmnflGroupService {

    Optional<AtchmnflGroupDto> findAtchmnflGroup(String atchmnflGroupId);

    String createAtchmnflGroup(AtchmnflGroupDto atchmnflGroupDto);

    boolean deleteAtchmnflGroup(String atchmnflGroupId, String updaterId);
}

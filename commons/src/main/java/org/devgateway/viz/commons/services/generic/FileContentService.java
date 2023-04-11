package org.devgateway.viz.commons.services.generic;


import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.repositories.FileContentRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;

@Service
public class FileContentService {

    private final FileContentRepository fileContentRepository;

    public FileContentService(FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    public FileContent saveFile(String name, String contentType, byte[] content) throws IOException {
        FileContent fileContent = new FileContent(name, contentType, content);
        fileContent.setCreatedDate(ZonedDateTime.now());
        return fileContentRepository.save(fileContent);
    }

}

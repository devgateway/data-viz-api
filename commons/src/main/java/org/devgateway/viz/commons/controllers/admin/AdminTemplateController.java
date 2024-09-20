package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.services.generic.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/template", produces = APPLICATION_JSON_VALUE)
public class AdminTemplateController extends Controller {

    @Autowired
    private Environment env;

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM)
    public byte[] downloadTemplateFile() throws IOException {
        String templateDir = env.getProperty("tcdi.import.directory");
        Path path = Paths.get(templateDir, CommonConstants.TEMPLATE_FILE_NAME);

        File file = path.toFile();
        if (file.exists()) {
            return Files.readAllBytes(path);
        }

        return "Template file not found. Please contact the administrator".getBytes();
    }

}
package org.devgateway.viz.commons.io;

import org.apache.commons.lang.StringUtils;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.services.CategoryService;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Transactional
public abstract class BaseImport<T, R> {
    @Value("${viz.import.directory}")
    String importDirectory;
    protected boolean shouldRunImport = true;
    @Autowired
    private DatasetService datasetService;

    protected void beforeStart() {
        clean();

    }


    public abstract T read(R row);


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected CategoryService categoryService;

    @PersistenceContext
    protected EntityManager entityManager;

    protected Boolean toBoolean(final String val) {
        if (StringUtils.isBlank(val)) {
            return null;
        }

        return "y".equalsIgnoreCase(val) || "yes".equalsIgnoreCase(val) || "true".equalsIgnoreCase(val) ||
                "1".equalsIgnoreCase(val);
    }

    protected Integer toInteger(final Object value) {

        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        if (value instanceof String) {
            if (!((String) value).isEmpty()) {
                return Integer.parseInt((String) value);
            } else {
                return null;
            }
        }
        return null;
    }

    protected Long toLong(final Object value) {

        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Double) {
            return ((Double) value).longValue();
        }

        if (value instanceof Long) {
            return ((Long) value);
        }
        if (value instanceof String) {
            if (!((String) value).isEmpty()) {
                return Long.parseLong((String) value);
            } else {
                return null;
            }
        }
        return null;
    }


    public Double toDouble(final Object value) {
        if (value != null) {

            if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            }
            if (value instanceof String) {
                if (!((String) value).isEmpty() && !((String) value).equalsIgnoreCase("null")) {
                    return Double.parseDouble((String) value);
                } else {
                    return null;
                }
            }

            if (value instanceof Double) {
                return (Double) value;
            }
        }
        return null;
    }

    protected abstract void init() throws Exception;

    public Stream<Path> getImportFiles() throws Exception {
        Path path = Paths.get(importDirectory);
        return Files.list(path).filter(file -> !file.getFileName().toString().equalsIgnoreCase(CommonConstants.TEMPLATE_FILE_NAME));
    }


    public List<Dataset> createDataSets() throws Exception {
        List<Dataset> datasets = new ArrayList<>();
        getImportFiles().forEach(file -> {
            try {
                UUID uuid = UUID.randomUUID();
                String name = file.getFileName().toString();
                name = name.substring(0, name.lastIndexOf(".")).toUpperCase();
                Dataset ds = datasetService.createDataset(uuid.toString(), name, file.getFileName().toString(), Files.probeContentType(file.toAbsolutePath()), Files.readAllBytes(file.toAbsolutePath()), false);
                datasets.add(ds);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return datasets;
    }

    ;

    public void start(Dataset dataset) throws IOException {
        FileContent f = dataset.getFileContent();
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(f.getBytes())));
        start(in, dataset);
    }

    public void start(BufferedReader in, Dataset dataset) throws IOException {
        throw new RuntimeException("Not implemented");
    }

    protected abstract void clean();


    public void save(T record) {
        logger.info("Saving Entity "+record.toString());
        entityManager.persist(record);
    }

}

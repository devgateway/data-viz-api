package org.devgateway.viz.gateway.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.SupersetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class SupersetDataServiceImpl implements SupersetDataService {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = Logger.getLogger(SupersetDataServiceImpl.class.getName());

    @Value("${viz.superset.schema:public}")
    private String schemaName;

    @Autowired
    public SupersetDataServiceImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean createView(String viewName, Object viewData) {
        try {
            // Convert viewData to a list of maps
            List<Map<String, Object>> dataList = convertToListOfMaps(viewData);
            if (dataList.isEmpty()) {
                logger.warning("No data to create view: " + viewName);
                return false;
            }

            // Create a temporary table to hold the data
            String tempTableName = "temp_" + viewName + "_" + System.currentTimeMillis();
            String createTableSql = generateCreateTableSql(tempTableName, dataList);

            // Execute SQL to create the temporary table
            jdbcTemplate.execute(createTableSql);

            // Insert data into the temporary table
            String insertDataSql = generateInsertDataSql(tempTableName, dataList);
            jdbcTemplate.execute(insertDataSql);

            // Create or replace the view using the temporary table
            String createViewSql = "CREATE OR REPLACE VIEW " + schemaName + "." + viewName + " AS SELECT * FROM " + tempTableName;
            jdbcTemplate.execute(createViewSql);

            // Drop the temporary table
            jdbcTemplate.execute("DROP TABLE " + tempTableName);

            logger.info("Successfully created view: " + viewName);
            return true;
        } catch (Exception e) {
            logger.severe("Error creating view: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createViews(Map<String, Object> views) {
        boolean allSuccess = true;
        for (Map.Entry<String, Object> entry : views.entrySet()) {
            boolean success = createView(entry.getKey(), entry.getValue());
            if (!success) {
                allSuccess = false;
                logger.warning("Failed to create view: " + entry.getKey());
            }
        }
        return allSuccess;
    }

    private List<Map<String, Object>> convertToListOfMaps(Object viewData) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            if (viewData instanceof List) {
                List<?> dataList = (List<?>) viewData;
                for (Object item : dataList) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) item;
                        result.add(map);
                    }
                }
            } else if (viewData instanceof JsonNode) {
                JsonNode jsonNode = (JsonNode) viewData;
                if (jsonNode.isArray()) {
                    for (JsonNode item : jsonNode) {
                        Map<String, Object> map = new HashMap<>();
                        Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> field = fields.next();
                            map.put(field.getKey(), convertJsonNodeToObject(field.getValue()));
                        }
                        result.add(map);
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error converting view data: " + e.getMessage());
        }
        return result;
    }

    private Object convertJsonNodeToObject(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            return node.isInt() ? node.asInt() : node.asDouble();
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isNull()) {
            return null;
        } else {
            return node.toString();
        }
    }

    private String generateCreateTableSql(String tableName, List<Map<String, Object>> dataList) {
        if (dataList.isEmpty()) {
            return "";
        }

        Map<String, Object> firstRow = dataList.get(0);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        boolean first = true;

        for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
            if (!first) {
                sql.append(", ");
            }
            String columnName = entry.getKey();
            Object value = entry.getValue();
            String columnType = getPostgreSqlType(value);

            sql.append("\"")
               .append(columnName)
               .append("\" ")
               .append(columnType);

            first = false;
        }

        sql.append(")");
        return sql.toString();
    }

    private String generateInsertDataSql(String tableName, List<Map<String, Object>> dataList) {
        if (dataList.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (");

        Map<String, Object> firstRow = dataList.get(0);
        boolean first = true;
        for (String columnName : firstRow.keySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("\"").append(columnName).append("\"");
            first = false;
        }

        sql.append(") VALUES ");

        for (int i = 0; i < dataList.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(");

            Map<String, Object> row = dataList.get(i);
            first = true;
            for (Object value : row.values()) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(formatSqlValue(value));
                first = false;
            }

            sql.append(")");
        }

        return sql.toString();
    }

    private String getPostgreSqlType(Object value) {
        if (value == null) {
            return "text"; // default for null (could also return null or throw)
        }

        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return "integer";
        }
        return switch (value) {
            case Long l -> "bigint";
            case Float v -> "real";
            case Double v -> "double precision";
            case BigDecimal bigDecimal -> "numeric";
            case Boolean b -> "boolean";
            case java.sql.Date date -> "date";
            case java.sql.Time time -> "time";
            case java.util.Date date -> "timestamp";
            case byte[] bytes -> "bytea";
            case String s -> "text"; // or "varchar(255)"

            default -> "text";
        };

    }

    private String formatSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String) {
            return "'" + ((String) value).replace("'", "''") + "'";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "'" + value.toString().replace("'", "''") + "'";
        }
    }
}

package org.devgateway.viz.gateway.common;

import java.util.List;

public class Constants {
    public static final Long ROW_LIMIT = Long.MAX_VALUE;

    public static final List<String> SPECIAL_PARAMS = List.of("dvzProxyDatasetId", "row_limit", "apacheSupersetUrl");

    public static final String MEASURE_GROUP_LABEL = "Summary Statistics";

    public static final String DEFAULT_COLOR = "#555";

    public static final List<String> COLORS = List.of(
            "#484848", "#BA4747",  "#FACE58", "#EA901C", "#6EBB6D", "#4D8A24", "#2765A5", "#6E60B3", "#A64C8C", "#D8D8D8");
}

package org.devgateway.viz.gateway.common;

import java.util.List;

public class Constants {
    public static final Long ROW_LIMIT = Long.MAX_VALUE;

    public static final List<String> SPECIAL_PARAMS = List.of("datasetId", "row_limit");

    public static final String MEASURE_GROUP_LABEL = "Summary Statistics";

    public static final String DEFAULT_COLOR = "#555";

    public static final List<String> COLORS = List.of(
            "#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"
    );
}

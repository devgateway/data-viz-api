package org.devgateway.viz.commons.services.generic.delegates;

import org.devgateway.viz.commons.services.generic.StatsDSL;

import java.util.Map;

public abstract class FilteredDelegate extends BaseDelegate {

    protected String filter;

    public FilteredDelegate(String filter, StatsDSL statsDSL) {
        super(statsDSL);
        this.filter = filter;
    }

    /**
     * Check if the delegated measure is a filter, and it has been filtered out
     **/
    protected Boolean isFilteredOut(Map<String, String> filters) {
        return filters.containsKey(filter) && filters.get(filter).equals("false");
    }
}

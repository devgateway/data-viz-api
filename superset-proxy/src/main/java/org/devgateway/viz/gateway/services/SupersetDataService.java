package org.devgateway.viz.gateway.services;

import java.util.Map;

public interface SupersetDataService {
    /**
     * Creates a view in the database with the given name and data
     * @param viewName the name of the view to create
     * @param viewData the data to add to the view
     * @return true if the view was created successfully, false otherwise
     */
    boolean createView(String viewName, Object viewData);

    /**
     * Creates multiple views in the database from a map of view names to view data
     * @param views a map of view names to view data
     * @return true if all views were created successfully, false otherwise
     */
    boolean createViews(Map<String, Object> views);
}

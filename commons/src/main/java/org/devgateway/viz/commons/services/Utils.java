package org.devgateway.viz.commons.services;

public class Utils {

    public static String codify(String label) {
        return label.trim().toUpperCase().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
    }

}

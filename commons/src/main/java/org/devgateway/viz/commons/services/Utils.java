package org.devgateway.viz.commons.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String codify(String label) {
        return label.trim().toUpperCase().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
    }

    public static List parseParams(String value, Class type) {
        String[] strValues = value.split(",");
        return Arrays.stream(strValues).map(s -> {
            try {
                return type.getConstructor(String.class).newInstance(s);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

    }

}

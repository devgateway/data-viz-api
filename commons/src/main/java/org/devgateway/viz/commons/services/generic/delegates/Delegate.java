package org.devgateway.viz.commons.services.generic.delegates;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.devgateway.viz.commons.pojo.Dimension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Delegate {

    Number computeStats(Map<String, String> filters);

    HashMap<String, Number> computeStats(Set<String> keys, Map<String, String> filters, List<Dimension> sub);

    EntityPathBase getEntityPath();

    Class getAnnotatedClass();
}

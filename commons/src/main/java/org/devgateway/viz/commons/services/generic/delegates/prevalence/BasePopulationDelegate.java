package org.devgateway.viz.commons.services.generic.delegates.prevalence;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.services.generic.StatsDSL;
import org.devgateway.viz.commons.services.generic.delegates.FilteredDelegate;

import java.util.*;

/**
 * This is delegate can be used for computing population for CATS and GATS collected stats where Weight can be extrapolated to population
 */
public abstract class BasePopulationDelegate extends FilteredDelegate {

    public BasePopulationDelegate(String filter, StatsDSL statsDSL) {
        super(filter, statsDSL);
    }

    public abstract NumberExpression getWeightSumExpression();

    @Override
    //compute at root level
    /**
     * Prevalence is calculated as total population / smokers
     * */
    public Number computeStats(Map<String, String> filters) {
        if (isFilteredOut(filters)) {
            //if measure is also a filter and it was turned to false
            return 0d;
        } else {
            Map<String, String> prevParams = new HashMap<>();
            prevParams.putAll(filters);
            prevParams.put(filter, "true");
            final Tuple partialTotal = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, getEntityPath(), getAnnotatedClass());
            return partialTotal != null ? partialTotal.get(0, Double.class) : 0d;
        }
    }

    @Override
    //compute at dimension level per dimension key
    public HashMap<String, Number> computeStats(Set<String> keys, Map<String, String> filters, List<Dimension> sub) {
        HashMap<String, Number> results = new HashMap<>();
        if (isFilteredOut(filters)) {
            //if measure is also a filter and it was turned to false
            return results;
        } else {
            Map<String, String> prevParams = new HashMap<>();
            prevParams.putAll(filters);
            //TODO:check what is this about
            prevParams.remove(filter);
            prevParams.put(filter, "true");
            HashMap<String, Tuple> p = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, sub, getEntityPath(), getAnnotatedClass());

            keys.stream().forEach(s -> {
                Tuple val = p.get(s);
                if (val != null) {
                    results.put(s, val.get(sub.size(), Double.class));
                } else {
                    results.put(s, 0);
                }
            });

            return results;
        }
    }

}

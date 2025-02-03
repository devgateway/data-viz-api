package org.devgateway.viz.commons.services.generic.delegates.prevalence;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.services.generic.StatsDSL;
import org.devgateway.viz.commons.services.generic.delegates.FilteredDelegate;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;

public abstract class BasePrevalenceDelegate extends FilteredDelegate {

    public BasePrevalenceDelegate(String filter, StatsDSL statsDSL) {
        super(filter, statsDSL);
    }

    public abstract NumberExpression getWeightSumExpression();

    private Double calculatePercentage(final Double partial, final Double total) {
        double val = BigDecimal.valueOf(partial)
                .divide(BigDecimal.valueOf(total), MathContext.DECIMAL64)
                .scaleByPowerOfTen(2)
                .doubleValue();
        return val;
    }

    /**
     * If the field involved in the measure is also a filter returns filter
     * param name
     *
     */
    @Override
    /**
     * Prevalence is calculated as total population / smokers
     *
     */
    public Number computeStats(Map<String, String> filters) {
        if (isFilteredOut(filters)) {
            //if measure is also a filter and it was turned to false
            return 0d;
        } else {
            Map<String, String> prevParams = new HashMap<>();
            prevParams.putAll(filters);
            // get the totals for a measure without blanks
            prevParams.put(filter, "true,false");
            //compute total of group
            Tuple totalSum = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, getEntityPath(), getAnnotatedClass());
            if (totalSum != null) {
                //get smokers only (filter by current field as true)
                prevParams.put(filter, "true");
                //compute smokers only total
                final Tuple partialTotal = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, getEntityPath(), getAnnotatedClass());
                //return value
                if (partialTotal != null) {
                    //calculate % total / smokers
                    return calculatePercentage(partialTotal.get(0, Double.class), totalSum.get(0, Double.class));
                }
            }

            return 0;
        }
    }

    @Override
    //compute at dimension level per dimension key
    public HashMap<String, Number> computeStats(Set<String> keys, Map<String, String> filters, List<Dimension> sub) {
        HashMap<String, Number> results = new HashMap<>();
        ArrayList<Number> values = new ArrayList<>();
        if (isFilteredOut(filters)) {
            //if measure is also a filter, and it was turned to false
            return results;
        } else {
            Map<String, String> prevParams = new HashMap<>();
            prevParams.putAll(filters);
            // get the totals for a measure without blanks
            prevParams.put(filter, "true,false");
            HashMap<String, Tuple> t = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, sub, getEntityPath(), getAnnotatedClass());
            // get the totals for a measure to calculate the prevalence
            prevParams.put(filter, "true");
            HashMap<String, Tuple> p = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, sub, getEntityPath(), getAnnotatedClass());
            keys.stream().forEach(s -> {
                if (p.get(s) != null && !t.isEmpty()) {
                    Double total = t.get(s).get(sub.size(), Double.class);
                    Double partial = p.get(s).get(sub.size(), Double.class);
                    Double percent = calculatePercentage(partial, total);
                    results.put(s, percent);
                } else {
                    results.put(s, 0);
                }
            });

            return results;
        }
    }

}

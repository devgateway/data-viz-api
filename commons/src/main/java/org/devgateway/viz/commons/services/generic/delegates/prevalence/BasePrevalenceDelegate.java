package org.devgateway.viz.commons.services.generic.delegates.prevalence;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if (partial == null || total == null || total == 0) {
            System.out.println("Warning: Null or zero value encountered in calculatePercentage - returning 0.0");
            return 0.0; // Prevent division by zero or null pointer errors
        }
        return BigDecimal.valueOf(partial)
                .divide(BigDecimal.valueOf(total), MathContext.DECIMAL64)
                .scaleByPowerOfTen(2)
                .doubleValue();
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
            // if measure is also a filter and it was turned to false
            return 0d;
        } else {
            Map<String, String> prevParams = new HashMap<>(filters);
            // get the totals for a measure without blanks
            prevParams.put(filter, "true,false");

            // Compute total of group
            Tuple totalSum = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, getEntityPath(), getAnnotatedClass());

            if (totalSum != null) {
                // Get smokers only (filter by current field as true)
                prevParams.put(filter, "true");
                // Compute smokers only total
                final Tuple partialTotal = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, getEntityPath(), getAnnotatedClass());

                // Ensure non-null values before calculation
                Double total = Optional.ofNullable(totalSum.get(0, Double.class)).orElse(0.0);
                Double partial = Optional.ofNullable(partialTotal != null ? partialTotal.get(0, Double.class) : null).orElse(0.0);

                System.out.println("Computed values -> Partial: " + partial + ", Total: " + total);

                return calculatePercentage(partial, total);
            }

            return 0;
        }
    }

    @Override
    // compute at dimension level per dimension key
    public HashMap<String, Number> computeStats(Set<String> keys, Map<String, String> filters, List<Dimension> sub) {
        HashMap<String, Number> results = new HashMap<>();
        if (isFilteredOut(filters)) {
            // if measure is also a filter, and it was turned to false
            return results;
        } else {
            Map<String, String> prevParams = new HashMap<>(filters);
            // get the totals for a measure without blanks
            prevParams.put(filter, "true,false");

            HashMap<String, Tuple> t = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, sub, getEntityPath(), getAnnotatedClass());

            // get the totals for a measure to calculate the prevalence
            prevParams.put(filter, "true");
            HashMap<String, Tuple> p = statsDSL.computeStats(Arrays.asList(getWeightSumExpression()), prevParams, sub, getEntityPath(), getAnnotatedClass());

            keys.forEach(s -> {
                Double total = Optional.ofNullable(t.get(s))
                        .map(tuple -> tuple.get(sub.size(), Double.class))
                        .orElse(0.0);

                Double partial = Optional.ofNullable(p.get(s))
                        .map(tuple -> tuple.get(sub.size(), Double.class))
                        .orElse(0.0);

                System.out.println("Dimension: " + s + " -> Partial: " + partial + ", Total: " + total);

                Double percent = calculatePercentage(partial, total);
                results.put(s, percent);
            });

            return results;
        }
    }
}

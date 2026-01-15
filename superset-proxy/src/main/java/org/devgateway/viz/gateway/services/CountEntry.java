package org.devgateway.viz.gateway.services;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class CountEntry implements Comparable<CountEntry> {

    private static final Comparator<CountEntry> COMPARATOR =
            Comparator.comparing(CountEntry::getCount);

    private final String key;
    private final int count;

    public CountEntry(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(@NotNull CountEntry other) {
        return COMPARATOR.compare(this, other);
    }
}

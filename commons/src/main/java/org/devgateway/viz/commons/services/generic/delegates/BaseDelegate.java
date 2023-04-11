package org.devgateway.viz.commons.services.generic.delegates;

import org.devgateway.viz.commons.services.generic.StatsDSL;

public abstract class BaseDelegate implements Delegate {

    protected StatsDSL statsDSL;

    public BaseDelegate(StatsDSL statsDSL) {
        this.statsDSL = statsDSL;
    }
}

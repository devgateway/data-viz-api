package org.devgateway.viz.commons.services.generic.delegates;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.Map;
import java.util.Optional;

@Service
public class CachedDelegatedComputation {

    private static final Logger logger = LoggerFactory.getLogger(CachedDelegatedComputation.class);

    @Cacheable("stats")
    /*Wrapped to cache results*/
    public Number computeStats(Delegate delegate, Map<String, String> params) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");
        return delegate.computeStats(params);

    }
}

package nl.bertriksikken.luftdaten.config;

import java.util.ArrayList;
import java.util.Collection;

public final class RenderJobs extends ArrayList<RenderJob> {

    private static final long serialVersionUID = 1L;

    public RenderJobs(Collection<RenderJob> jobs) {
        super(jobs);
    }

    /**
     * Jackson constructor.
     */
    private RenderJobs() {
        // jackson constructor
    }

}

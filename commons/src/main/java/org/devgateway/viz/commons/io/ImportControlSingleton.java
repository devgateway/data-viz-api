package org.devgateway.viz.commons.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportControlSingleton {

    private static final Logger log = LoggerFactory.getLogger(ImportControlSingleton.class);
    private static ImportControlSingleton instance;
    private boolean isImporting = false;
    private double id = 0;

    private ImportControlSingleton() {
        isImporting = false;
    }

    public static ImportControlSingleton getInstance() {
        if (instance == null) {
            instance = new ImportControlSingleton();
        }
        return instance;
    }

    public synchronized boolean isImporting() {
        return isImporting;
    }

    /**
     * Attempt to start importing. If importing is already in progress, return false.
     * Otherwise, set importing to true and return true.
     *
     * @return true if importing is not in progress and was successfully set to true, false otherwise.
     */
    public synchronized boolean attemptToStartImporting(double id) {
        if (isImporting) {
            return false;
        }
        this.isImporting = true;
        this.id = id;
        log.info("Starting import...");
        return true;
    }

    /**
     * Only stop importing if importing is in progress and the id matches the id of the current import.
     *
     * @param id
     */
    public void stopImporting(double id) {
        // TODO: add a timer to automatically stop importing after a certain amount of time.
        if (!isImporting) {
            log.error("Importing is not in progress.");
            return;
        }
        if (this.id != id) {
            log.error("Importing is not in progress for this id.");
            return;
        }
        log.info("Stopping import...");
        this.isImporting = false;
        this.id = 0;
    }
}

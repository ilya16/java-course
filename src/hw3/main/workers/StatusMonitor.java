package hw3.main.workers;

/**
 * Stores the status of the running job.
 */
public class StatusMonitor {

    /** Status of the job */
    private Status status = Status.OK;

    /**
     * Returns the current status.
     * @return  the Status object
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Updates the current status.
     * @param status the status to be set
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
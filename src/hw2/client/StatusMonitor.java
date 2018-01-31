package hw2.client;

/**
 * Stores the status of the running job.
 */
public class StatusMonitor {

    /** Status of the job */
    private Status status = Status.SETTINGS_OFF;

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


/**
 * Possible statuses of the running job.
 */
enum Status {
    SETTINGS_ON,
    SETTINGS_OFF
}
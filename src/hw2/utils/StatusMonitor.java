package hw2.utils;

/**
 * Stores the status of the application.
 */
public class StatusMonitor {

    /** Status of the job */
    private Status status;

    public StatusMonitor(Status status) {
        this.status = status;
    }

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
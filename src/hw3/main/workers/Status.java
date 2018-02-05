package hw3.main.workers;

/**
 * Possible statuses of the running job.
 */
public enum Status {
    OK,
    DUPLICATE_FOUND,
    NONVALID_SYMBOL_FOUND,
    FILE_NOT_FOUND,
    EXCEPTION_THROWN
}

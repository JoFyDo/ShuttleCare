package com.rocketshipcheckingtool.ui.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a task associated with a shuttle and mechanic.
 * Contains information about the task description, status, mechanic, shuttle, and time needed.
 * Provides methods for property access and logging.
 */
public class Task{
    private String task;
    private Boolean status;
    private String mechanic;
    private Shuttle shuttle;
    private String shuttleName;
    private int id;
    private int timeNeeded;

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    /**
     * Constructs a Task object with a Shuttle reference.
     *
     * @param task     the task description
     * @param status   the status of the task (true = in progress, false = finished)
     * @param mechanic the name of the assigned mechanic
     * @param shuttle  the associated Shuttle object
     * @param id       the unique identifier of the task
     */
    public Task(String task, Boolean status, String mechanic, Shuttle shuttle, int id) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttle  = shuttle;
        this.id = id;
    }

    /**
     * Constructs a Task object with shuttle name and time needed.
     *
     * @param task        the task description
     * @param status      the status of the task
     * @param mechanic    the name of the assigned mechanic
     * @param shuttleName the name of the associated shuttle
     * @param id          the unique identifier of the task
     * @param timeNeeded  the time needed for the task
     */
    public Task(String task, Boolean status, String mechanic, String shuttleName, int id, int timeNeeded) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttleName = shuttleName;
        this.id = id;
        this.timeNeeded = timeNeeded;
    }

    /**
     * Constructs a Task object with shuttle name and time needed, without mechanic.
     *
     * @param task        the task description
     * @param status      the status of the task
     * @param id          the unique identifier of the task
     * @param shuttleName the name of the associated shuttle
     * @param timeNeeded  the time needed for the task
     */
    public Task (String task, Boolean status, int id, String shuttleName, int timeNeeded) {
        this.task = task;
        this.status = status;
        this.id = id;
        this.shuttleName = shuttleName;
        this.timeNeeded = timeNeeded;
    }

    /**
     * Returns a property of the task by index.
     *
     * @param index the index (0: task, 1: shuttleName, 2: mechanic, 3: status)
     * @return the property value as a string, or null if index is invalid
     */
    public String getByI(int index) {
        String result = switch (index) {
            case 0 -> task;
            case 2 -> mechanic;
            case 3 -> {
                if (status) {
                    yield "In Bearbeitung";
                } else {
                    yield "Fertig";
                }
            }
            case 1 -> shuttleName;
            default -> null;
        };
        logger.debug("getByI called with index {}: result='{}'", index, result);
        return result;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getTask() {
        return task;
    }

    /**
     * Returns the status of the task.
     *
     * @return true if in progress, false if finished
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * Returns the name of the assigned mechanic.
     *
     * @return the mechanic's name
     */
    public String getMechanic() {
        return mechanic;
    }

    /**
     * Returns the associated Shuttle object.
     *
     * @return the Shuttle object
     */
    public Shuttle getShuttle() {
        return shuttle;
    }

    /**
     * Returns the name of the associated shuttle.
     *
     * @return the shuttle name
     */
    public String getShuttleName() {
        return shuttleName;
    }

    /**
     * Returns the time needed for the task.
     *
     * @return the time needed
     */
    public int getTimeNeeded() {
        return timeNeeded;
    }

    /**
     * Returns the unique identifier of the task.
     *
     * @return the task ID
     */
    public int getId() { return id; }
}

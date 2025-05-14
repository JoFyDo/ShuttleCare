package com.rocketshipcheckingtool.server.datamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a task associated with a shuttle.
 * Contains details about the task, its status, assigned mechanic, and related shuttle information.
 */
public class Task {
    private String task; // The description of the task.
    private Boolean status; // The status of the task (e.g., completed or not).
    private String mechanic; // The name of the mechanic assigned to the task.
    private Shuttle shuttle; // The shuttle associated with the task.
    private String shuttleName; // The name of the shuttle associated with the task.
    private int id; // The unique identifier for the task.
    private int timeNeeded; // The time required to complete the task, in hours.

    private static final Logger logger = LoggerFactory.getLogger(Task.class); // Logger for logging task-related events.

    /**
     * Constructs a Task with the specified details.
     *
     * @param task     The description of the task.
     * @param status   The status of the task.
     * @param mechanic The name of the mechanic assigned to the task.
     * @param shuttle  The shuttle associated with the task.
     * @param id       The unique identifier for the task.
     */
    public Task(String task, Boolean status, String mechanic, Shuttle shuttle, int id) {
        this.task = task;
        this.status = status;
        this.mechanic = mechanic;
        this.shuttle = shuttle;
        this.id = id;
    }

    /**
     * Constructs a Task with the specified details, including shuttle name and time needed.
     *
     * @param task        The description of the task.
     * @param status      The status of the task.
     * @param mechanic    The name of the mechanic assigned to the task.
     * @param shuttleName The name of the shuttle associated with the task.
     * @param id          The unique identifier for the task.
     * @param timeNeeded  The time required to complete the task, in hours.
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
     * Constructs a Task with the specified details, excluding the mechanic.
     *
     * @param task        The description of the task.
     * @param status      The status of the task.
     * @param id          The unique identifier for the task.
     * @param shuttleName The name of the shuttle associated with the task.
     * @param timeNeeded  The time required to complete the task, in hours.
     */
    public Task(String task, Boolean status, int id, String shuttleName, int timeNeeded) {
        this.task = task;
        this.status = status;
        this.id = id;
        this.shuttleName = shuttleName;
        this.timeNeeded = timeNeeded;
    }

    /**
     * Gets the description of the task.
     *
     * @return The task description.
     */
    public String getTask() {
        return task;
    }

    /**
     * Gets the status of the task.
     *
     * @return The task status.
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * Gets the name of the mechanic assigned to the task.
     *
     * @return The mechanic's name.
     */
    public String getMechanic() {
        return mechanic;
    }

    /**
     * Gets the shuttle associated with the task.
     *
     * @return The associated shuttle.
     */
    public Shuttle getShuttle() {
        return shuttle;
    }

    /**
     * Gets the name of the shuttle associated with the task.
     *
     * @return The shuttle name.
     */
    public String getShuttleName() {
        return shuttleName;
    }

    /**
     * Gets the time required to complete the task.
     *
     * @return The time needed, in hours.
     */
    public int getTimeNeeded() {
        return timeNeeded;
    }

    /**
     * Gets the unique identifier for the task.
     *
     * @return The task ID.
     */
    public int getId() {
        return id;
    }

}
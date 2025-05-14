package com.rocketshipcheckingtool.server.database;

import com.rocketshipcheckingtool.server.database.repository.*;
import com.rocketshipcheckingtool.server.datamodel.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Calendar;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acts as a facade for database operations, providing a unified interface to interact with various repositories.
 * Manages connections and operations for shuttles, tasks, parts, notifications, mechanics, questionnaires, and comments.
 */
public class DatabaseFacade {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFacade.class); // Logger for logging database-related events.

    private final ShuttleRepository shuttleRepository; // Repository for shuttle-related operations.
    private final TaskRepository taskRepository; // Repository for task-related operations.
    private final GeneralTaskRepository generalTaskRepository; // Repository for general task-related operations.
    private final PartRepository partRepository; // Repository for part-related operations.
    private final NotificationRepository notificationRepository; // Repository for notification-related operations.
    private final MechanicRepository mechanicRepository; // Repository for mechanic-related operations.
    private final QuestionnaireRepository questionnaireRepository; // Repository for questionnaire-related operations.
    private final CommentRepository commentRepository; // Repository for comment-related operations.
    private final DatabaseConnector connector; // Connector for managing database connections.

    /**
     * Constructs a DatabaseFacade and initializes all repositories.
     * Establishes a database connection using the DatabaseConnector.
     *
     * @throws RuntimeException if the database connection fails.
     */
    public DatabaseFacade() {
        logger.info("Initializing DatabaseFacade...");
        this.connector = new BaseDatabaseConnection();
        Connection connection = null;
        try {
            connection = connector.getConnection();
        } catch (Exception e) {
            logger.error("Failed to get database connection", e);
            throw new RuntimeException("Failed to initialize DatabaseFacade", e);
        }

        this.shuttleRepository = new ShuttleRepository(connection);
        this.taskRepository = new TaskRepository(connection);
        this.generalTaskRepository = new GeneralTaskRepository(connection);
        this.partRepository = new PartRepository(connection);
        this.notificationRepository = new NotificationRepository(connection);
        this.mechanicRepository = new MechanicRepository(connection);
        this.questionnaireRepository = new QuestionnaireRepository(connection);
        this.commentRepository = new CommentRepository(connection);
        logger.info("All repositories initialized in DatabaseFacade.");
    }

    public DatabaseFacade(String databasePath) {
        logger.info("Initializing DatabaseFacade with custom database path...");
        this.connector = new BaseDatabaseConnection(databasePath);
        Connection connection = null;
        try {
            connection = connector.getConnection();
        } catch (Exception e) {
            logger.error("Failed to get database connection", e);
            throw new RuntimeException("Failed to initialize DatabaseFacade", e);
        }

        this.shuttleRepository = new ShuttleRepository(connection);
        this.taskRepository = new TaskRepository(connection);
        this.generalTaskRepository = new GeneralTaskRepository(connection);
        this.partRepository = new PartRepository(connection);
        this.notificationRepository = new NotificationRepository(connection);
        this.mechanicRepository = new MechanicRepository(connection);
        this.questionnaireRepository = new QuestionnaireRepository(connection);
        this.commentRepository = new CommentRepository(connection);
        logger.info("All repositories initialized in DatabaseFacade.");
    }

    // Shuttle Repository methods

    /**
     * Retrieves all shuttles from the database.
     *
     * @return A list of all shuttles.
     */
    public ArrayList<Shuttle> getShuttles() {
        return shuttleRepository.getShuttles();
    }

    /**
     * Retrieves a shuttle by its name.
     *
     * @param name The name of the shuttle.
     * @return The shuttle with the specified name.
     */
    public Shuttle getShuttle(String name) {
        return shuttleRepository.getShuttle(name);
    }

    /**
     * Retrieves a shuttle by its ID.
     *
     * @param id The ID of the shuttle.
     * @return The shuttle with the specified ID.
     */
    public Shuttle getShuttle(int id) {
        return shuttleRepository.getShuttle(id);
    }

    /**
     * Changes the status of a shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param status    The new status of the shuttle.
     */
    public void changeShuttleStatus(int shuttleID, String status) {
        shuttleRepository.changeShuttleStatus(shuttleID, status);
    }

    /**
     * Updates the status of a shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param status    The new status of the shuttle.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateShuttleStatus(int shuttleID, String status) {
        return shuttleRepository.updateShuttleStatus(shuttleID, status);
    }

    /**
     * Updates the predicted release time of a shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param time      The new predicted release time in minutes.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updatePredictedReleaseTime(int shuttleID, int time) {
        return shuttleRepository.updatePredictedReleaseTime(shuttleID, time);
    }

    /**
     * Retrieves the predicted release time of a shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @return The predicted release time as a Calendar object.
     */
    public Calendar getPredictedReleaseTime(int shuttleID) {
        return shuttleRepository.getPredictedReleaseTime(shuttleID);
    }

    /**
     * Sets the predicted release time of a shuttle.
     *
     * @param shuttleID            The ID of the shuttle.
     * @param predictedReleaseTime The new predicted release time as a string.
     * @return True if the update was successful, false otherwise.
     */
    public boolean setPredictedReleaseTime(int shuttleID, String predictedReleaseTime) {
        return shuttleRepository.setPredictedReleaseTime(shuttleID, predictedReleaseTime);
    }
    
    // Task Repository methods
    /**
     * Retrieves all tasks from the database.
     *
     * @return A list of all tasks.
     */
    public ArrayList<Task> getActiveTasks() {
        return taskRepository.getActiveTasks();
    }
    
    /**
     * Retrieves all tasks associated with a specific shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @return A list of tasks associated with the specified shuttle.
     */
    public ArrayList<Task> getActiveTaskByShuttleID(int shuttleID) {
        return taskRepository.getActiveTaskByShuttleID(shuttleID);
    }
    
    /**
     * Updates the status of a task by its ID.
     *
     * @param taskID The ID of the task.
     * @param status The new status for the task.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateTask(int taskID, String status) {
        return taskRepository.updateTask(taskID, status);
    }
    
    /**
     * Creates a new task.
     *
     * @param mechanic    The name of the mechanic assigned to the task.
     * @param description The description of the task.
     * @param shuttleID   The ID of the shuttle associated with the task.
     * @return True if the task was created successfully, false otherwise.
     * @throws IOException if an error occurs during task creation.
     */
    public boolean createTask(String mechanic, String description, String shuttleID) throws IOException {
        return taskRepository.createTask(mechanic, description, shuttleID);
    }
    
    /**
     * Updates the status of all tasks associated with a specific shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param status    The new status for the tasks.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateAllTasksActivityBelongToShuttle(int shuttleID, String status) {
        return taskRepository.updateAllTasksActivityBelongToShuttle(shuttleID, status);
    }
    
    /**
     * Updates the status of all tasks associated with a specific shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param status    The new status for the tasks.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateAllTasksStatusBelongToShuttle(int shuttleID, String status) {
        return taskRepository.updateAllTasksStatusBelongToShuttle(shuttleID, status);
    }
    
    // Part Repository methods
    /**
     * Retrieves all parts from the database.
     *
     * @return A list of all parts.
     */
    public ArrayList<Part> getParts() {
        return partRepository.getParts();
    }
    
    /**
     * Retrieves a part by its ID.
     *
     * @param partID The ID of the part.
     * @return The part with the specified ID.
     */
    public Part getPart(int partID) {
        return partRepository.getPart(partID);
    }
    
    /**
     * Updates the quantity of a part by its ID.
     *
     * @param partID   The ID of the part.
     * @param quantity The new quantity to set.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updatePartQuantity(Integer partID, Integer quantity) {
        return partRepository.updatePartQuantity(partID, quantity);
    }
    
    // Notification Repository methods
    /**
     * Retrieves all notifications for a specific user.
     *
     * @param user The username of the user.
     * @return A list of notifications for the specified user.
     */
    public ArrayList<Notification> getNotifications(String user) {
        return notificationRepository.getNotifications(user);
    }
    
    /**
     * Updates the status of a notification by its ID.
     *
     * @param notificationID The ID of the notification.
     * @param status         The new status for the notification.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateNotification(int notificationID, String status) {
        return notificationRepository.updateNotification(notificationID, status);
    }
    
    /**
     * Retrieves all notifications associated with a specific shuttle.
     *
     * @param shuttleID The ID of the shuttle.
     * @param user      The username of the user.
     * @return A list of notifications associated with the specified shuttle.
     */
    public ArrayList<Notification> getNotificationsByShuttle(String shuttleID, String user) {
        return notificationRepository.getNotificationsByShuttle(shuttleID, user);
    }
    
    /**
     * Creates a new notification.
     *
     * @param message   The message content of the notification.
     * @param shuttleId The ID of the shuttle associated with the notification.
     * @param sender    The sender of the notification.
     * @param comment   An optional comment related to the notification.
     * @return True if the notification was created successfully, false otherwise.
     */
    public boolean createNotification(String message, String shuttleId, String sender, String comment) {
        return notificationRepository.createNotification(message, shuttleId, sender, comment);
    }
    
    // Mechanic Repository methods
    /**
     * Retrieves all mechanics from the database.
     *
     * @return A list of all mechanics.
     */
    public ArrayList<Mechanic> getMechanics() {
        return mechanicRepository.getMechanics();
    }

    /**
     * Retrieves a mechanic by their ID.
     *
     * @param mechanicId The ID of the mechanic.
     * @return The mechanic with the specified ID.
     */
    public Mechanic getMechanic(int mechanicId) {
        return mechanicRepository.getMechanic(mechanicId);
    }
    
    // Questionnaire Repository methods
    /**
     * Retrieves all questionnaires from the database.
     *
     * @return A list of all questionnaires.
     */
    public ArrayList<QuestionnaireRating> getQuestionnaires() {
        return questionnaireRepository.getQuestionnaires();
    }
    
    /**
     * Retrieves a questionnaire by its Shuttle ID.
     *
     * @param shuttleID The ID of the questionnaire.
     * @return The questionnaire with the specified ID.
     */
    public ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(int shuttleID) {
        return questionnaireRepository.getQuestionnaireForShuttle(shuttleID);
    }
    
    /**
     * Updates the status of a questionnaire rating by its ID.
     *
     * @param questionnaireID The ID of the questionnaire rating.
     * @param status          The new status for the questionnaire rating.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateQuestionnaireRating(int questionnaireID, String status) {
        return questionnaireRepository.updateQuestionnaireRating(questionnaireID, status);
    }
    
    // Comment Repository methods
    /**
     * Retrieves all comments from the database.
     *
     * @return A list of all comments.
     */
    public ArrayList<Comment> getComments() {
        return commentRepository.getComments();
    }
    
    /**
     * Retrieves comments by the shuttle ID.
     *
     * @param shuttleID The ID of the comment.
     * @return The comment with the specified ID.
     */
    public ArrayList<Comment> getCommentsByShuttle(int shuttleID) {
        return commentRepository.getCommentsByShuttle(shuttleID);
    }
    
    /**
     * Updates the status of a comment by its ID.
     *
     * @param commentID The ID of the comment.
     * @param status    The new status for the comment.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateComment(int commentID, String status) {
        return commentRepository.updateComment(commentID, status);
    }
    
    // General Task Repository methods
    /**
     * Retrieves all general tasks from the database.
     *
     * @return A list of all general tasks.
     */
    public ArrayList<Task> getGeneralTasksForShuttle(int shuttleID) {
        return generalTaskRepository.getGeneralTasksForShuttle(shuttleID);
    }
    
    /**
     * Updates the status of a general task by its ID.
     *
     * @param taskID The ID of the general task.
     * @param status The new status for the general task.
     * @return The general task with the specified ID.
     */
    public boolean updateGeneralTask(int taskID, String status) {
        return generalTaskRepository.updateGeneralTask(taskID, status);
    }

    /**
     * Updates the status of a general tasks by its Shuttle ID.
     *
     * @param shuttleID The ID of the shuttle.
     * @param status The new status for the general task.
     * @return The general task with the specified ID.
     */
    public boolean updateAllGeneralTasksStatusBelongToShuttle(int shuttleID, String status) {
        return generalTaskRepository.updateAllGeneralTasksStatusBelongToShuttle(shuttleID, status);
    }

    /**
     * Disconnects the database connection managed by the DatabaseFacade.
     *
     * @throws SQLException if an error occurs during disconnection.
     */
    public void disconnect() throws SQLException {
        logger.info("Disconnecting DatabaseFacade...");
        try {
            connector.disconnect();
            logger.info("DatabaseFacade disconnected.");
        } catch (SQLException e) {
            logger.error("Error disconnecting DatabaseFacade", e);
            throw e;
        }
    }
    
}


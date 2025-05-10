package com.rocketshipcheckingtool.server.database;

import com.rocketshipcheckingtool.server.database.repository.*;
import com.rocketshipcheckingtool.server.datamodel.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Calendar;
import java.sql.SQLException;

public class DatabaseFacade {
    private final ShuttleRepository shuttleRepository;
    private final TaskRepository taskRepository;
    private final GeneralTaskRepository generalTaskRepository;
    private final PartRepository partRepository;
    private final NotificationRepository notificationRepository;
    private final MechanicRepository mechanicRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final CommentRepository commentRepository;
    private final DatabaseConnector connector;

    public DatabaseFacade() {
        this.connector = new BaseDatabaseConnection();
        Connection connection = connector.getConnection();
        
        this.shuttleRepository = new ShuttleRepository(connection);
        this.taskRepository = new TaskRepository(connection);
        this.generalTaskRepository = new GeneralTaskRepository(connection);
        this.partRepository = new PartRepository(connection);
        this.notificationRepository = new NotificationRepository(connection);
        this.mechanicRepository = new MechanicRepository(connection);
        this.questionnaireRepository = new QuestionnaireRepository(connection);
        this.commentRepository = new CommentRepository(connection);
    }

    // Shuttle Repository methods
    public ArrayList<Shuttle> getShuttles() {
        return shuttleRepository.getShuttles();
    }
    
    public Shuttle getShuttle(String name) {
        return shuttleRepository.getShuttle(name);
    }
    
    public Shuttle getShuttle(int id) {
        return shuttleRepository.getShuttle(id);
    }
    
    public void changeShuttleStatus(int shuttleID, String status) {
        shuttleRepository.changeShuttleStatus(shuttleID, status);
    }
    
    public boolean updateShuttleStatus(int shuttleID, String status) {
        return shuttleRepository.updateShuttleStatus(shuttleID, status);
    }
    
    public boolean updatePredictedReleaseTime(int shuttleID, int time) {
        return shuttleRepository.updatePredictedReleaseTime(shuttleID, time);
    }
    
    public Calendar getPredictedReleaseTime(int shuttleID) {
        return shuttleRepository.getPredictedReleaseTime(shuttleID);
    }
    
    public boolean setPredictedReleaseTime(int shuttleID, String predictedReleaseTime) {
        return shuttleRepository.setPredictedReleaseTime(shuttleID, predictedReleaseTime);
    }
    
    // Task Repository methods
    public ArrayList<Task> getActiveTasks() {
        return taskRepository.getActiveTasks();
    }
    
    public ArrayList<Task> getActiveTaskByShuttleID(int shuttleID) {
        return taskRepository.getActiveTaskByShuttleID(shuttleID);
    }
    
    public boolean updateTask(int taskID, String status) {
        return taskRepository.updateTask(taskID, status);
    }
    
    public boolean createTask(String mechanic, String description, String shuttleID) throws IOException {
        return taskRepository.createTask(mechanic, description, shuttleID);
    }
    
    public boolean updateAllTasksActivityBelongToShuttle(int shuttleID, String status) {
        return taskRepository.updateAllTasksActivityBelongToShuttle(shuttleID, status);
    }
    
    public boolean updateAllTasksStatusBelongToShuttle(int shuttleID, String status) {
        return taskRepository.updateAllTasksStatusBelongToShuttle(shuttleID, status);
    }
    
    // Part Repository methods
    public ArrayList<Part> getParts() {
        return partRepository.getParts();
    }
    
    public Part getPart(int partID) {
        return partRepository.getPart(partID);
    }
    
    public boolean updatePartQuantity(Integer partID, Integer quantity) {
        return partRepository.updatePartQuantity(partID, quantity);
    }
    
    // Notification Repository methods
    public ArrayList<Notification> getNotifications(String user) {
        return notificationRepository.getNotifications(user);
    }
    
    public boolean updateNotification(int notificationID, String status) {
        return notificationRepository.updateNotification(notificationID, status);
    }
    
    public ArrayList<Notification> getNotificationsByShuttle(String shuttleID, String user) {
        return notificationRepository.getNotificationsByShuttle(shuttleID, user);
    }
    
    public boolean createNotification(String message, String shuttleId, String sender, String comment) {
        return notificationRepository.createNotification(message, shuttleId, sender, comment);
    }
    
    // Mechanic Repository methods
    public ArrayList<Mechanic> getMechanics() {
        return mechanicRepository.getMechanics();
    }
    
    // Questionnaire Repository methods
    public ArrayList<QuestionnaireRating> getQuestionnaires() {
        return questionnaireRepository.getQuestionnaires();
    }
    
    public ArrayList<QuestionnaireRating> getQuestionnaireForShuttle(int shuttleID) {
        return questionnaireRepository.getQuestionnaireForShuttle(shuttleID);
    }
    
    public boolean updateQuestionnaireRating(int questionnaireID, String status) {
        return questionnaireRepository.updateQuestionnaireRating(questionnaireID, status);
    }
    
    // Comment Repository methods
    public ArrayList<Comment> getComments() {
        return commentRepository.getComments();
    }
    
    public ArrayList<Comment> getCommentsByShuttle(int shuttleID) {
        return commentRepository.getCommentsByShuttle(shuttleID);
    }
    
    public boolean updateComment(int commentID, String status) {
        return commentRepository.updateComment(commentID, status);
    }
    
    // General Task Repository methods
    public ArrayList<Task> getGeneralTasksForShuttle(int shuttleID) {
        return generalTaskRepository.getGeneralTasksForShuttle(shuttleID);
    }
    
    public boolean updateGeneralTask(int taskID, String status) {
        return generalTaskRepository.updateGeneralTask(taskID, status);
    }
    
    public boolean updateAllGeneralTasksStatusBelongToShuttle(int shuttleID, String status) {
        return generalTaskRepository.updateAllGeneralTasksStatusBelongToShuttle(shuttleID, status);
    }
    
    public void disconnect() throws SQLException {
        connector.disconnect();
    }
}
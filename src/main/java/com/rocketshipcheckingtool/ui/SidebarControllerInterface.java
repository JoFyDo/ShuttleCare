package com.rocketshipcheckingtool.ui;

/**
 * Interface for sidebar controllers in the application.
 * Provides methods for setting the main controller and selecting buttons in the sidebar.
 */
public interface SidebarControllerInterface {

    /**
     * Sets the main controller for managing views.
     *
     * @param controller The main view manager controller to be set.
     */
    void setMainController(ViewManagerController controller);

    /**
     * Selects a button in the sidebar based on the given button ID.
     * Highlights the selected button to indicate the active view.
     *
     * @param buttonId The ID of the button to be selected.
     */
    void selectButton(String buttonId);
}
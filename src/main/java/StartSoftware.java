import com.rocketshipcheckingtool.server.Server;
import com.rocketshipcheckingtool.ui.ViewManager;

/**
 * Entry point for the Rocketship Checking Tool software.
 * Initializes and starts the server and launches the user interface.
 * @author ShuttleCare Team
 * @version 1.0
 */
public class StartSoftware {

    /**
     * Main method to start the software.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        // Create and start the server
        Server server = new Server();
        server.start();

        // Launch the user interface
        ViewManager.main(args);
    }
}
import com.rocketshipcheckingtool.server.Server;
import com.rocketshipcheckingtool.ui.technician.HomeView;
import com.rocketshipcheckingtool.ui.technician.SceneManager;

public class StartSoftware {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        SceneManager.main(args);

    }
}

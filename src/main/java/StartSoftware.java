import com.rocketshipcheckingtool.server.Server;
import com.rocketshipcheckingtool.ui.technician.HomeView;

public class StartSoftware {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        HomeView.main(args);

    }
}

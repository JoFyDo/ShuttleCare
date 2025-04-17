import com.rocketshipcheckingtool.server.Server;
import com.rocketshipcheckingtool.ui.MainView;
import com.rocketshipcheckingtool.ui.technician.HomeView;

public class StartSoftware {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        MainView.main(args);

    }
}

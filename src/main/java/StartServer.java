import com.rocketshipcheckingtool.server.Server;
import com.rocketshipcheckingtool.ui.ViewManager;

public class StartServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

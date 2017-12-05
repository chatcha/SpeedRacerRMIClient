
import java.awt.Color;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

public interface IClient {

    public boolean connect(String url, int port);

    public List<Party> getParties();

    public boolean joinParty(String name);

    public Arena getArena();

    public void disconnect();

    public boolean createParty(String name);

    public String getUsername();

    public void setGui(GUI gui);

    boolean leaveParty(String name);

    public void newGrid();

    public void addObserver(Observer jpScores);

    public void newGame();

    public void beginGame();

    public void moveCar(String choice, boolean flag);

    public void close();

    public boolean isConnected();
}

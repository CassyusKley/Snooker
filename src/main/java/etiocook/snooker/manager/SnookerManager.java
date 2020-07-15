package etiocook.snooker.manager;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SnookerManager {

    private static SnookerManager instance;
    private boolean state;
    private boolean running;
    private boolean happening;
    private boolean camarote;
    private List<Player> camaroteList = new LinkedList<>();

    public static SnookerManager getInstance() {
        if (instance == null) instance = new SnookerManager();
        return instance;
    }

    public SnookerManager() {
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public List<Player> getCamaroteList() {
        return camaroteList;
    }

    public void setCamaroteList(List<Player> camaroteList) {
        this.camaroteList = camaroteList;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isHappening() {
        return happening;
    }

    public void setHappening(boolean happening) {
        this.happening = happening;
    }

    public boolean isCamarote() {
        return camarote;
    }

    public void setCamarote(boolean camarote) {
        this.camarote = camarote;
    }
}

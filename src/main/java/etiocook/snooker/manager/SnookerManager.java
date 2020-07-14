package etiocook.snooker.manager;

public class SnookerManager {

    private boolean state;
    private boolean running;
    private boolean happening;

    public SnookerManager() {
        this.happening = false;
        this.state = false;
        this.running = false;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
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
}

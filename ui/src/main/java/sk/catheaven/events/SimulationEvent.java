package sk.catheaven.events;

public class SimulationEvent {

    private Action action;
    public SimulationEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public enum Action {
        PLAY,
        STOP,
        PAUSE,
        RESET,
        STEP,
        PLAY_FAST,
        ZOOM_IN,
        ZOOM_OUT
    }
}

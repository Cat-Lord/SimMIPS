package sk.catheaven.events;

public class ApplicationEvent {
    private final Action action;
    public ApplicationEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        SHUTDOWN
    }
}

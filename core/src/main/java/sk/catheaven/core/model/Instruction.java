package sk.catheaven.core.model;

public class Instruction {
    private final String mnemo;
    private final String description;

    public Instruction(String mnemo, String description) {
        this.mnemo = mnemo;
        this.description = description;
    }

    public String getMnemo() {
        return mnemo;
    }

    public String getDescription() {
        return description;
    }
}

package dev.diona.southside.command;

public abstract class Command {
    public final String description;

    public Command(String description) {
        this.description = description;
    }

    public abstract void run(String[] args);

    public abstract void printUsage();
}

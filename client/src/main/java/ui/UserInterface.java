package ui;

public interface UserInterface {
    CommandOutput eval(String cmd, String[] args);
    String help();
    String getPromptText();
}

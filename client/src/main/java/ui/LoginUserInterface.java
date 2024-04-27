package ui;

import data.DataCache;
import model.UserData;

public class LoginUserInterface implements UserInterface {

    @Override
    public CommandOutput eval(String cmd, String[] args) {
        return switch (cmd) {
            case "l", "login" -> login(args);
            case "r", "register" -> register(args);
            case "q", "quit" -> new CommandOutput("quit", true);
            case "h", "help" -> new CommandOutput(help(), true);
            default -> new CommandOutput(help(), false);
        };
    }


    @Override
    public String help() {
        return """
        Options:
        Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>
        Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>
        Exit the program: \"q\", \"quit\"
        Print this message: \"h\", \"help\"
        """;
    }


    @Override
    public String getPromptText() {
        return "Chess Login";
    }


    private CommandOutput login(String[] args) {
        if(args.length != 2) return new CommandOutput("Usage: login <USERNAME> <PASSWORD>", false);
        UserData request = new UserData(args[0], args[1], null);
        DataCache.getInstance().getFacade().login(request);
        DataCache.getInstance().setState(DataCache.State.LOGGED_IN);
        return new CommandOutput("Successfully logged in.", true);
    }



    private CommandOutput register(String[] args) {
        if(args.length != 3) return new CommandOutput("Usage: register <USERNAME> <PASSWORD> <EMAIL>", false);
        UserData request = new UserData(args[0], args[1], args[2]);
        DataCache.getInstance().getFacade().register(request);
        DataCache.getInstance().setState(DataCache.State.LOGGED_IN);
        return new CommandOutput("Successfully registered.", true);
    }

}

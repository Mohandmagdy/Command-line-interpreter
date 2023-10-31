import java.util.Objects;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

public class Parser {
    String commandName;
    String[] args;

    public Parser() {
        commandName = "";
        args = new String[10];
    }
    public boolean parse(String input) {
        String[] words = input.split(" ");
        if (Objects.equals(words[0], "echo")) {
            commandName = "echo";
            args = Arrays.copyOfRange(words, 1, words.length);
        } else if (Objects.equals(words[0], "pwd")) {
            commandName = "pwd";
        } else if (Objects.equals(words[0], "cd")) {
            commandName = "cd";
            if(words.length == 2){
                args[0] = words[1];
            } else if (words.length > 2){
                commandName = words[0];
                args[0] = "arguments";
                return false;
            }
        } else if (Objects.equals(words[0], "ls")) {
            commandName = "ls";
            if (words.length == 2) {
                if (Objects.equals(words[1], "-r")) {
                    commandName = "ls-r";
                } else {
                    commandName = words[0];
                    args[0] = "arguments";
                    return false;
                }
            } else if (words.length > 2) {
                commandName = words[0];
                args[0] = "arguments";
                return false;
            }

        } else if (Objects.equals(words[0], "mkdir")) {
            commandName = "mkdir";
            args = Arrays.copyOfRange(words, 1, words.length);
        } else if (Objects.equals(words[0], "exit")) {
            commandName = "exit";
        } else {
            commandName = words[0];
            return false;
        }
        return true;
    }
}

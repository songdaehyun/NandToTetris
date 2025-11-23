import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Parser {
    BufferedReader br;

    String currentCommand;
    Set<String> arithmetic = new HashSet<>();

    public Parser (BufferedReader reader) throws FileNotFoundException {
        br = reader;
        arithmetic.addAll(Arrays.asList("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"));
    }

    public boolean hasMoreLines() throws IOException {
        return br.ready();
    }

    public String advance() throws IOException {
        String line = br.readLine().trim();
        int commentIndex = line.indexOf("//");

        if (commentIndex != -1) {
            currentCommand = line.substring(0, commentIndex).trim();
            return currentCommand;
        }

        currentCommand = line;
        return currentCommand;
    }

    public String commandType() {
        String[] splitCommand = currentCommand.split(" ");
        if (arithmetic.contains(splitCommand[0])){
            return "C_ARITHMETIC";
        }
        return switch (splitCommand[0]) {
            case "push" -> "C_PUSH";
            case "pop" -> "C_POP";
            case "label" -> "C_LABEL";
            case "goto" -> "C_GOTO";
            case "if-goto" -> "C_IF";
            case "call" -> "C_CALL";
            case "function" -> "C_FUNCTION";
            case "return" -> "C_RETURN";
            default -> "commandType error";
        };
    }

    public String arg1() {
        String[] splitCommand = currentCommand.split(" ");
        String commandType = commandType();
        if (commandType.equals("C_ARITHMETIC")
                || commandType.equals("C_RETURN")){
            return splitCommand[0];
        } else if (commandType.equals("C_PUSH")
                || commandType.equals("C_POP")
                || commandType.equals("C_LABEL")
                || commandType.equals("C_GOTO")
                || commandType.equals("C_IF")
                || commandType.equals("C_FUNCTION")
                || commandType.equals("C_CALL")) {
            return splitCommand[1];
        } else {
            return "arg1 error";
        }
    }

    public int arg2() {
        String[] splitCommand = currentCommand.split(" ");
        if(splitCommand.length > 2){
            return Integer.parseInt(splitCommand[2]);
        } else {
            return -1;
        }

    }

}



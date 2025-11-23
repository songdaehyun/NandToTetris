import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    BufferedReader br;

    int lineCounter = 0;
    String currentInstruction;


    public Parser (BufferedReader reader) throws FileNotFoundException {
        br = reader;
    }

    public boolean hasMoreLines() throws IOException {
        return br.ready();
    }

    public String advance() throws IOException {
        currentInstruction = br.readLine().trim();
        return currentInstruction;
    }

    public String instructionType() {
        if (currentInstruction.startsWith("@")){
            return "A";
        }else if(currentInstruction.startsWith("(")) {
            return "L";
        } else{
            return "C";
        }
    }

    public String symbol() {
        if (currentInstruction.startsWith("@")){
            return currentInstruction.substring(1);
        }else if (currentInstruction.startsWith("(")){
            return currentInstruction.substring(1, currentInstruction.length()-1);
        } else {
            return currentInstruction;
        }
    }

    public String dest() {
        int destIndex = currentInstruction.indexOf("=");
        if (destIndex != -1){
            return currentInstruction.substring(0, destIndex).trim();
        } else {
            return "";
        }
    }

    public String comp() {
        int destIndex = currentInstruction.indexOf("=");
        int jumpIndex = currentInstruction.indexOf(";");
        if (destIndex != -1){
            if (jumpIndex != -1){
                return currentInstruction.substring(destIndex + 1, jumpIndex).trim();
            } else {
                return currentInstruction.substring(destIndex + 1).trim();
            }
        } else {
            if (jumpIndex != -1){
                return currentInstruction.substring(0, jumpIndex).trim();
            } else {
                return currentInstruction.trim();
            }
        }
    }

    public String jump() {
        int jumpIndex = currentInstruction.indexOf(";");
        if (jumpIndex != -1){
            return currentInstruction.substring(jumpIndex + 1).trim();
        } else {
            return "";
        }
    }

}

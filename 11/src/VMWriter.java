import java.io.BufferedWriter;
import java.io.IOException;

public class VMWriter {
    BufferedWriter wr;

    public VMWriter(BufferedWriter writer){
        wr = writer;
    }

    public void writePush(String segment, int index) throws IOException {
        String vm = "push %s %d".formatted(segment, index);
        wr.write(vm);
        wr.newLine();
    }

    public void writePop(String segment, int index) throws IOException {
        String vm = "pop %s %d".formatted(segment, index);
        wr.write(vm);
        wr.newLine();
    }

    public void writeArithmetic(String op) throws IOException {
        String command;
        switch (op) {
            case "+" -> command = "add";
            case "-" -> command = "sub";
            case "=" -> command = "eq";
            case ">" -> command = "gt";
            case "<" -> command = "lt";
            case "&" -> command = "and";
            case "|" -> command = "or";
            case "*" -> command = "call Math.multiply 2";
            case "/" -> command = "call Math.divide 2";
            case "neg" -> command = "neg";
            case "not" -> command = "not";
            default -> command = "error";
        }
        String vm = "%s".formatted(command);
        wr.write(vm);
        wr.newLine();
    }

    public void writeLabel(String label) throws IOException {
        String vm = "label %s".formatted(label);
        wr.write(vm);
        wr.newLine();
    }

    public void writeGoto(String label) throws IOException {
        String vm = "goto %s".formatted(label);
        wr.write(vm);
        wr.newLine();
    }

    public void writeIf(String label) throws IOException {
        String vm = "if-goto %s".formatted(label);
        wr.write(vm);
        wr.newLine();
    }

    public void writeCall(String name, int nArgs) throws IOException {
        String vm = "call %s %d".formatted(name, nArgs);
        wr.write(vm);
        wr.newLine();
    }

    public void writeFunction(String name, int nVars) throws IOException {
        String vm = "function %s %d".formatted(name, nVars);
        wr.write(vm);
        wr.newLine();
    }

    public void writeReturn() throws IOException {
        wr.write("return");
        wr.newLine();
    }


}

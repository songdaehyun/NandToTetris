import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CodeWriter {
    BufferedWriter wr;
    String fileNameForStatic;
    int labelCount = 0;


    public CodeWriter(BufferedWriter writer) throws FileNotFoundException {
        wr = writer;
    }

    public void setFileName(String fileName) {
        this.fileNameForStatic = fileName;
    }

    public void writeInit() throws IOException {
        // SP = 256
    }


    public void writeArithmetic (String command) throws IOException {
        String arithmetic =  switch (command) {
            case "add" ->
                    """
                    D=D+M
                    """;
            case "sub" ->
                    """
                    D=M-D
                    """;
            case "eq" ->
                    """
                    D=M-D
                    
                    @TRUE%d
                    D;JEQ
                    
                    D=0
                    @NEXT%d
                    0;JMP
                    
                    (TRUE%d)
                    D=-1
                    
                    (NEXT%d)
                    """.formatted(labelCount, labelCount, labelCount, labelCount);
            case "gt"->
                    """
                    D=M-D
                    
                    @TRUE%d
                    D;JGT
                    
                    D=0
                    @NEXT%d
                    0;JMP
                    
                    (TRUE%d)
                    D=-1
                    
                    (NEXT%d)
                    """.formatted(labelCount, labelCount, labelCount, labelCount);
            case "lt" ->
                    """
                    D=M-D
                    
                    @TRUE%d
                    D;JLT
                    
                    D=0
                    @NEXT%d
                    0;JMP
                    
                    (TRUE%d)
                    D=-1
                    
                    (NEXT%d)
                    """.formatted(labelCount, labelCount, labelCount, labelCount);
            case "and" ->
                    """
                    D=D&M
                    """;
            case "or" ->
                    """
                    D=D|M
                    """;
            case "neg" ->
                    """
                    D=-D
                    """;
            case "not" ->
                    """
                    D=!D
                    """;
            default ->
                    """
                    arithmetic error
                    """;
        };

        String unary =
                """
                @SP
                M=M-1
                A=M
                
                D=M
                """;

        String binary =
                """
                @SP
                M=M-1
                A=M
                
                D=M
                
                @SP
                M=M-1
                A=M
                
                """;

        String post =
                """
                @SP
                A=M
                M=D
                
                @SP
                M=M+1
                """;

        String total;
        if(command.equals("neg") || command.equals("not")){
            total = unary + arithmetic + post;
        }else{
            total = binary + arithmetic + post;
        }

        if(command.equals("eq") || command.equals("gt") || command.equals("lt")){
            labelCount += 1;
        }

        System.out.println("writing arithmetic...");
        wr.write(total);
        wr.newLine();
    }


    public void writePushPop(String command, String segment, int ind) throws IOException {
        String Assembly = "";
        switch (segment) {
            case "constant":
                switch (command) {
                    case "push":
                        Assembly =
                                """
                                @%d
                                D=A
                                @SP
                                A=M
                                M=D
                                @SP
                                M=M+1
                                """.formatted(ind);

                        break;
                    default:
                        System.out.println("constant segment has only push");
                }
                break;
            case "local":
            case "argument":
            case "this":
            case "that":
                String SEGMENT = switch (segment){
                    case "local" -> "LCL";
                    case "argument" -> "ARG";
                    case "this" -> "THIS";
                    case "that" -> "THAT";
                    default -> "wrong segment";
                };
                switch (command) {
                    case "push":
                        Assembly =
                                """
                                @%d
                                D=A
                                @%s
                                A=D+M
                              
                                D=M
                                @SP
                                A=M
                                M=D
                                
                                @SP
                                M=M+1
                                """.formatted(ind, SEGMENT);
                        break;
                    case "pop":
                        Assembly =
                                """
                                @SP
                                M=M-1
                                
                                @%d
                                D=A
                                @%s
                                D=D+M
                                @R13
                                M=D
                                
                                @SP
                                A=M
                                D=M
                                @R13
                                A=M
                                M=D
                                """.formatted(ind, SEGMENT);
                        break;
                    default:
                        System.out.println("NOT PUSH OR POP");
                }
                break;
            case "static":
                //file name은 wr의 write path에서 읽어와야할듯.
                switch (command) {
                    case "push":
                        Assembly =
                                """
                                @%s.%d
                                D=M
                                
                                @SP
                                A=M
                                M=D
                                
                                @SP
                                M=M+1
                                """.formatted(fileNameForStatic, ind);
                        break;
                    case "pop":
                        Assembly =
                                """
                                @SP
                                M=M-1
                                
                                A=M
                                D=M
                                
                                @%s.%d
                                M=D
                                """.formatted(fileNameForStatic, ind);
                        break;
                    default:
                        System.out.println("NOT PUSH OR POP");
                }
                break;
            case "temp":
                switch (command) {
                    case "push":
                        Assembly =
                                """
                                @%d
                                D=A
                                @5
                                A=D+A
                                
                                D=M
                                @SP
                                A=M
                                M=D
                                
                                @SP
                                M=M+1
                                """.formatted(ind);
                        break;
                    case "pop":
                        Assembly =
                                """
                                @SP
                                M=M-1
                                
                                @%d
                                D=A
                                @5
                                D=D+A
                                @R13
                                M=D
                                
                                @SP
                                A=M
                                D=M
                                @R13
                                A=M
                                M=D
                                """.formatted(ind);
                        break;
                    default:
                        System.out.println("NOT PUSH OR POP");
                }
                break;
            case "pointer":
                switch (command) {
                    case "push":
                        switch (ind){
                            case 0:
                                Assembly =
                                        """
                                        @THIS
                                        D=M
                                        
                                        @SP
                                        A=M
                                        M=D
                                        
                                        @SP
                                        M=M+1
                                        """;
                                break;
                            case 1:
                                Assembly =
                                        """
                                        @THAT
                                        D=M
                                        
                                        @SP
                                        A=M
                                        M=D
                                        
                                        @SP
                                        M=M+1
                                        """;
                                break;
                            default:
                                System.out.println("push pointer must be 0 or 1");
                        }
                        break;
                    case "pop":
                        switch (ind){
                            case 0:
                                Assembly =
                                        """
                                        @SP
                                        M=M-1
                                        
                                        A=M
                                        D=M
                                        
                                        @THIS
                                        M=D
                                        """;
                                break;
                            case 1:
                                Assembly =
                                        """
                                        @SP
                                        M=M-1
                                        
                                        A=M
                                        D=M
                                        
                                        @THAT
                                        M=D
                                        """;
                                break;
                            default:
                                System.out.println("push pointer must be 0 or 1");
                        }
                        break;
                    default:
                        System.out.println("NOT PUSH OR POP");
                }
                break;
            default:
                System.out.println("not valid segment");
        }
        System.out.println("writing push/pop...");
        wr.write(Assembly);
        wr.newLine();

    }

    public void writeLabel(String label) throws IOException{
        String assembly = "(" + label + ")";
        System.out.println("writing label...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeGoto(String label) throws IOException{
        String assembly =
                """
                @%s
                0;JMP
                """.formatted(label);
        System.out.println("writing goto...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeIf(String label) throws IOException{
        String assembly =
                """
                @SP
                M=M-1
                A=M
                D=M
                
                @%s
                D;JNE
                """.formatted(label);
        System.out.println("writing if-goto...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeFunction(String functionName, int nVars) throws IOException{
        String functionlabel =
                """
                (%s)
                """.formatted(functionName);

        String assembly = "";
        String pushNvars =
                """
                @SP
                A=M
                M=0
                
                @SP
                M=M+1
                
                """;
        for (int i=0; i<nVars;i++ ){
            assembly += pushNvars;
        }
        assembly = functionlabel + assembly;
        System.out.println("writing function...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeCall(String command) throws IOException{

    }

    public void writeReturn() throws IOException{
        String assembly1 = """
                @LCL
                D=M
                @R13
                M=D
                
                @R13
                D=M
                @5
                D=D-A
                A=D
                D=M
                @R14
                M=D
                
                @SP
                M=M-1
                A=M
                D=M
                @ARG
                A=M
                M=D
                
                @ARG
                M=M+1
                D=M
                @SP
                M=D
                
                """;

        String assembly2 = "";
        List<String> pointers = Arrays.asList("THAT", "THIS", "ARG", "LCL");

        for(int i=1; i < pointers.size()+1; i++){
            assembly2 +=
                    """
                    @R13
                    D=M
                    @%d
                    A=D-A
                    D=M
                    @%s
                    M=D
                    
                    """.formatted(i, pointers.get(i-1));
        }

        String assembly3 = """
                @R14
                A=M
                0;JMP
                
                """;

        String assembly = assembly1 + assembly2 + assembly3;
        System.out.println("writing return...");
        wr.write(assembly);
        wr.newLine();
    }
}

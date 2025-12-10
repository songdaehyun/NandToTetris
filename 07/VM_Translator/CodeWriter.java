import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CodeWriter {
    BufferedWriter wr;
    String fileName;
    String functionName;
    int returnCount;
    int labelCount = 0;



    public CodeWriter(BufferedWriter writer) throws FileNotFoundException {
        wr = writer;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.returnCount = 0;
    }

    public void setFunctionName(String funName){
        this.functionName = funName;
    }

    public void writeInit() throws IOException {
        String spInit =
                """
                @256
                D=A
                @SP
                M=D
                """;
        /*
        String callSysInit =
                """
                @Sys.init
                0;JMP
                """;
         */
        String assembly = spInit;
        System.out.println("writing Boot strap...");
        wr.write(assembly);
        wr.newLine();
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
                                """.formatted(fileName, ind);
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
                                """.formatted(fileName, ind);
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
        String assembly = "(%s.%s$%s)".formatted(fileName, functionName, label);
        System.out.println("writing label...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeGoto(String label) throws IOException{
        String assembly =
                """
                @%s.%s$%s
                0;JMP
                """.formatted(fileName, functionName, label);
        System.out.println("writing goto...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeIfGoto(String label) throws IOException{
        String assembly =
                """
                @SP
                M=M-1
                A=M
                D=M
                
                @%s.%s$%s
                D;JNE
                """.formatted(fileName, functionName, label);
        System.out.println("writing if-goto...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeFunction(String functionName, int nVars) throws IOException{
        //함수선언할때 함수이름 저장.
        setFunctionName(functionName);

        String functionlabel =
                """
                (%s)
                """.formatted(functionName);

        String assembly =
                """
                """;
        String pushNvars =
                """
                @SP
                A=M
                M=0
                
                @SP
                M=M+1
                
                """;
        for (int i=0; i<nVars; i++ ){
            assembly += pushNvars;
        }
        assembly = functionlabel + assembly;
        System.out.println("writing function...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeCall(String functionName, int nArgs) throws IOException{
        returnCount += 1;
        String returnAddress = "%s.%s$ret.%d".formatted(fileName, functionName, returnCount);
        String pushRetAddr =
                """
                @%s
                D=A
                
                @SP
                A=M
                M=D
                
                @SP
                M=M+1
                
                """.formatted(returnAddress);

        List<String> pointers = Arrays.asList("LCL", "ARG", "THIS", "THAT");

        String pushFrame =
                """
                """;
        for (String pointer : pointers) {
            pushFrame +=
            """
            @%s
            D=M
            
            @SP
            A=M
            M=D
            
            @SP
            M=M+1
            
            """.formatted(pointer);
        }

        String argReposition =
                """
                @SP
                D=M
                @5
                D=D-A
                @%d
                D=D-A
                @ARG
                M=D
                
                """.formatted(nArgs);

        String lclReposition =
                """
                @SP
                D=M
                @LCL
                M=D
                
                """;

        String gotoFunction =
                """
                @%s
                0;JMP
                
                """.formatted(functionName);
        String returnLabel = "(" + returnAddress + ")";

        String assembly = pushRetAddr + pushFrame + argReposition + lclReposition + gotoFunction + returnLabel;
        System.out.println("writing Call...");
        wr.write(assembly);
        wr.newLine();
    }

    public void writeReturn() throws IOException{
        //13번 레지스터에 임시로 저장
        String frameEnd =
                """
                @LCL
                D=M
                @R13
                M=D
                
                """;
        //14번 레지스터에 임시로 저장
        String retAddr =
                """
                @R13
                D=M
                @5
                D=D-A
                A=D
                D=M
                @R14
                M=D
                
                """;
        //ARG[0]에 리턴값 저장
        String retValue =
                """
                @SP
                M=M-1
                A=M
                D=M
                @ARG
                A=M
                M=D
                
                """;
        //SP를 재조정
        String spReposition = """
                @ARG
                D=M+1
                @SP
                M=D
                
                """;

        //caller의 context 다시 불러오기
        String contextRecover =
                """
                """;
        List<String> pointers = Arrays.asList("THAT", "THIS", "ARG", "LCL");
        for(int i=1; i < pointers.size()+1; i++){
            contextRecover +=
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
        //돌아갈 주소
        String gotoRetAddr =
                """
                @R14
                A=M
                0;JMP
                
                """;

        String assembly = frameEnd + retAddr + retValue + spReposition + contextRecover + gotoRetAddr;
        System.out.println("writing return...");
        wr.write(assembly);
        wr.newLine();
    }
}

import java.io.BufferedWriter;
import java.io.IOException;

public class JackCompilationEngine {
    JackTokenizer tk;
    BufferedWriter wr;
    SymbolTable st;
    VMWriter vmWr;
    String currentClass;
    String currentSubroutine;
    String currentSubroutineSort;
    //파일 안에서 중복라벨 피하기
    int labelCount = 0;

    public JackCompilationEngine(JackTokenizer input, BufferedWriter writer){
        tk = input;
        wr = writer;
        st = new SymbolTable();
        vmWr = new VMWriter(wr);
        checkToken();
    }

    public void compileClass() throws IOException {

        eatToken(1, "class");
        currentClass = eatToken(0, "IDENTIFIER");
        eatToken(1, "{");
        while(tk.CurrentToken[1].equals("static") | tk.CurrentToken[1].equals("field")){
            compileClassVarDec();
        }

        //class Table 확인
        st.printClassTables(currentClass);

        while(tk.CurrentToken[1].equals("constructor") | tk.CurrentToken[1].equals("function") | tk.CurrentToken[1].equals("method")){
            compileSubroutineDec();
        }
        eatToken(1,"}");


    }

    public void compileClassVarDec() throws IOException {
        String kind;
        String type;
        String varName;

        //kind
        kind = eatToken(1, "static", "field");

        //type
        if(tk.CurrentToken[0].equals("KEYWORD")){
            type = eatToken(1, "int", "char", "boolean");
        } else {
            type = eatToken(0, "IDENTIFIER");
        }

        //name
        varName = eatToken(0, "IDENTIFIER");

        //symbolTable 추가
        st.define(varName, type, kind);

        while(tk.CurrentToken[1].equals(",")){
            eatToken(1,",");
            varName = eatToken(0, "IDENTIFIER");
            st.define(varName, type, kind);
        }

        eatToken(1, ";");


    }

    public void compileSubroutineDec() throws IOException {

        currentSubroutineSort = eatToken(1, "constructor", "function", "method");

        if(tk.CurrentToken[0].equals("KEYWORD")){
            eatToken(1, "void", "int", "char", "boolean");
        } else if (tk.CurrentToken[0].equals("IDENTIFIER")){
            eatToken(0, "IDENTIFIER");
        } else {

        }

        //subroutine table reset
        st.reset();
        currentSubroutine = eatToken(0, "IDENTIFIER");

        //method의 경우 argument 0 로 넘어온 object에 접근하기 위해 this 변수를 사용함.
        if(currentSubroutineSort.equals("method")){
            st.define("this", currentClass, "arg");
        }
        eatToken(1, "(");
        compileParameterList();
        eatToken(1, ")");

        compileSubroutineBody();

        //subroutine table 확인
        st.printSubroutineTables(currentSubroutine);

    }

    public void compileParameterList() throws IOException {

        if(tk.CurrentToken[1].equals(")")){

        } else {
            compileTypeVarName("arg");

           while(tk.CurrentToken[1].equals(",")){
               eatToken(1, ",");
               compileTypeVarName("arg");

           }
        }

    }

    public String compileTypeVarName(String kind) throws IOException {
        if(tk.CurrentToken[0].equals("KEYWORD")){
            String type = eatToken(1, "int", "char", "boolean");
            String varName = eatToken(0, "IDENTIFIER");
            st.define(varName, type, kind);
            return type;
        } else if (tk.CurrentToken[0].equals("IDENTIFIER")) {
            String type = eatToken(0, "IDENTIFIER");
            String varName = eatToken(0, "IDENTIFIER");
            st.define(varName, type, kind);
            return type;
        } else {
            return "error";
        }
    }

    public void compileSubroutineBody() throws IOException {


        eatToken(1, "{");
        while(tk.CurrentToken[1].equals("var")){
            compileVarDec();
        }

        int nVars = st.varCount("var");
        vmWr.writeFunction(currentClass + "." + currentSubroutine, nVars);
        //생성자의 경우 메모리할당후 base address return 해야함.
        if(currentSubroutineSort.equals("constructor")){
            int nFields = st.varCount("field");
            vmWr.writePush("constant", nFields);
            vmWr.writeCall("Memory.alloc", 1);
            vmWr.writePop("pointer", 0);
        }

        //메서드의 경우 인자로 넘어온 object의 base 주소를 THIS segment와 정렬
        if(currentSubroutineSort.equals("method")){
            vmWr.writePush("argument", 0);
            vmWr.writePop("pointer", 0);
        }

        compileStatements();

        eatToken(1, "}");


    }

    public void compileVarDec() throws IOException {

        eatToken(1, "var");
        String type = compileTypeVarName("var");
        while(tk.CurrentToken[1].equals(",")){
            eatToken(1, ",");
            String name = eatToken(0, "IDENTIFIER");
            st.define(name, type, "var");
        }
        eatToken(1, ";");

    }

    public void compileStatements() throws IOException {

        while(tk.CurrentToken[1].equals("let")
                | tk.CurrentToken[1].equals("if")
                | tk.CurrentToken[1].equals("while")
                | tk.CurrentToken[1].equals("do")
                | tk.CurrentToken[1].equals("return")){
            switch (tk.CurrentToken[1]) {
                case "let" -> {
                    eatToken(1, "let");
                    String varName = eatToken(0, "IDENTIFIER");
                    String[] segAndIndex = st.nameToSegment(varName);
                    if (tk.CurrentToken[1].equals("[")) {
                        vmWr.writePush(segAndIndex[0], Integer.parseInt(segAndIndex[1]));

                        eatToken(1, "[");
                        compileExpression();
                        eatToken(1, "]");

                        vmWr.writeArithmetic("+");

                        eatToken(1, "=");
                        compileExpression();
                        eatToken(1, ";");

                        vmWr.writePop("temp", 0);
                        vmWr.writePop("pointer", 1);
                        vmWr.writePush("temp", 0);
                        vmWr.writePop("that", 0);
                    } else {
                        eatToken(1, "=");
                        compileExpression();
                        eatToken(1, ";");

                        vmWr.writePop(segAndIndex[0], Integer.parseInt(segAndIndex[1]));
                    }
                }
                case "if" -> {
                    String localLabelCount = String.valueOf(labelCount++);

                    eatToken(1, "if");
                    eatToken(1, "(");
                    compileExpression();
                    eatToken(1, ")");
                    vmWr.writeArithmetic("not");
                    vmWr.writeIf("conditionFalse" + localLabelCount);
                    eatToken(1, "{");
                    compileStatements();
                    eatToken(1, "}");

                    if (tk.CurrentToken[1].equals("else")) {
                        vmWr.writeGoto("conditionTrue" + localLabelCount);

                        vmWr.writeLabel("conditionFalse" + localLabelCount);
                        eatToken(1, "else");
                        eatToken(1, "{");
                        compileStatements();
                        eatToken(1, "}");

                        vmWr.writeLabel("conditionTrue" + localLabelCount);
                    } else {
                        vmWr.writeLabel("conditionFalse" + localLabelCount);
                    }
                }
                case "while" -> {
                    String localLabelCount = String.valueOf(labelCount++);

                    eatToken(1, "while");

                    vmWr.writeLabel("loop" + localLabelCount);
                    eatToken(1, "(");
                    compileExpression();
                    eatToken(1, ")");
                    vmWr.writeArithmetic("not");
                    vmWr.writeIf("exit" + localLabelCount);

                    eatToken(1, "{");
                    compileStatements();
                    eatToken(1, "}");
                    vmWr.writeGoto("loop" + localLabelCount);
                    vmWr.writeLabel("exit" + localLabelCount);
                }
                case "do" -> {
                    eatToken(1, "do");
                    compileExpression();
                    eatToken(1, ";");

                    vmWr.writePop("temp", 0);
                }
                default -> {
                    eatToken(1, "return");
                    if (tk.CurrentToken[1].equals(";")) {
                        vmWr.writePush("constant", 0);
                    } else {
                        if(tk.CurrentToken[1].equals("this")){
                            //return this
                            eatToken(1, "this");
                            vmWr.writePush("pointer", 0);
                        } else {
                            compileExpression();
                        }
                    }
                    eatToken(1, ";");
                    vmWr.writeReturn();
                }
            }
        }
    }

    public void compileExpression() throws IOException {

        compileTerm();
        while(tk.CurrentToken[1].equals("+") |
                tk.CurrentToken[1].equals("-") |
                tk.CurrentToken[1].equals("*") |
                tk.CurrentToken[1].equals("/") |
                tk.CurrentToken[1].equals("&") |
                tk.CurrentToken[1].equals("|") |
                tk.CurrentToken[1].equals("<") |
                tk.CurrentToken[1].equals(">") |
                tk.CurrentToken[1].equals("=")){
            String op = eatToken(0, "SYMBOL");
            compileTerm();
            vmWr.writeArithmetic(op);
        }

    }

    public void compileTerm() throws IOException {

        String type = tk.CurrentToken[0];
        String value = tk.CurrentToken[1];
        switch (type) {
            case "INT_CONST" -> {
                String integer = eatToken(0, "INT_CONST");
                vmWr.writePush("constant", Integer.parseInt(integer));
            }
            case "STRING_CONST" -> {
                String stringConst = eatToken(0, "STRING_CONST");
                int n = stringConst.length();
                vmWr.writePush("constant", n);
                vmWr.writeCall("String.new", 1);
                for (int i = 0; i < n; i++) {
                    int asciiCode = (int) stringConst.charAt(i);
                    vmWr.writePush("constant", asciiCode);
                    vmWr.writeCall("String.appendChar", 2);
                }
            }
            case "KEYWORD" -> {
                if (value.equals("true") | value.equals("false") | value.equals("null") | value.equals("this")) {
                    String keyword = eatToken(1, "true", "false", "null", "this");
                    if (keyword.equals("true")) {
                        vmWr.writePush("constant", 1);
                        vmWr.writeArithmetic("neg");
                    } else if (keyword.equals("this")) {
                        vmWr.writePush("pointer", 0);
                    } else {
                        vmWr.writePush("constant", 0);
                    }
                } else {
                    wr.write("compileTerm: syntax error");
                    wr.newLine();
                }
            }
            case "IDENTIFIER" -> {
                String name = eatToken(0, "IDENTIFIER");
                String nextToken = tk.CurrentToken[1];
                switch (nextToken) {
                    case "[" -> {
                        String[] nameToSegment = st.nameToSegment(name);
                        vmWr.writePush(nameToSegment[0], Integer.parseInt(nameToSegment[1]));

                        eatToken(1, "[");
                        compileExpression();
                        eatToken(1, "]");

                        vmWr.writeArithmetic("+");
                        vmWr.writePop("pointer", 1);
                        vmWr.writePush("that", 0);
                    }
                    //메서드콜임. this.method 경우에 해당
                    case "(" -> {
                        //현재 작용중인 object this push
                        //생성자에서 사용하던, 메서드에서 사용하던 현재 object의 base address는 무조건 pointer 0에 있음
                        vmWr.writePush("pointer", 0);

                        eatToken(1, "(");
                        int nArgs = compileExpressionList();
                        eatToken(1, ")");

                        //현재 컴파일중인 클래스 이름 사용..
                        String className = currentClass;
                        vmWr.writeCall(className + "." + name, nArgs + 1);
                    }
                    case "." -> {
                        String varClass = st.typeOf(name);
                        //varName인 경우, 메서드이므로 this object push
                        if (!varClass.equals("NONE")) {
                            String[] nameToSegment = st.nameToSegment(name);
                            vmWr.writePush(nameToSegment[0], Integer.parseInt(nameToSegment[1]));
                        }

                        eatToken(1, ".");
                        String subroutineName = eatToken(0, "IDENTIFIER");

                        eatToken(1, "(");
                        int nArgs = compileExpressionList();
                        eatToken(1, ")");


                        if (!varClass.equals("NONE")) {
                            vmWr.writeCall(varClass + "." + subroutineName, nArgs + 1);
                        } else {
                            //className인경우
                            vmWr.writeCall(name + "." + subroutineName, nArgs);
                        }

                    }
                    default -> {
                        String[] nameToSegment = st.nameToSegment(name);
                        vmWr.writePush(nameToSegment[0], Integer.parseInt(nameToSegment[1]));
                    }
                }
            }
            default -> {
                if (value.equals("(")) {
                    eatToken(1, "(");
                    compileExpression();
                    eatToken(1, ")");
                } else if (value.equals("-") | value.equals("~")) {  //unary operator
                    String unaryOp = eatToken(1, "-", "~");
                    compileTerm();
                    if (unaryOp.equals("-")) {
                        vmWr.writeArithmetic("neg");
                    } else {
                        vmWr.writeArithmetic("not");
                    }
                } else {
                    wr.write("compileTerm: syntax error");
                    wr.newLine();
                }
            }
        }
    }

    public int compileExpressionList() throws IOException {

        int expression_number = 0;
        if(tk.CurrentToken[1].equals(")")){
           //expression 없음
        } else {
            compileExpression();
            expression_number += 1;
            while(tk.CurrentToken[1].equals(",")){
                eatToken(1, ",");
                compileExpression();
                expression_number += 1;
            }
        }
        return expression_number;
    }

    private void writeTag() throws IOException {
        String tokenValue = tk.CurrentToken[1];
        //String의 경우 양 끝의 " " 제거
        if(tk.tokenType().equals("stringConstant")){
            tokenValue = tokenValue.substring(1, tokenValue.length()-1);
        }
        String openTag = "<" + tk.tokenType() + ">";
        String closeTag = "</" + tk.tokenType() + ">";
        String completeTag = switch (tokenValue) {
            case "<" -> openTag + " &lt; " + closeTag;
            case ">" -> openTag + " &gt; " + closeTag;
            case "\"" -> openTag + " &quot; " + closeTag;
            case "&" -> openTag + " &amp; " + closeTag;
            default -> openTag + " " + tokenValue + " " + closeTag;
        };

        wr.write(completeTag);
        wr.newLine();
    }

    private void checkToken(){
        if(tk.hasMoreTokens()){
            tk.advance();
        } else {
            System.out.println("checkToken: there is no token");
        }
    }

    //typeOrValue: 0 이면 type, 1 이면 value
    private String eatToken(int typeOrValue, String... values) throws IOException {
        boolean result = false;
        String token = "error";
        String[] currentToken = tk.CurrentToken;
        for (String value : values) {
            if (currentToken[typeOrValue].equals(value)) {
                result = true;
                token = currentToken[1];
                //writeTag();
                break;
            }
        }
        if(!result){
            wr.write("syntax error");
            wr.newLine();
        }
        checkToken();
        return token;
    }
}

import java.io.BufferedWriter;
import java.io.IOException;

public class JackCompilationEngine {

    JackTokenizer tk;
    BufferedWriter wr;
    public JackCompilationEngine(JackTokenizer input, BufferedWriter writer){
        tk = input;
        wr = writer;
        checkToken();
    }

    public void compileClass() throws IOException {
        wr.write("<class>");
        wr.newLine();

        eatToken(1, "class");
        eatToken(0, "IDENTIFIER");
        eatToken(1, "{");
        while(tk.CurrentToken[1].equals("static") | tk.CurrentToken[1].equals("field")){
            compileClassVarDec();
        }
        while(tk.CurrentToken[1].equals("constructor") | tk.CurrentToken[1].equals("function") | tk.CurrentToken[1].equals("method")){
            compileSubroutineDec();
        }
        eatToken(1,"}");

        wr.write("</class>");
        wr.newLine();
    }

    public void compileClassVarDec() throws IOException {
        wr.write("<classVarDec>");
        wr.newLine();

        eatToken(1, "static", "field");

        if(tk.CurrentToken[0].equals("KEYWORD")){
            eatToken(1, "int", "char", "boolean");
        } else if (tk.CurrentToken[0].equals("IDENTIFIER")){
            eatToken(0, "IDENTIFIER");
        } else {

        }
        eatToken(0, "IDENTIFIER");

        while(tk.CurrentToken[1].equals(",")){
            eatToken(1,",");
            eatToken(0, "IDENTIFIER");
        }

        eatToken(1, ";");

        wr.write("</classVarDec>");
        wr.newLine();
    }

    public void compileSubroutineDec() throws IOException {
        wr.write("<subroutineDec>");
        wr.newLine();

        eatToken(1, "constructor", "function", "method");

        if(tk.CurrentToken[0].equals("KEYWORD")){
            eatToken(1, "void", "int", "char", "boolean");
        } else if (tk.CurrentToken[0].equals("IDENTIFIER")){
            eatToken(0, "IDENTIFIER");
        } else {

        }

        eatToken(0, "IDENTIFIER");
        eatToken(1, "(");
        compileParameterList();
        eatToken(1, ")");
        compileSubroutineBody();

        wr.write("</subroutineDec>");
        wr.newLine();

    }

    public void compileParameterList() throws IOException {
        wr.write("<parameterList>");
        wr.newLine();
        if(tk.CurrentToken[1].equals(")")){

        } else {
            compileTypeVarName();
           while(tk.CurrentToken[1].equals(",")){
               eatToken(1, ",");
               compileTypeVarName();
           }
        }
        wr.write("</parameterList>");
        wr.newLine();
    }

    public void compileTypeVarName() throws IOException {
        if(tk.CurrentToken[0].equals("KEYWORD")){
            eatToken(1, "int", "char", "boolean");
            eatToken(0, "IDENTIFIER");

        } else if (tk.CurrentToken[0].equals("IDENTIFIER")) {
            eatToken(0, "IDENTIFIER");
            eatToken(0, "IDENTIFIER");

        } else {

        }
    }

    public void compileSubroutineBody() throws IOException {
        wr.write("<subroutineBody>");
        wr.newLine();

        eatToken(1, "{");
        while(tk.CurrentToken[1].equals("var")){
            compileVarDec();
        }
        compileStatements();

        eatToken(1, "}");

        wr.write("</subroutineBody>");
        wr.newLine();
    }

    public void compileVarDec() throws IOException {
        wr.write("<varDec>");
        wr.newLine();
        eatToken(1, "var");
        compileTypeVarName();
        while(tk.CurrentToken[1].equals(",")){
            eatToken(1, ",");
            eatToken(0, "IDENTIFIER");
        }
        eatToken(1, ";");
        wr.write("</varDec>");
        wr.newLine();
    }

    public void compileStatements() throws IOException {
        wr.write("<statements>");
        wr.newLine();
        while(tk.CurrentToken[1].equals("let")
                | tk.CurrentToken[1].equals("if")
                | tk.CurrentToken[1].equals("while")
                | tk.CurrentToken[1].equals("do")
                | tk.CurrentToken[1].equals("return")){
            if(tk.CurrentToken[1].equals("let")){
                wr.write("<letStatement>");
                wr.newLine();

                eatToken(1, "let");
                eatToken(0, "IDENTIFIER");
                if(tk.CurrentToken[1].equals("[")){
                    eatToken(1, "[");
                    compileExpression();
                    eatToken(1, "]");
                }
                eatToken(1, "=");
                compileExpression();
                eatToken(1, ";");

                wr.write("</letStatement>");
                wr.newLine();
            } else if (tk.CurrentToken[1].equals("if")){
                wr.write("<ifStatement>");
                wr.newLine();

                eatToken(1, "if");
                eatToken(1, "(");
                compileExpression();
                eatToken(1, ")");
                eatToken(1, "{");
                compileStatements();
                eatToken(1, "}");
                if(tk.CurrentToken[1].equals("else")){
                    eatToken(1, "else");
                    eatToken(1, "{");
                    compileStatements();
                    eatToken(1, "}");
                }

                wr.write("</ifStatement>");
                wr.newLine();
            } else if (tk.CurrentToken[1].equals("while")){
                wr.write("<whileStatement>");
                wr.newLine();

                eatToken(1, "while");
                eatToken(1, "(");
                compileExpression();
                eatToken(1, ")");
                eatToken(1, "{");
                compileStatements();
                eatToken(1, "}");

                wr.write("</whileStatement>");
                wr.newLine();
            } else if (tk.CurrentToken[1].equals("do")) {
                wr.write("<doStatement>");
                wr.newLine();

                eatToken(1, "do");
                compileExpression();
                eatToken(1, ";");

                wr.write("</doStatement>");
                wr.newLine();
            } else {
                wr.write("<returnStatement>");
                wr.newLine();

                eatToken(1, "return");
                if(tk.CurrentToken[1].equals(";")){

                } else {
                    compileExpression();
                }
                eatToken(1, ";");

                wr.write("</returnStatement>");
                wr.newLine();
            }
        }

        wr.write("</statements>");
        wr.newLine();
    }

    public void compileExpression() throws IOException {
        wr.write("<expression>");
        wr.newLine();

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
            eatToken(0, "SYMBOL");
            compileTerm();
        }

        wr.write("</expression>");
        wr.newLine();
    }

    public void compileTerm() throws IOException {
        wr.write("<term>");
        wr.newLine();

        String type = tk.CurrentToken[0];
        String value = tk.CurrentToken[1];
        if(type.equals("INT_CONST")){
            eatToken(0, "INT_CONST");
        } else if(type.equals("STRING_CONST")) {
            eatToken(0, "STRING_CONST");
        } else if(type.equals("KEYWORD")){
            if(value.equals("true") | value.equals("false") | value.equals("null") | value.equals("this")){
                eatToken(1, "true", "false", "null", "this");
            } else {
                wr.write("compileTerm: syntax error");
                wr.newLine();
            }
        } else if(type.equals("IDENTIFIER")){
            eatToken(0, "IDENTIFIER");
            String nextToken = tk.CurrentToken[1];
            if(nextToken.equals("[")){
                eatToken(1, "[");
                compileExpression();
                eatToken(1, "]");
            } else if (nextToken.equals("(")){
                eatToken(1, "(");
                compileExpressionList();
                eatToken(1, ")");
            } else if (nextToken.equals(".")){
                eatToken(1, ".");
                eatToken(0, "IDENTIFIER");
                eatToken(1, "(");
                compileExpressionList();
                eatToken(1, ")");
            } else {
                //varName으로 끝인 경우 그냥 통과
            }
        } else {
            if(value.equals("(")){
                eatToken(1, "(");
                compileExpression();
                eatToken(1, ")");
            } else if (value.equals("-") | value.equals("~")){  //unary operator
                eatToken(1, "-", "~");
                compileTerm();
            } else {
                wr.write("compileTerm: syntax error");
                wr.newLine();
            }
        }

        wr.write("</term>");
        wr.newLine();
    }

    public int compileExpressionList() throws IOException {
        wr.write("<expressionList>");
        wr.newLine();

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

        wr.write("</expressionList>");
        wr.newLine();

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
    private void eatToken(int typeOrValue, String... values) throws IOException {
        boolean result = false;
        String[] currentToken = tk.CurrentToken;
        for (String value : values) {
            if (currentToken[typeOrValue].equals(value)) {
                result = true;
                writeTag();
                break;
            }
        }
        if(!result){
            wr.write("syntax error");
            wr.newLine();
        }
        checkToken();
    }
}

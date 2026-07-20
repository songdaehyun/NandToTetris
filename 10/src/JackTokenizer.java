import javax.swing.plaf.ColorUIResource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.*;

public class JackTokenizer {
    Queue<String> PreProcessedList = new LinkedList<>();
    Queue<String[]> TokenList = new LinkedList<>();
    //List<String> PreProcessedList = new ArrayList<>();
    //List<String[]> TokenList = new ArrayList<>();
    String[] CurrentToken = new String[2];
    Set<Character> SymbolSet = new HashSet<>();
    Set<String> KeywordSet = new HashSet<>();

    public JackTokenizer (String source) throws FileNotFoundException {
        SymbolSet.addAll(Arrays.asList('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'));
        KeywordSet.addAll(Arrays.asList("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true",
                "false", "null", "this", "let", "do", "if", "else", "while", "return"));
        String preProcessedSource = removeNewlineCharAndTab(removeComments3(removeComments2(removeComments1(source))));
        StringBuilder building = new StringBuilder();
        int buildSwitch = 0;
        int stringSwitch = 0;
        for (int i = 0; i < preProcessedSource.length(); i++) {
            char ch = preProcessedSource.charAt(i);
            if (stringSwitch == 1) {
                if (ch == '\"') {
                    building.append(ch);
                    PreProcessedList.add(building.toString());
                    building = new StringBuilder();
                    stringSwitch = 0;
                } else {
                    building.append(ch);
                }
            } else {
                if (ch == '\"') {
                    stringSwitch = 1;
                    building.append(ch);
                } else if (ch == ' ') {
                    if (buildSwitch == 1) {
                        PreProcessedList.add(building.toString());
                        building = new StringBuilder();
                        buildSwitch = 0;
                    } else {
                        //nothing
                    }
                } else if (SymbolSet.contains(ch)) {
                    if (buildSwitch == 1) {
                        PreProcessedList.add(building.toString());
                        PreProcessedList.add(String.valueOf(ch));
                        building = new StringBuilder();
                        buildSwitch = 0;
                    } else {
                        PreProcessedList.add(String.valueOf(ch));
                    }
                } else {
                    buildSwitch = 1;
                    building.append(ch);
                }
            }
        }
    }

    public boolean hasMoreTokens(){
        if(PreProcessedList.isEmpty()){
            return false;
        } else {
            String preToken = PreProcessedList.remove();
            String [] Token = {whatIsType(preToken), preToken.trim()};
            TokenList.add(Token);
            return true;
        }
    }

    public void advance(){
        CurrentToken = TokenList.remove();
    }

    public String tokenType(){
        return switch (CurrentToken[0]) {
            case "SYMBOL" -> "symbol";
            case "KEYWORD" -> "keyword";
            case "STRING_CONST" -> "stringConstant";
            case "INT_CONST" -> "integerConstant";
            default -> "identifier";
        };
    }

    public String keyWord(){
        return CurrentToken[1];
    }

    public char symbol() {
        return CurrentToken[1].charAt(0);
    }

    public String identifier() {
        return CurrentToken[1];
    }

    public int intVal() {
        return Integer.parseInt(CurrentToken[1]);
    }

    public String stringVal(){
        return CurrentToken[1];
    }

    private String whatIsType(String preToken){
        String trimmed = preToken.trim();
        if(trimmed.length() == 1 & SymbolSet.contains(trimmed.charAt(0))){
            return "SYMBOL";
        } else if (KeywordSet.contains(trimmed)){
            return "KEYWORD";
        } else if (trimmed.startsWith("\"") & trimmed.endsWith("\"")) {
            return "STRING_CONST";
        } else {
            try {
                Integer.parseInt(trimmed);
                return "INT_CONST";
            } catch (Exception e){
                return "IDENTIFIER";
            }
        }
    }

    private String removeComments1(String source) {
        int commentIndex = source.indexOf("//");
        if(commentIndex != -1){
            String restSource = source.substring(commentIndex + 2);
            int newLineIndex = restSource.indexOf("\n");
            if(newLineIndex != -1){
                String newSource = source.substring(0, commentIndex) + restSource.substring(newLineIndex + 1);
                return removeComments1(newSource);
            } else {
                return source.substring(0, commentIndex);
            }
        }
        return source;
    }

    private String removeComments2(String source) {
        int openCommentIndex = source.indexOf("/**");
        if(openCommentIndex != -1){
            String restSource = source.substring(openCommentIndex + 3);
            int closeCommentIndex = restSource.indexOf("*/");
            if(closeCommentIndex != -1){
                String newSource = source.substring(0, openCommentIndex) + restSource.substring(closeCommentIndex + 2);
                return removeComments2(newSource);
            }
        }
        return source;
    }

    private String removeComments3(String source) {
        int openCommentIndex = source.indexOf("/*");
        if(openCommentIndex != -1){
            String restSource = source.substring(openCommentIndex + 2);
            int closeCommentIndex = restSource.indexOf("*/");
            if(closeCommentIndex != -1){
                String newSource = source.substring(0, openCommentIndex) + restSource.substring(closeCommentIndex + 2);
                return removeComments3(newSource);
            }
        }
        return source;
    }

    private String removeNewlineCharAndTab(String source) {
       return source.replaceAll("\\R", "").replace("\t", "");
    }
}

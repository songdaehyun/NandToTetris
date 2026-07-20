import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JackAnalyzer {

    public static void main(String[] args) throws IOException {
        // 명령줄 인자가 1개 맞는지 확인
        if (args.length != 1) {
            System.err.println("사용법: java JackAnalyzer input: 파일이름.jack 또는 폴더이름");
            return;
        }

        String path = "C:/Users/user/Documents/NandToTetris/10/test_file/" + args[0];
        File input = new File(path);

        if (!input.exists()) {
            System.err.println("에러: 해당 경로에 파일 또는 디렉터리가 존재하지 않습니다.");
            System.err.println("사용법: java JackAnalyzer input: 파일이름.jack 또는 폴더이름");
            return;
        }

        File[] jackFiles;

        if (input.isDirectory()) {
            // 폴더인 경우: 내부의 .jack 파일들을 필터링
            jackFiles = input.listFiles((dir, name) -> name.endsWith(".jack"));

            if (jackFiles == null || jackFiles.length == 0) {
                System.err.println("해당 폴더 안에 .jack 파일이 없습니다.");
                return;
            }
        } else if (input.isFile() && input.getName().endsWith(".jack")) {
            // 단일 .vm 파일
            jackFiles = new File[]{input}; // 배열 하나로 감싸기
        } else {
            System.err.println("입력은 .jack 파일이거나 .jack 파일을 포함한 디렉터리여야 합니다.");
            return;
        }


        for (File file : jackFiles){
           String readPath = file.getAbsolutePath();
           String source = Files.readString(Path.of(readPath));

           String fileName = file.getName();
           String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
           String newFileName = nameWithoutExtension + "ST.xml"; //Tokenized by Song
           String writePath = new File(file.getParentFile(), newFileName).getAbsolutePath();


           try(BufferedWriter wr = new BufferedWriter(new FileWriter(writePath))){
               JackTokenizer tk = new JackTokenizer(source);
               wr.write("<tokens>");
               wr.newLine();
               while(tk.hasMoreTokens()){
                   tk.advance();
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
                   //System.out.println(completeTag);
                   wr.write(completeTag);
                   wr.newLine();
               }
               wr.write("</tokens>");
               wr.newLine();
           } catch (Exception e){
               System.out.println("처리중 오류 발생");
           }
        }
    }
}


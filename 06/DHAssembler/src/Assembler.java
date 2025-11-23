import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
    public static void main(String[] args) throws IOException {

        // 명령줄 인자가 2개인지 확인
        if (args.length != 1) {
            System.err.println("사용법: java Assembler 파일이름");
            return;
        }
        String readPath = "C:/Users/Administrator/Desktop/NandToTetris/06/asm/" + args[0]; // 첫 번째 인자를 입력 파일 경로로 사용
        String writePath = "C:/Users/Administrator/Desktop/NandToTetris/06/hack/" + args[0].split("\\.")[0] + ".hack";

        SymbolTable table = new SymbolTable();
        // first pass. Label 처리
        try (BufferedReader br = new BufferedReader(new FileReader(readPath))) {
            Parser parser = new Parser(br);
            int lineCounter = 0;
            while(parser.hasMoreLines()){
                String Line = parser.advance();
                String instruction = removeComments(Line);
                if(!instruction.isEmpty()){
                    String type = parser.instructionType();
                   if (type.equals("L")) {
                        String withoutParen = parser.symbol();
                        if(!table.contains(withoutParen)){
                            table.addEntry(withoutParen, lineCounter);
                        }
                   } else if (type.equals("A")){
                       lineCounter++;
                   } else {
                       lineCounter++;
                   }
                }
            }
            System.out.println("Label 처리가 완료되었습니다");

        } catch (IOException e) {
            System.err.println("Label 처리 중 오류가 발생했습니다: " + e.getMessage());
        }


        // Second pass
        try (BufferedReader br = new BufferedReader(new FileReader(readPath));
             BufferedWriter wr = new BufferedWriter(new FileWriter(writePath))) {

            Parser parser = new Parser(br);
            Code code = new Code();
            int symbolCount = 16;

            while(parser.hasMoreLines()){
                String Line = parser.advance();
                String instruction = removeComments(Line);
                if(!instruction.isEmpty()){
                    String result;
                    String type = parser.instructionType();
                    if(type.equals("A")){
                        String withoutAt = parser.symbol();
                        if(table.contains(withoutAt)){
                            int address = table.getAddress(withoutAt);
                            result = "0" + getBinary(String.valueOf(address));

                        }else{
                            try {
                                Integer.parseInt(withoutAt); // 또는 Long.parseLong(s)
                                result = "0" + getBinary(withoutAt);
                            } catch (NumberFormatException e) {
                                table.addEntry(withoutAt, symbolCount);
                                result = "0" + getBinary(String.valueOf(symbolCount));
                                symbolCount++;
                            }

                        }
                        wr.write(result);
                        wr.newLine();
                    }else if (type.equals("L")) {

                    }else if (type.equals("C")){
                        String destFeild = code.dest(parser.dest());
                        String compFeild = code.comp(parser.comp());
                        String jumpFeild = code.jump(parser.jump());
                        result = "111" + compFeild+ destFeild + jumpFeild;
                        wr.write(result);
                        wr.newLine();
                    }
                }
            }
            System.out.println("어셈블리 파일 변환이 완료되었습니다.");

        } catch (IOException e) {
            System.err.println("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

    }

    /**
     * 한 줄의 어셈블리 코드에서 //로 시작하는 주석을 제거합니다.
     * @param line 처리할 코드 한 줄
     * @return 주석이 제거된 코드
     */
    public static String removeComments(String line) {
        int commentIndex = line.indexOf("//");

        if (commentIndex != -1) {
            return line.substring(0, commentIndex).trim();
        }

        return line.trim();
    }

    public static String getBinary(String decimal) {
        //문자열로 된 십진수를 정수(int)로 변환
        int number = Integer.parseInt(decimal);
        int mask = 0b0111111111111111;
        int result = number & mask;
        //Integer.toBinaryString() 메서드를 사용해 이진수로 변환
        return String.format("%15s", Integer.toBinaryString(result)).replace(' ', '0');
    }
}


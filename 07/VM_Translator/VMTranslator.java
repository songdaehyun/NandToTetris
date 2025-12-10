import java.io.*;

public class VMTranslator {
    public static void main(String[] args) throws IOException{
        // 명령줄 인자가 1개 맞는지 확인
        if (args.length != 1) {
            System.err.println("사용법: java VMTranslator 파일이름.vm 또는 폴더이름");
            return;
        }

        String path = "C:/Users/user/Documents/NandToTetris/07/vm/" + args[0];
        File input = new File(path);

        if (!input.exists()) {
            System.err.println("에러: 해당 경로에 파일 또는 디렉터리가 존재하지 않습니다.");
            System.err.println("사용법: java VMTranslator 파일이름.vm 또는 폴더이름");
            return;
        }

        File[] vmFiles;
        String writePath;
        String folderOrFileName;

        if (input.isDirectory()) {
            // 폴더인 경우: 내부의 .vm 파일들을 필터링
            folderOrFileName = input.getName();
            vmFiles = input.listFiles((dir, name) -> name.toLowerCase().endsWith(".vm"));

            if (vmFiles == null || vmFiles.length == 0) {
                System.err.println("해당 폴더 안에 .vm 파일이 없습니다.");
                return;
            }
        } else if (input.isFile() && input.getName().toLowerCase().endsWith(".vm")) {
            // 단일 .vm 파일
            folderOrFileName = input.getName().split("\\.")[0];
            vmFiles = new File[]{input}; // 배열 하나로 감싸기
        } else {
            System.err.println("입력은 .vm 파일이거나 .vm 파일을 포함한 디렉터리여야 합니다.");
            return;
        }
        writePath = "C:/Users/user/Documents/NandToTetris/07/asm/" + folderOrFileName + ".asm";
        System.out.println("처리할 .vm 파일 개수: " + vmFiles.length);


        try(BufferedWriter wr = new BufferedWriter(new FileWriter(writePath))){
            CodeWriter asmWriter = new CodeWriter(wr);
            // 부트스트랩 코드
            if (vmFiles.length > 1) {
                asmWriter.writeInit();// 스택포인터 초기화
                asmWriter.writeCall("Sys.init", 0);// Sys.init 호출
            }

            for (File vmFile : vmFiles) {
                //System.out.println("절대경로: " + vmFile.getAbsolutePath());
                //System.out.println("파일이름: " + vmFile.getName());

                String fileName = vmFile.getName().split("\\.")[0];
                asmWriter.setFileName(fileName);

                String readPath = vmFile.getAbsolutePath();
                // 여기에 각 .vm 파일 처리 로직 추가
                try(BufferedReader br = new BufferedReader(new FileReader(readPath)))
                {
                    Parser vmParser = new Parser(br);
                    while(vmParser.hasMoreLines()){
                        String line = vmParser.advance();
                        if(!line.isEmpty()){
                            System.out.println("Translating " + line);
                            System.out.println("Parsed like " + vmParser.commandType() + " " + vmParser.arg1() + " " + vmParser.arg2());

                            //번역하는 라인 주석으로 쓰기
                            wr.write("//" + line);
                            wr.newLine();

                            String type = vmParser.commandType();
                            switch (type){
                                case "C_ARITHMETIC":
                                    asmWriter.writeArithmetic(vmParser.arg1());
                                    break;
                                case "C_PUSH":
                                    asmWriter.writePushPop("push",vmParser.arg1(), vmParser.arg2());
                                    break;
                                case "C_POP":
                                    asmWriter.writePushPop("pop",vmParser.arg1(), vmParser.arg2());
                                    break;
                                case "C_LABEL":
                                    asmWriter.writeLabel(vmParser.arg1());
                                    break;
                                case "C_GOTO":
                                    asmWriter.writeGoto(vmParser.arg1());
                                    break;
                                case "C_IF":
                                    asmWriter.writeIfGoto(vmParser.arg1());
                                    break;
                                case "C_CALL":
                                    asmWriter.writeCall(vmParser.arg1(), vmParser.arg2());
                                    break;
                                case "C_FUNCTION":
                                    asmWriter.writeFunction(vmParser.arg1(), vmParser.arg2());
                                    break;
                                case "C_RETURN":
                                    asmWriter.writeReturn();
                                    break;
                                default:
                                    System.out.println("not valid command type");
                            }
                        }
                    }
                    System.out.println("VM 파일 변환이 완료되었습니다.");
                } catch (IOException e) {
                    System.err.println("처리 중 오류가 발생했습니다: " + e.getMessage());
                }
            }
        }
    }

}

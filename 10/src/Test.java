import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Test {

    public static void main(String[] args) throws IOException {
        String readPath = "C:/Users/user/Documents/NandToTetris/10/test_file/Main.jack";
        String source = Files.readString(Path.of(readPath));

        System.out.println(source.indexOf("//"));
        System.out.println(source.indexOf("\n"));
        System.out.println( source
                .replace("\r", "\\r")
                .replace("\n", "\\n\n")
                .replace("\t", "\\t"));


    }
}

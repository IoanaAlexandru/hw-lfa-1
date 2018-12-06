import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    static String addNewline(String print) {
        Scanner scanner = new Scanner(print);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            builder.append("\t").append(line).append("\n");
        }
        scanner.close();
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        ImpLexer l = new ImpLexer(new FileReader("test"));

        l.yylex();

        System.out.println(l.main.show());
    }
}
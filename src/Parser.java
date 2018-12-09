import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

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
        ImpLexer l = new ImpLexer(new FileReader("input"));

        l.yylex();

        PrintWriter writer = new PrintWriter("arbore", "UTF-8");
        writer.print(l.getMain().show());
        writer.close();

        writer = new PrintWriter("output", "UTF-8");
        TreeMap<VarNode, Integer> vars = l.interpret();
        for (Map.Entry<VarNode, Integer> e : vars.entrySet()) {
            String value = e.getValue() != null ? e.getValue().toString() : "null";
            writer.println(e.getKey() + "=" + value);
        }
        writer.close();
    }
}
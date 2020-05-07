package parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static String xml_parse (String str) {
        StringBuilder str_out = new StringBuilder("{\"");
        int i = 1;
        while (str.charAt(i) != '>') {
            if (str.charAt(i) != '/')
                str_out.append(str.charAt(i));
            i++;
        }

        if (str.charAt(i - 1) == '/') {
            str_out.append("\": null }");
        } else {
            i++;
            str_out.append("\":\"");
            while (str.charAt(i) != '<') {
                str_out.append(str.charAt(i));
                i++;
            }
            str_out.append("\"}");
        }
        return String.valueOf(str_out);
    }

    private static String json_parse (String str) {
        StringBuilder str_out = new StringBuilder("<");
        StringBuilder Ttl = new StringBuilder();
        int i = 0;
        while(str.charAt(i) != '\"'){
            i++;
        }
        i++;
        while (str.charAt(i) != '\"') {
            Ttl.append(str.charAt(i));
            i++;
        }
        i++;
        while(str.charAt(i) != '\"' && i != str.length() - 1) {
            i++;
        }
        if (str.charAt(i) == '}') {
            str_out.append(Ttl).append("/>");
        } else {
            i++;
            str_out.append(Ttl).append('>');
            while (str.charAt(i) != '\"') {
                str_out.append(str.charAt(i));
                i++;
            }
            str_out.append("</").append(Ttl).append('>');
        }
        return String.valueOf(str_out);
    }
    public static void main(String[] args) throws IOException {
        File file = new File("test.txt");
        Scanner fin = new Scanner(file);
        FileWriter fout = new FileWriter("test1.txt", false);

        String str = fin.nextLine();
        String res = "None";

        char type = str.charAt(0);
        if (type == '<') {
            res = xml_parse(str);
        }
        else if (type == '{') {
            res = json_parse(str);
        }
        fout.write(res);
        fout.close();
    }
}

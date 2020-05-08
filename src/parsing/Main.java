package parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static StringBuilder xml_parse (StringBuilder str) {
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
        return str_out;
    }

    private static StringBuilder json_parse (StringBuilder str) {
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
        return str_out;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("test.txt");
        Scanner fin = new Scanner(file);
        FileWriter fout = new FileWriter("output.txt", false);
        StringBuilder str = new StringBuilder();
        StringBuilder res = new StringBuilder();
        while (fin.hasNext()){
            str.append(fin.nextLine());
        }
        fin.close();

        final String[] regexes = {
                "\\s*<[^&<>\"']*>[^&<>\"']*</[^&<>\"']*>\\s*",
                                            /* <host>127.0.0.1</host> */
                "\\s*\\{\\s*\"[^&<>\"']*\"\\s*:\\s*\"[^&<>\"']*\"\\s*}\\s*",
                                            /* {"host":"127.0.0.1"} */
                "<[^&<>\"']*\\s+[^&<>\"']*\\s*=\\s*\"[^&<>\"']*\">[^&<>\"']*</[^&<>\"']*>",
                                            /* <employee department = "manager">Garry Smith</employee> */
                "\\{\"[^&<>\"']*\"\\s*:\\s*\\{[\\S\\s]*"
                                            /*{"employee" : {"@department" : "manager", "#employee" : "Garry Smith"} */
        };
        if (str.toString().matches(regexes[0])) { // XML
            res = xml_parse(str);
        } else if(str.toString().matches(regexes[1])) { //JSON
            res = json_parse(str);
        } else if (str.toString().matches(regexes[2])) { //Attribute XML
            System.out.println("XML 2: " + str);
        } else if (str.toString().matches(regexes[3])) { //Attribute JSON
            System.out.println("JSON 2: " + str);
        }
        else {
            System.out.println("default");
        }

        fout.write(res.toString());
        fout.close();
    }
}

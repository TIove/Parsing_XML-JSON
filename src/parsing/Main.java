package parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class pair<T, V> {
    public T first = null;
    public V second = null;
    public pair(T x, V y) {
        this.first = x;
        this.second = y;
    }
    public T getFirst() {
        return first;
    }
    public V getSecond() {
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}

public class Main {
    private static int it = 0;
    private static StringBuilder str = new StringBuilder();

    private static void miss_spc() {
        while (str.charAt(it) == ' ') {
            it++;
        }
    }
    private static pair get_pair() {
        pair<String, String> ans = new pair<>(null, null);
        ans.setFirst(get_word(it, false));
        miss_spc();
        it ++;
        miss_spc();
        it++;
        ans.setSecond(get_word(it,false));
        return ans;
    }

    private static String get_word (int iter, boolean is_ignore_spc) {
        boolean f = false;
        if (iter == it) {
            f = true;
        }
        StringBuilder str_out = new StringBuilder();
        if (is_ignore_spc) {
            while (str.charAt(iter) != '<' && str.charAt(iter) != '\"' && str.charAt(iter) != '>') {
                str_out.append(str.charAt(iter));
                iter++;
            }
        } else {
            while (str.charAt(iter) != '<' && str.charAt(iter) != '\"' && str.charAt(iter) != '>' && str.charAt(iter) != ' ') {
                str_out.append(str.charAt(iter));
                iter++;
            }
        }
        if (f){
            it = iter;
        }
        return String.valueOf(str_out);
    }

    private static StringBuilder xml_parse () {
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

    private static StringBuilder json_parse () {
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

    private static StringBuilder xml_attribute () {/*<employee department = "manager">Garry Smith</employee> */
        StringBuilder str_out = new StringBuilder("{\n\t\"");           /*{"employee" : {"@department" : "manager", "#employee" : "Garry Smith"}*/
        it = 0;
        Pattern pattern = Pattern.compile(">\\s*");
        Matcher matcher = pattern.matcher(str);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        miss_spc();
        it++;
        pair<String, String> title = new pair<>(null, null);
        if (matcher.find()) {
            int match = matcher.end();
            if (match != str.length() - 1) {
                title.setFirst(get_word(it, false));
                title.setSecond(get_word(match, true));
            } else {
                title.setFirst(get_word(it, false)); //second is null
            }
        }
        miss_spc();
        str_out.append(title.getFirst()).append("\" : {\n\t\t");
        while (str.charAt(it) != '>') {
            pair<String, String> buf = get_pair();
            map.put(buf.getFirst(), buf.getSecond());
            it++;
            miss_spc();
        }
        for (var entry : map.entrySet()) {
            str_out.append("\"@").append(entry.getKey()).append("\" : ").append(entry.getValue()).append("\",\n\t\t");
        }
        str_out.append("\"#").append(title.getFirst()).append("\" : \"").append(title.getSecond()).append("\"\n\t}\n}");
        return str_out;
    }
    public static void main(String[] args) throws IOException {
        File file = new File("test.txt");
        Scanner fin = new Scanner(file);
        FileWriter fout = new FileWriter("output.txt", false);
        StringBuilder res = new StringBuilder();
        while (fin.hasNext()) {
            str.append(fin.nextLine());
        }
        fin.close();

        final String[] regexes = {
                "\\s*<[^&<>\"']*>[^&<>\"']*</[^&<>\"']*>\\s*",
                                            /* <host>127.0.0.1</host> */
                "\\s*\\{\\s*\"[^&<>\"']*\"\\s*:\\s*\"[^&<>\"']*\"\\s*}\\s*",
                                            /* {"host":"127.0.0.1"} */
                "\\s*<[^&<>\"']*\\s+[^&<>\"']*\\s*=\\s*\"[^&<>\"']*\">[^&<>\"']*</[^&<>\"']*>\\s*",
                                            /* <employee department = "manager">Garry Smith</employee> */
                "\\s*\\{\"[^&<>\"']*\"\\s*:\\s*\\{[\\S\\s]*"
                                            /*{"employee" : {"@department" : "manager", "#employee" : "Garry Smith"} */
        };
        if (str.toString().matches(regexes[0])) { // XML
            res = xml_parse();
        } else if(str.toString().matches(regexes[1])) { //JSON
            res = json_parse();
        } else if (str.toString().matches(regexes[2])) { //Attribute XML
            res = xml_attribute();
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

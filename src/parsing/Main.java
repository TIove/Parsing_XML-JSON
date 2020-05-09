package parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class pair<T, V> {
    public T first;
    public V second;
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
        if (str.length() - 1 < it) {
            return;
        }
        while (str.charAt(it) == ' ') {
            it++;
        }
    }
    private static pair get_pair(boolean is_spc_first, boolean is_spc_second) {
        pair<String, String> ans = new pair<>(null, null);
        ans.setFirst(get_word(it, is_spc_first));/*{"employee" : {"@department" : "manager", "#employee" : "Garry Smith"} */
        if (str.charAt(it) != ' ') {             /*<employee department = "manager">Garry Smith</employee> */
            it++;
        }
        miss_spc();
        it++;
        miss_spc();
        if (str.charAt(it) == '\"') {
            it++;
            ans.setSecond(get_word(it, is_spc_second));
            return ans;
        }
        ans.setSecond(get_word(it, true));
        return ans;
    }

    private static String get_word (int iter, boolean is_ignore_spc) {
        boolean f = false;
        if (iter == it) {
            f = true;
        }
        StringBuilder str_out = new StringBuilder();
        if (is_ignore_spc) {
            while (str.charAt(iter) != '<' && str.charAt(iter) != '\"' && str.charAt(iter) != '>' && str.charAt(iter) != ',' && str.charAt(iter) != '}') {
                str_out.append(str.charAt(iter));
                iter++;
            }
        } else {
            while (str.charAt(iter) != '<' && str.charAt(iter) != '\"' && str.charAt(iter) != '>' && str.charAt(iter) != ' ' && str.charAt(iter) != ',' && str.charAt(iter) != '}') {
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
        String title;
        it = 1;
        while(str.charAt(it) != '\"'){
            it++;
        }
        it++;
        title = get_word(it, true);
        it++;
        while(str.charAt(it) != '\"' && it != str.length() - 1) {
            it++;
        }
        if (str.charAt(it) == '}') {
            str_out.append(title).append("/>");
        } else {
            it++;
            str_out.append(title).append('>');
            while (str.charAt(it) != '\"') {
                str_out.append(str.charAt(it));
                it++;
            }
            str_out.append("</").append(title).append('>');
        }
        return str_out;
    }

    private static StringBuilder xml_attribute () {
        StringBuilder str_out = new StringBuilder("{\n\t\"");
        it = 0;
        Pattern pattern = Pattern.compile(">\\s*");
        Matcher matcher = pattern.matcher(str);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        miss_spc();
        it++;
        pair<String, String> title = new pair<>(null, null);
        if (matcher.find()) {
            int match = matcher.end();
            if (match != str.length()) {
                title.setFirst(get_word(it, false));
                title.setSecond(get_word(match, true));
            } else {
                title.setFirst(get_word(it, false)); //second is null
            }
        }
        miss_spc();
        str_out.append(title.getFirst()).append("\" : {\n\t\t");
        while (str.charAt(it) != '>' && str.charAt(it) != '/') {
            pair<String, String> buf = get_pair(false, false);
            map.put(buf.getFirst(), buf.getSecond());
            it++;
            miss_spc();
        }
        for (var entry : map.entrySet()) {
            str_out.append("\"@").append(entry.getKey()).append("\" : \"").append(entry.getValue()).append("\",\n\t\t");
        }
        str_out.append("\"#").append(title.getFirst()).append("\" : ");
        if (title.getSecond() == null) {
            str_out.append("null\n\t}\n}");
        } else {
            str_out.append("\"").append(title.getSecond()).append("\"\n\t}\n}");
        }

        return str_out;
    }

    private static StringBuilder json_attribute () {
        StringBuilder str_out = new StringBuilder("<");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        it = 1;
        while(str.charAt(it) != '\"') {
            it++;
        }
        it++;
        String title = get_word(it, true);
        while (str.charAt(it) != '{') {
            it++;
        }
        it ++;
        miss_spc();
        it ++;
        while (str.charAt(it) != ' ' && str.charAt(it) != '}') {
            pair<String, String> buf = get_pair(true, true);
            map.put(buf.getFirst(), buf.getSecond());
            if (it + 3 > str.length() - 1)
                break;
            it += 2;
            miss_spc();
            it++;
        }
        boolean is_title_name = false;
        str_out.append(title);
        for (var entry : map.entrySet()) {
            if (!entry.getKey().matches("#[\\s\\S]*")) {
                str_out.append(' ').append(entry.getKey().substring(1)).append(" = \"").append(entry.getValue()).append('\"');
            } else
                if (!entry.getValue().matches("\\s*null\\s*")) {
                    is_title_name = true;
            }
        }
        if (is_title_name) {
            str_out.append('>').append(map.get('#' + title)).append("</").append(title).append('>');
        } else {
            str_out.append(" />");
        }

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
                "\\s*<[^&<>\"']*\\s+[[^&<>\"']*=[\\s*\"[^&<>\"']*\"\\s*]*]*/*>[\\s\\S]*",
                                            /* <employee department = "manager">Garry Smith</employee> */
                "\\s*\\{\\s*\"[^&<>\"']*\"\\s*:\\s*\\{[\\S\\s]*"
                                            /*{"employee" : {"@department" : "manager", "#employee" : "Garry Smith"} */
        };
        if (str.toString().matches(regexes[0])) { // XML
            res = xml_parse();
        } else if(str.toString().matches(regexes[1])) { //JSON
            res = json_parse();
        } else if (str.toString().matches(regexes[2])) { //Attribute XML
            res = xml_attribute();
        } else if (str.toString().matches(regexes[3])) { //Attribute JSON
            res = json_attribute();
        }
        else {
            System.out.println("default");
        }

        fout.write(res.toString());
        fout.close();
    }
}

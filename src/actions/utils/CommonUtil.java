package actions.utils;

import com.intellij.openapi.project.Project;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    /**
     * 把粘贴的字符串分行按空格转换成列表
     */
    @NotNull
    public static List<List<String>> convertToList(String str) {
        List<List<String>> modelList = new ArrayList<>();
        //首先按行分割
        String[] lines = str.split("\n");
        for (String singleLine : lines) {
            if (TextUtils.isEmpty(singleLine)) {
                continue;
            }
            String[] stringArr = singleLine.split("\t");
            //如果该行只有一个字符串，认为是上一行的注释有多行
            List<String> lastLine;
            if (modelList.size() == 0) {
                lastLine = new ArrayList<>();
            } else {
                lastLine = modelList.get(modelList.size() - 1);
            }
            if (stringArr.length == 1 && lastLine.size() != 0) {
                String newLine = lastLine.get(lastLine.size() - 1) + "\n*\t" + stringArr[0];
                //多行注释，复制过来会带引号，去掉它们
                lastLine.set(lastLine.size() - 1, newLine.replaceAll("\"",""));
            } else {
                List<String> singleLineList = new ArrayList<>();
                for (String s : stringArr) {
                    if (!TextUtils.isEmpty(s)) {
                        singleLineList.add(s);
                    }
                }
                modelList.add(singleLineList);
            }
        }
        return modelList;
    }

    /**
     * 从AndroidManifest.xml文件中获取当前app的包名
     *
     * @return
     */
    public static String getPackageName(Project project) {
        String package_name = "";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/app/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                package_name = element.getAttribute("package");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return package_name;
    }

    /**
     * 服务端契约中的类型跟我们用的类型有差别，这里修正一下
     * bool -> boolean
     * string -> String
     * decimal -> double
     * list -> List
     */
    public static String modifyClassType(List<String> strings) {
        if (strings.size() > 1) {
            String type = strings.get(1);
            if ("boolean".contains(type)) {
                return "boolean";
            } else if ("decimal".equalsIgnoreCase(type)) {
                return "double";
            } else if (type.contains("string")) {
                return type.replace("string", "String");
            } else if ("list".equalsIgnoreCase(type)) {
                return "List";
            } else {
                return type;
            }
        }
        return "";
    }

    public static void appendAnnotation(List<String> strings, StringBuilder sb) {
        if (strings.size() == 3) {
            sb.append("/**\n\t*\t").append(strings.get(2)).append("\n*/\n");
        }
    }

    public static void appendField(List<String> strings, StringBuilder sb) {
        if (strings.size() == 0) {
            return;
        }
        sb.append(" ").append(strings.get(0)).append(";");
    }

    public static String appendFieldType(List<String> strings, StringBuilder sb) {
        return modifyClassType(strings);
    }

    public static void appendMemberType(String memberType, StringBuilder sb) {
        if (memberType == null) {
            memberType = "public static";
        }
        sb.append(memberType).append(" ");
    }
}

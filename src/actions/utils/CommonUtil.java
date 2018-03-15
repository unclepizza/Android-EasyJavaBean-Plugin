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

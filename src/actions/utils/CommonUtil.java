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
        String[] lines = str.split("\n");
        for (String singleLine : lines) {
            if (TextUtils.isEmpty(singleLine)) {
                continue;
            }
            String[] stringArr = singleLine.split("\t");
            List<String> singleLineList = new ArrayList<>();
            for (String s : stringArr) {
                if (!TextUtils.isEmpty(s)) {
                    singleLineList.add(s);
                }
            }
            modelList.add(singleLineList);
        }
        return modelList;
    }

    /**
     * 从AndroidManifest.xml文件中获取当前app的包名
     * @return
     */
    public static String getPackageName(Project project) {
        String package_name = "";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/app/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element element = (Element) node;
                package_name = element.getAttribute("package");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return package_name;
    }
}

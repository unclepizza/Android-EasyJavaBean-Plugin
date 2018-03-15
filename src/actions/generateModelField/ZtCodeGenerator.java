package actions.generateModelField;

import actions.utils.CommonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自己实现解析和拼接逻辑
 */
public class ZtCodeGenerator implements ICodeGenerator {

    @Override
    public void onSplice(List<List<String>> fields, Project project, PsiClass psiClass, String memberType) {
        if (psiClass == null) {
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        for (List<String> strings : fields) {
            StringBuilder sb = new StringBuilder();
            if (strings.size() == 0 || strings.size() == 1) {
                continue;
            }
            //注释
            CommonUtil.appendAnnotation(strings, sb);
            //字段类型：int，字段类型不为空，再追加成员类型
            String fieldType = CommonUtil.appendFieldType(strings, sb);
            if (!TextUtils.isEmpty(fieldType)) {
                //成员类型：private
                CommonUtil.appendMemberType(memberType, sb);
                sb.append(fieldType);
            }
            //字段名
            CommonUtil.appendField(strings, sb);
            PsiField field = factory.createFieldFromText(sb.toString(), psiClass);
            psiClass.add(field);
        }
    }

    @Override
    public List<List<String>> onParse(String str) {
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
}

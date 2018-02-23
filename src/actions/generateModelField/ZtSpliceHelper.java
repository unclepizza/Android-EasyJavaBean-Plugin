package actions.generateModelField;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ZtSpliceHelper implements ISpliceField {
    /**
     * 成员变量类型：private or public
     */
    private String mType;

    @Override
    public void onSplice(List<List<String>> fields, Project project, PsiClass psiClass, boolean isSerializable,
                         String memberType) {
        this.mType = memberType;
        if (psiClass == null) {
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        ArrayList<PsiField> psiFields = new ArrayList<>();
        String ss = "private static final long serialVersionUID = 1L;";
        if (isSerializable) {
            PsiField field = factory.createFieldFromText(ss, psiClass);
            psiFields.add(field);
        }
        for (List<String> strings : fields) {
            StringBuilder sb = new StringBuilder();
            if (strings.size() == 0 || strings.size() == 1) {
                continue;
            }
            //注释
            appendAnnotation(strings, sb);
            //字段类型：int，字段类型不为空，再追加成员类型
            String fieldType = appendFieldType(strings, sb);
            if (!TextUtils.isEmpty(fieldType)) {
                //成员类型：private
                appendMemberType(sb);
                sb.append(fieldType);
            }
            //字段名
            appendField(strings, sb);
            PsiField field = factory.createFieldFromText(sb.toString(), psiClass);
            psiClass.add(field);

        }
    }

    private void appendAnnotation(List<String> strings, StringBuilder sb) {
        if (strings.size() == 3) {
            sb.append("/**\n *  ").append(strings.get(2)).append("\n*/\n");
        }
    }

    private void appendField(List<String> strings, StringBuilder sb) {
        if (strings.size() == 0) {
            return;
        }
        sb.append(" ").append(strings.get(0)).append(";");
    }

    private String appendFieldType(List<String> strings, StringBuilder sb) {
        String classType = modifyClassType(strings);
        return classType;
    }

    private void appendMemberType(StringBuilder sb) {
        if (mType == null) {
            mType = "private";
        }
        sb.append(mType).append(" ");
    }

    /**
     * 服务端契约中的类型跟我们用的类型有差别，这里修正一下
     * bool -> boolean
     * string -> String
     * decimal -> double
     */
    private String modifyClassType(List<String> strings) {
        if (strings.size() > 1) {
            String type = strings.get(1);
            if ("boolean".contains(type)) {
                return "boolean";
            } else if ("decimal".equalsIgnoreCase(type)) {
                return "double";
            } else if (type.contains("string")) {
                return type.replace("string", "String");
            } else {
                return type;
            }
        }
        return "";
    }
}

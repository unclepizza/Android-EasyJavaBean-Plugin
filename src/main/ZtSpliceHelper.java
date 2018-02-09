package main;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
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
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        ArrayList<PsiField> psiFields = new ArrayList<>();
        String ss = "private static final long serialVersionUID = 1L;";
        if (isSerializable) {
            PsiField field = factory.createFieldFromText(ss, psiClass);
            psiFields.add(field);
        }
        for (List<String> strings : fields) {
            StringBuilder sb = new StringBuilder();
            if (strings.size() == 0) {
                continue;
            }
            //注释
            if (strings.size() == 3) {
                sb.append("/**\n *  ").append(strings.get(2)).append("\n*/\n");
            }
            //成员类型：private
            appendMemberType(sb);
            //字段类型：int
            appendFieldType(strings, sb);
            //字段名
            appendField(strings, sb);
            PsiField field = factory.createFieldFromText(sb.toString(), psiClass);
            psiFields.add(field);
        }

        for (PsiField psiField : psiFields) {
            psiClass.add(psiField);
        }
    }

    private void appendField(List<String> strings, StringBuilder sb) {
        if (strings.size() == 0) {
            return;
        }
        sb.append(" ").append(strings.get(0)).append(";");
    }

    private void appendFieldType(List<String> strings, StringBuilder sb) {
        sb.append(modifyClassType(strings));
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
            if ("string".equalsIgnoreCase(type)) {
                return "String";
            } else if ("boolean".contains(type)) {
                return "boolean";
            } else if ("decimal".equalsIgnoreCase(type)) {
                return "double";
            } else if ("List<string>".equalsIgnoreCase(type)) {
                return "List<String>";
            } else {
                return type;
            }
        }
        return "";
    }
}

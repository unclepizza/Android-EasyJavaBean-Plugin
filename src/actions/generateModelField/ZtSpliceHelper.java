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
 * 自己实现拼接逻辑
 */
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
            CommonUtil.appendAnnotation(strings, sb);
            //字段类型：int，字段类型不为空，再追加成员类型
            String fieldType = CommonUtil.appendFieldType(strings, sb);
            if (!TextUtils.isEmpty(fieldType)) {
                //成员类型：private
                CommonUtil.appendMemberType(mType, sb);
                sb.append(fieldType);
            }
            //字段名
            CommonUtil.appendField(strings, sb);
            PsiField field = factory.createFieldFromText(sb.toString(), psiClass);
            psiClass.add(field);
        }
    }
}

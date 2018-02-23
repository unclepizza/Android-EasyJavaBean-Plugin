package actions.generateModelField;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.List;

public interface ISpliceField {
    /**
     * @param fields 格式化后的，各行的文本元组
     * @param project 当前工程
     * @param psiClass 当前类
     * @param isSerializable 是否序列化
     * @param memberType 成员变量类型
     */
    void onSplice(List<List<String>> fields, Project project, PsiClass psiClass, boolean isSerializable, String memberType);
}

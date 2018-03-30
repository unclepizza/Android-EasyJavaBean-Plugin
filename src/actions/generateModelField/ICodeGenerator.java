package actions.generateModelField;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.List;

public interface ICodeGenerator {
    /**
     * 解析字符串
     * @param str 粘贴的字符串
     * @return 各行字符串
     */
    List<List<String>> onParse(String str);

    /**
     * @param fields 格式化后的，各行的文本元组
     * @param project 当前工程
     * @param psiClass 当前类
     * @param memberType 成员变量类型
     */
    void onSplice(List<List<String>> fields, Project project, PsiClass psiClass, String memberType);
}

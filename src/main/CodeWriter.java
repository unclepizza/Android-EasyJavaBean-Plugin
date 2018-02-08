package main;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaok
 * @description
 * @date 2018/02/07 11:18
 */
public class CodeWriter {
    private String type = "private";
    private static CodeWriter INSTANCE;
    private List<List<String>> attributeList;

    public static CodeWriter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CodeWriter();
        }
        return INSTANCE;
    }

    public String write(AnActionEvent event, List<List<String>> list, String type, boolean isSerializable) {
        this.type = type;
        this.attributeList = list;
        //获取当前编辑的文件
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return "PsiFile can not be null";
        }
        final String[] errorMessage = {""};
        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (editor == null) {
                errorMessage[0] = "Editor can not be null!";
                return;
            }
            Project project = editor.getProject();
            if (project == null) {
                errorMessage[0] = "Project can not be null!";
                return;
            }
            //获取当前编辑的class对象
            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (psiClass == null) {
                return;
            }
            if (psiClass.getNameIdentifier() == null) {
                return;
            }
            errorMessage[0] = write(project, psiClass, isSerializable);
        });
        return errorMessage[0];
    }

    /**
     * 根据粘贴的字段，自动生成Model代码
     */
    public String write(Project project, PsiClass psiClass, boolean isSerializable) {
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        ArrayList<PsiField> psiFields = new ArrayList<>();
        String ss = "private static final long serialVersionUID = 1L;";
        try {
            if (isSerializable) {
                PsiField field = factory.createFieldFromText(ss, psiClass);
                psiFields.add(field);
            }
            for (List<String> strings : attributeList) {
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
        } catch (Exception e) {
            return e.getMessage();
        }

        for (int i = 0; i < psiFields.size(); i++) {
            psiClass.add(psiFields.get(i));
        }
        return "success";
    }

    private StringBuilder appendField(List<String> strings, StringBuilder sb) {
        if (strings.size() == 0) {
            return sb;
        }
        return sb.append(" ").append(strings.get(0)).append(";");
    }

    private StringBuilder appendFieldType(List<String> strings, StringBuilder sb) {
        return sb.append(modifyClassType(strings));
    }

    private StringBuilder appendMemberType(StringBuilder sb) {
        if (type == null) {
            type = "private";
        }
        return sb.append(type).append(" ");
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

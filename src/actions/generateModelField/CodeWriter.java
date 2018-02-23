package actions.generateModelField;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

/**
 * @author gaok
 * @description
 * @date 2018/02/07 11:18
 */
public class CodeWriter {

    private static CodeWriter INSTANCE;

    public static CodeWriter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CodeWriter();
        }
        return INSTANCE;
    }

    /**
     * 自定义实现文本拼接逻辑
     */
    private ISpliceField spliceHelper = new ZtSpliceHelper();

    String write(AnActionEvent event, List<List<String>> list, String type, boolean isSerializable) {
        //获取当前编辑的文件
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return "当前编辑文件不能为空！";
        }
        final String[] resultMessage = {"success"};
        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (editor == null) {
                resultMessage[0] = "Editor can not be null!";
                return;
            }
            Project project = editor.getProject();
            if (project == null) {
                resultMessage[0] = "当前工程不能为空！";
                return;
            }
            //获取当前编辑的class对象
            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (psiClass == null) {
                resultMessage[0] = "必须先新建class文件！";
                return;
            }
            if (psiClass.getNameIdentifier() == null) {
                return;
            }
            PsiReferenceList implementsList = psiClass.getImplementsList();

            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            try {
                spliceHelper.onSplice(list, project, psiClass, isSerializable, type);
            } catch (Exception e) {
                resultMessage[0] = e.getMessage() + "\n" + "请检查复制的文本格式是否正确！";
            }
        });
        return resultMessage[0];
    }
}

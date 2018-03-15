package actions.generateJavaBean;

import actions.generateModelField.ICodeGenerator;
import actions.generateModelField.ZtCodeGenerator;
import actions.utils.CommonUtil;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据类名和文本生成Java Bean文件
 */
public class GenerateJavaBeanAction extends AnAction {
    private AnActionEvent actionEvent;
    /**
     * 成员变量类型：private or public
     */
    private String mType;

    private GenerateJavaBeanDialog.OnClickListener mClickListener = new GenerateJavaBeanDialog.OnClickListener() {

        @Override
        public void onGenerate(String className, String pasteStr, boolean serializable, String
                member) {
            String result;
            try {
                result = generateFile(className, pasteStr, serializable, member);
            } catch (Exception e) {
                result = e.getMessage();
                if (TextUtils.isEmpty(result)) {
                    result = e.toString();
                }
            }
            if (!TextUtils.isEmpty(result)) {
                Messages.showMessageDialog(result, "Error", Messages.getInformationIcon());
            }
        }

        @Override
        public void onCancel() {
            //nothing…
        }
    };

    private String generateFile(String fileName, String pasteStr, boolean serializable, String member) {
        JavaDirectoryService directoryService = JavaDirectoryService.getInstance();
        //当前工程
        Project project = actionEvent.getProject();
        //当前路径
        IdeView ideView = actionEvent.getRequiredData(LangDataKeys.IDE_VIEW);
        PsiDirectory directory = ideView.getOrChooseDirectory();
        //检查文件是否已有
        if (directory.findFile(fileName + ".java") != null) {
            return "Generation failed, " + fileName + " already exists";
        }
        //模板文件参数
        Map<String, String> map = new HashMap<>();
        map.put("NAME", fileName);
        if (serializable) {
            map.put("INTERFACES", "implements Serializable");
        } else {
            map.put("INTERFACES", "");
        }
        map.put("PACKAGE", CommonUtil.getPackageName(project));
        //使用模板生成文件
        PsiClass psiClass = directoryService.createClass(directory, fileName, "GenerateFileByString", false, map);
        WriteCommandAction.runWriteCommandAction(project, () -> generateModelField(pasteStr, member, project, psiClass, new ZtCodeGenerator()));
        return "";
    }

    private void generateModelField(String pasteStr, String member, Project project, PsiClass psiClass, ICodeGenerator codeGenerator) {
        this.mType = member;
        if (psiClass == null) {
            return;
        }
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        List<List<String>> modelList = codeGenerator.onParse(pasteStr);
        for (List<String> strings : modelList) {
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.actionEvent = e;
        GenerateJavaBeanDialog dialog = new GenerateJavaBeanDialog();
        dialog.setOnClickListener(mClickListener);
        dialog.setTitle("Generate Java Bean By String");
        //自动调整对话框大小
        dialog.pack();
        //设置对话框跟随当前windows窗口
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(e.getProject()));
        dialog.setVisible(true);
    }
}

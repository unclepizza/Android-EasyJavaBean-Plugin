package actions.generateJavaBean;

import actions.generateModelField.CodeWriter;
import actions.utils.CommonUtil;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
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
        if(serializable){
            map.put("INTERFACES", "implements Serializable");
        }else{
            map.put("INTERFACES", "");
        }
        map.put("PACKAGE", CommonUtil.getPackageName(project));
        //使用模板生成文件
        PsiClass psiClass = directoryService.createClass(directory, fileName, "GenerateFileByString", false, map);
        //根据粘贴的文本生成字段
        List<List<String>> modelList = CommonUtil.convertToList(pasteStr);
        WriteCommandAction.runWriteCommandAction(project, () -> generateModelField(serializable, member, project, psiClass, modelList));
        return "";
    }

    private void generateModelField(boolean serializable, String member, Project project, PsiClass psiClass, List<List<String>> modelList) {
        this.mType = member;
        if (psiClass == null) {
            return;
        }
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        ArrayList<PsiField> psiFields = new ArrayList<>();
        String ss = "private static final long serialVersionUID = 1L;";
        if (serializable) {
            PsiField field = factory.createFieldFromText(ss, psiClass);
            psiFields.add(field);
        }
        for (List<String> strings : modelList) {
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.actionEvent = e;
        GenerateJavaBeanDialog dialog = new GenerateJavaBeanDialog();
        dialog.setOnClickListener(mClickListener);
        dialog.setTitle("Generate Java Bean By String");
        //默认设置Serializable为false，即不产生：“private static final long serialVersionUID = 1L;”
        dialog.setCbSerializable(false);
        //自动调整对话框大小
        dialog.pack();
        //设置对话框跟随当前windows窗口
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(e.getProject()));
        dialog.setVisible(true);
    }

}

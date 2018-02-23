package actions.generateModelField;

import actions.utils.CommonUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据文本在类中生成Java Bean字段
 */
public class GenerateModelFieldAction extends AnAction {
    private AnActionEvent anActionEvent;

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.anActionEvent = e;
        GenerateModelFieldDialog generateDialog = new GenerateModelFieldDialog();
        generateDialog.setOnClickListener(mClickListener);
        generateDialog.setTitle("Generate Model By String");
        //默认设置Serializable为false，即不产生：“private static final long serialVersionUID = 1L;”
        generateDialog.setCbSerializable(false);
        //自动调整对话框大小
        generateDialog.pack();
        //设置对话框跟随当前windows窗口
        generateDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(e.getProject()));
        generateDialog.setVisible(true);
    }

    private GenerateModelFieldDialog.OnClickListener mClickListener = new GenerateModelFieldDialog.OnClickListener() {

        @Override
        public void onGenerate(String str, String memberType, boolean serializable) {
            generateModel(str, memberType, serializable);
        }

        @Override
        public void onCancel() {
            //nothing...
        }
    };

    private void generateModel(String str, String memberType, boolean isSerializable) {
        List<List<String>> modelList = CommonUtil.convertToList(str);
        String result = CodeWriter.getInstance().write(anActionEvent, modelList, memberType, isSerializable);
        //如果有错误信息，弹出来
        if (!TextUtils.isEmpty(result) && !result.equalsIgnoreCase("success")) {
            Messages.showMessageDialog(result, "Error", Messages.getInformationIcon());
        }
    }


}

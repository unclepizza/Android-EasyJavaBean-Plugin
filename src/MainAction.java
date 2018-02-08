import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaok
 * @description
 * @date 2018/02/07 12:34
 */
public class MainAction extends AnAction {
    private AnActionEvent anActionEvent;

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.anActionEvent = e;
        GenerateDialog generateDialog = new GenerateDialog();
        generateDialog.setOnClickListener(mClickListener);
        generateDialog.setTitle("GenerateModelByString");
        //默认设置Serializable为false，即不产生：“private static final long serialVersionUID = 1L;”
        generateDialog.setCbSerializable(false);
        //自动调整对话框大小
        generateDialog.pack();
        //设置对话框跟随当前windows窗口
        generateDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(e.getProject()));
        generateDialog.setVisible(true);
    }

    private GenerateDialog.OnClickListener mClickListener = new GenerateDialog.OnClickListener() {

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
        List<List<String>> modelList = convertToList(str);
        String result = CodeWriter.getInstance().write(anActionEvent, modelList, memberType, isSerializable);
        //如果有错误信息，弹出来
        if (!TextUtils.isEmpty(result) && !result.equalsIgnoreCase("success")) {
            Messages.showMessageDialog(result, "Error", Messages.getInformationIcon());
        }
    }

    @NotNull
    /**
     * 把粘贴的字符串分行按空格转换成列表
     */
    private List<List<String>> convertToList(String str) {
        List<List<String>> modelList = new ArrayList<>();
        String[] lines = str.split("\n");
        for (String singleLine : lines) {
            if (TextUtils.isEmpty(singleLine)) {
                continue;
            }
            String[] stringArr = singleLine.split("\t");
            List<String> singleLineList = new ArrayList<>();
            for (String s : stringArr) {
                if (!TextUtils.isEmpty(s)) {
                    singleLineList.add(s);
                }
            }
            modelList.add(singleLineList);
        }
        return modelList;
    }
}

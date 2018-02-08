import javax.swing.*;

public class GenerateDialog extends JDialog {
    private JPanel content;
    private JTextArea txtPasteStr;
    private JCheckBox cbSerializable;
    private JButton btnCancel, btnGenerate;
    private OnClickListener onClickListener;
    private JRadioButton rbPublic, rbPrivate;
    /**
     * 是否序列化
     */
    private boolean serializable;
    /**
     * 成员变量类型：private or public
     */
    private String memberType;

    public GenerateDialog() {
        setContentPane(content);
        setModal(true);
        btnGenerate.addActionListener(e -> {
            if (onClickListener != null) {
                onClickListener.onGenerate(txtPasteStr.getText(), memberType, serializable);
            }
            dispose();
        });
        btnCancel.addActionListener(e -> {
            if (onClickListener != null) {
                onClickListener.onCancel();
            }
            dispose();
        });

        cbSerializable.addChangeListener(e -> {
            this.serializable = cbSerializable.isSelected();
        });

        rbPublic.addChangeListener(e -> {
            if (rbPublic.isSelected()) {
                memberType = "public";
                rbPrivate.setSelected(!rbPublic.isSelected());
            }
        });

        rbPrivate.addChangeListener(e -> {
            if (rbPrivate.isSelected()) {
                rbPublic.setSelected(!rbPrivate.isSelected());
                memberType = "private";
            }
        });
    }

    public interface OnClickListener {
        void onGenerate(String str, String member, boolean serializable);

        void onCancel();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setCbSerializable(boolean select) {
        this.cbSerializable.setSelected(select);
    }
}

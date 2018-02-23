package actions.generateJavaBean;

import javax.swing.*;
import java.awt.event.*;

public class GenerateJavaBeanDialog extends JDialog {
    private JButton btnCancel;
    private JPanel contentPane;
    private JButton btnGenerate;
    private JTextField editClassName;
    private JRadioButton rbPublic;
    private JRadioButton rbPrivate;
    private JTextArea txtPasteHere;
    private JCheckBox cbSerializable;
    private OnClickListener onClickListener;

    private boolean serializable;
    private String memberType = "private";

    public GenerateJavaBeanDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnGenerate);

        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickListener.onGenerate(editClassName.getText(), txtPasteHere.getText(), serializable, memberType);
                dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickListener.onCancel();
                dispose();
            }
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

        cbSerializable.addChangeListener(e -> {
            this.serializable = cbSerializable.isSelected();
        });
    }

    public interface OnClickListener {
        void onGenerate(String className, String pasteStr, boolean serializable, String member);

        void onCancel();
    }

    public void setOnClickListener(GenerateJavaBeanDialog.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setCbSerializable(boolean select) {
        this.cbSerializable.setSelected(select);
    }
}

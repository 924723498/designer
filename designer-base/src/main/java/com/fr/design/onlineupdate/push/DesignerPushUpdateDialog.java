package com.fr.design.onlineupdate.push;

import com.fr.design.dialog.UIDialog;
import com.fr.design.ui.ModernUIPane;
import com.fr.design.utils.gui.GUICoreUtils;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

/**
 * Created by plough on 2019/4/10.
 */
class DesignerPushUpdateDialog extends UIDialog {
    public static final Dimension DEFAULT = new Dimension(640, 320);

    private ModernUIPane<Model> jsPane;

    private DesignerPushUpdateDialog(Frame parent) {
        super(parent);
        setModal(true);
        initComponents();
    }

    static void createAndShow(Frame parent, DesignerUpdateInfo updateInfo) {
        DesignerPushUpdateDialog dialog = new DesignerPushUpdateDialog(parent);
        dialog.populate(updateInfo);
        dialog.showDialog();
    }

    private void initComponents() {
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());

        jsPane = new ModernUIPane.Builder<Model>()
                .withEMB("/com/fr/design/ui/onlineupdate/push/pushUpdate.html").namespace("Pool").build();

        contentPane.add(jsPane);
    }

    private void populate(DesignerUpdateInfo updateInfo) {
        Model model = createModel(updateInfo);
        jsPane.populate(model);
    }

    private Model createModel(DesignerUpdateInfo updateInfo) {
        Model model = new Model();
        model.setVersion(updateInfo.getPushVersion());
        model.setContent(updateInfo.getPushContent());
        model.setMoreInfoUrl(updateInfo.getMoreInfoUrl());
        model.setBackgroundUrl(updateInfo.getBackgroundUrl());
        return model;
    }

    @Override
    public void checkValid() throws Exception {
        // do nothing
    }

    /**
     * 显示窗口
     */
    private void showDialog() {
        setSize(DEFAULT);
        setUndecorated(true);
        GUICoreUtils.centerWindow(this);
        setVisible(true);
    }

    public class Model {
        private String version;
        private String content;
        private String moreInfoUrl;
        private String backgroundUrl;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMoreInfoUrl() {
            return moreInfoUrl;
        }

        public void setMoreInfoUrl(String moreInfoUrl) {
            this.moreInfoUrl = moreInfoUrl;
        }

        public String getBackgroundUrl() {
            return backgroundUrl;
        }

        public void setBackgroundUrl(String backgroundUrl) {
            this.backgroundUrl = backgroundUrl;
        }

        public void updateNow() {
            DesignerPushUpdateManager.getInstance().doUpdate();
            exit();
        }

        public void remindNextTime() {
            exit();
        }

        public void skipThisVersion() {
            DesignerPushUpdateManager.getInstance().skipCurrentPushVersion();
            exit();
        }

        private void exit() {
            DesignerPushUpdateDialog.this.dialogExit();
        }
    }
}

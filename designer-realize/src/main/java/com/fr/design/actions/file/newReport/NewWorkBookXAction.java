package com.fr.design.actions.file.newReport;

import com.fr.base.BaseUtils;
import com.fr.design.actions.UpdateAction;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.JWorkBook;
import com.fr.design.menu.MenuKeySet;

import com.fr.main.impl.WorkBookX;
import com.fr.report.worksheet.WorkSheet;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;


/**
 * 新建cptx格式模板
 *
 * @author vito
 * @date 2018/4/23
 */
public class NewWorkBookXAction extends UpdateAction {

    private static final String DEFAULT_FILE_PREFIX = "WorkBookX";

    public NewWorkBookXAction() {
        this.setMenuKeySet(NEW_WORK_BOOK_X);
        this.setName(getMenuKeySet().getMenuKeySetName());
        this.setMnemonic(getMenuKeySet().getMnemonic());
        this.setSmallIcon(icon());
        this.setAccelerator(getMenuKeySet().getKeyStroke());
    }

    protected Icon icon() {
        return BaseUtils.readIcon("/com/fr/design/images/buttonicon/newcpts.png");
    }

    /**
     * 动作
     *
     * @param e 事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        DesignerContext.getDesignerFrame().addAndActivateJTemplate(new JWorkBook(new WorkBookX(new WorkSheet())));
    }

    public static final MenuKeySet NEW_WORK_BOOK_X = new MenuKeySet() {
        @Override
        public char getMnemonic() {
            return 'L';
        }

        @Override
        public String getMenuName() {
            return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_M_New_WorkBook") + "(cptx)";
        }

        @Override
        public KeyStroke getKeyStroke() {
            return null;
        }
    };
}

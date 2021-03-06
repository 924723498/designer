package com.fr.design.dscolumn;

import com.fr.data.TableDataSource;
import com.fr.design.condition.DSColumnLiteConditionPane;
import com.fr.design.condition.DSColumnSimpleLiteConditionPane;
import com.fr.design.data.DesignTableDataManager;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.data.Condition;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.cellattr.core.group.DSColumn;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class DSColumnConditionsPane extends BasicPane {
    //peter: Lite Condition.

    private DSColumnLiteConditionPane liteConditionPane;
    //marks:是否继承父格条件
    private UICheckBox reselectExpandCheckBox;
    //marks:作为布局的空字符串
    private static final String INSET_TEXT = "      ";

    public DSColumnConditionsPane() {
        this(DSColumnPane.SETTING_ALL);
    }

    public DSColumnConditionsPane(int setting) {
        this.setLayout(FRGUIPaneFactory.createM_BorderLayout());

        if (setting > DSColumnPane.SETTING_DSRELATED) {
            liteConditionPane = new DSColumnLiteConditionPane() {
                protected boolean isNeedDoWithCondition(Condition liteCondition) {
                    return liteCondition != null;
                }
            };
        } else {
            liteConditionPane = new DSColumnSimpleLiteConditionPane();
        }
        this.add(liteConditionPane, BorderLayout.CENTER);

        if (setting > DSColumnPane.SETTING_DSRELATED) {
            //alex:ReSelect
            JPanel pane = FRGUIPaneFactory.createX_AXISBoxInnerContainer_S_Pane();
//            pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
            pane.add(new UILabel(INSET_TEXT));

            reselectExpandCheckBox = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Bind_Column_Extend_The_Conditions_of_Father_Cell(Applied_To_The_Data_Contains_Other_Data)"), false);
            pane.add(reselectExpandCheckBox);
            reselectExpandCheckBox.setSelected(true);

            JPanel reSelectPane = GUICoreUtils.createFlowPane(pane, FlowLayout.LEFT);
            this.add(reSelectPane, BorderLayout.NORTH);
            reSelectPane.setBorder(GUICoreUtils.createTitledBorder(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Bind_Column_The_Conditions_Of_Father_Cell"), null));
        }
    }

    @Override
    public String title4PopupWindow() {
        return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Filter");
    }

    public void populate(TableDataSource tds, CellElement cellElement) {
        if (cellElement == null) {
            return;
        }
        Object value = cellElement.getValue();
        if (value == null || !(value instanceof DSColumn)) {
            return;
        }
        DSColumn linearDSColumn = (DSColumn) value;

        if (reselectExpandCheckBox != null) {
            reselectExpandCheckBox.setSelected(!(linearDSColumn.isReselect()));
        }

        String[] columnNames = DesignTableDataManager.getSelectedColumnNames(tds, linearDSColumn.getDSName());
        liteConditionPane.populateColumns(columnNames);

        this.liteConditionPane.populateBean(linearDSColumn.getCondition());
    }

    public void update(CellElement cellElement) {
        if (cellElement == null) {
            return;
        }
        Object value = cellElement.getValue();
        if (value == null || !(value instanceof DSColumn)) {
            return;
        }
        DSColumn linearDSColumn = (DSColumn) value;
        linearDSColumn.setCondition(this.liteConditionPane.updateBean());
        if (reselectExpandCheckBox != null) {
            linearDSColumn.setReselect(!(this.reselectExpandCheckBox.isSelected()));
        }
    }

}

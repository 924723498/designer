package com.fr.design.webattr.printsettings;

import com.fr.base.PaperSize;
import com.fr.base.Utils;
import com.fr.base.print.NativePrintAttr;
import com.fr.design.gui.ibutton.UIRadioButton;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.icombobox.UIComboBoxRenderer;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UIBasicSpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.ComparatorUtils;

import com.fr.report.stable.ReportConstants;
import com.fr.stable.StringUtils;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by plough on 2018/3/5.
 */
public class NativePrintSettingPane extends JPanel {
    private UICheckBox isShowDialogCheck;
    private UIComboBox printerComboBox;
    private UIBasicSpinner copySpinner;  // 份数
    private UIRadioButton allPageRadioButton;
    private UIRadioButton currentPageRadioButton;
    private UIRadioButton customPageRadioButton;
    private UITextField specifiedAreaField;
    private UIComboBox predefinedPaperSizeComboBox;
    private UICheckBox inheritPagePaperSettingCheck;
    private UICheckBox inheritPageLayoutSettingCheck;
    private UICheckBox inheritPageMarginSettingCheck;
    private UICheckBox fitPaperSizeCheck;  // 缩放
    private UIRadioButton portraitRadioButton;
    private UIRadioButton landscapeRadioButton;
    private PageMarginSettingPane pageMarginSettingPane;
    private JPanel centerPane;

    public NativePrintSettingPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        JPanel printPane = FRGUIPaneFactory.createBorderLayout_S_Pane();

        JPanel northPane = FRGUIPaneFactory.createNColumnGridInnerContainer_Pane(1, 0, 15);
        UILabel tipDownload = GUICoreUtils.createTipLabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Tip_Native_Print_Need_Client"));
        northPane.add(tipDownload);
        isShowDialogCheck = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Engine_Show_Print_Setting_Window_When_Printing"));
        isShowDialogCheck.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        UILabel tipCheck = GUICoreUtils.createTipLabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Tip_Use_Default_Settings"));
        JPanel checkPane =  GUICoreUtils.createFlowPane(new Component[] {
                isShowDialogCheck, tipCheck}, FlowLayout.LEFT);
        northPane.add(checkPane);
        northPane.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 0));

        printPane.add(northPane, BorderLayout.NORTH);

        centerPane = FRGUIPaneFactory.createTitledBorderPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Default_Settings"));

        UIScrollPane scrollPane = new UIScrollPane(getNativePrintMainSettingPane());
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 340));
        centerPane.add(scrollPane);

        printPane.add(centerPane, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(printPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        allPageRadioButton.addItemListener(getPageRaidoListener());
        currentPageRadioButton.addItemListener(getPageRaidoListener());
        customPageRadioButton.addItemListener(getPageRaidoListener());
        isShowDialogCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkEnabled();
            }
        });
        specifiedAreaField.addFocusListener(new FocusAdapter() {
            String lastValidText = StringUtils.EMPTY;

            @Override
            public void focusGained(FocusEvent e) {
                lastValidText = specifiedAreaField.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = specifiedAreaField.getText();
                Pattern r = Pattern.compile("^(\\d+-)?\\d+$");
                Matcher m = r.matcher(text);
                if (!m.matches()) {
                    specifiedAreaField.setText(lastValidText);
                }
                super.focusLost(e);
            }
        });
    }

    private ItemListener getPageRaidoListener() {
        return new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                specifiedAreaField.setEnabled(customPageRadioButton.isSelected());
            }
        };
    }

    private JPanel getNativePrintMainSettingPane() {
        // 打印机
        String[] printerArray = getAllPrinterNames();
        printerComboBox = new UIComboBox(printerArray);
        printerComboBox.setPreferredSize(new Dimension(200, printerComboBox.getPreferredSize().height));
        JPanel printerPane = FRGUIPaneFactory.createLeftFlowZeroGapBorderPane();
        printerPane.add(printerComboBox);

        // 份数
        copySpinner = new UIBasicSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
        GUICoreUtils.setColumnForSpinner(copySpinner, 5);
        JPanel copyPane = FRGUIPaneFactory.createLeftFlowZeroGapBorderPane();
        copyPane.add(copySpinner);

        // 继承页面纸张设置
        inheritPagePaperSettingCheck = GUICoreUtils.createNoBorderCheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Inherit_Page_Paper_Setting"));
        JPanel paperSettingPane = getPaperSettingPane();
        JPanel paperSettingCheckPane = GUICoreUtils.createCheckboxAndDynamicPane(inheritPagePaperSettingCheck, paperSettingPane, true);

        // 继承页面布局设置
        inheritPageLayoutSettingCheck = GUICoreUtils.createNoBorderCheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Inherit_Page_Layout_Setting"));
        JPanel layoutSettingPane = getLayoutSettingPane();
        JPanel layoutSettingCheckPane = GUICoreUtils.createCheckboxAndDynamicPane(inheritPageLayoutSettingCheck, layoutSettingPane, true);

        // 页码标签
        UILabel printAreaLabel = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Engine_Page_Number") + ":");
        JPanel printAreaLabelPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        printAreaLabelPane.add(printAreaLabel, BorderLayout.NORTH);
        printAreaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // 边距
        inheritPageMarginSettingCheck = GUICoreUtils.createNoBorderCheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Inherit_Page_Margin_Setting"));
        pageMarginSettingPane = new PageMarginSettingPane();
        pageMarginSettingPane.setBorder(BorderFactory.createEmptyBorder(10, -10, 0, 0));
        JPanel pageMarginCheckPane = GUICoreUtils.createCheckboxAndDynamicPane(inheritPageMarginSettingCheck, pageMarginSettingPane, true);

        // 缩放
        fitPaperSizeCheck = GUICoreUtils.createNoBorderCheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Print_To_Fit_Paper_Size"));

        // TableLayout
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p, p, p, p};
        double[] columnSize = {60, p};
        Component[][] components = {
                {new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Printer") + ":"), printerPane},
                {new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Copy_Number") + ":"), copyPane},
                {printAreaLabelPane, getPrintAreaPane()},
                {getTopAlignLabelPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Paper") + ":"), paperSettingCheckPane},
                {getTopAlignLabelPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Form_Layout") + ":"), layoutSettingCheckPane},
                {getTopAlignLabelPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Margin") + ":"), pageMarginCheckPane}
                // 此功能暂时不做，在界面上隐藏缩放选项
//                {new UILabel(com.fr.design.i18n.Toolkit.i18nText("FR-Designer_Scale_EnlargeOrReduce") + ":"), fitPaperSizeCheck},
        };
        return TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, 0, 15);
    }

    private String[] getAllPrinterNames() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
                DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        Set<String> allPrinterName = new HashSet<String>();

        for (int i = 0, len = printServices.length; i < len; i++) {
            allPrinterName.add(printServices[i].getName());
        }

        return allPrinterName.toArray(new String[allPrinterName.size()]);
    }

    private JPanel getPaperSettingPane() {
        predefinedPaperSizeComboBox = new UIComboBox();
        for (int i = 0; i < ReportConstants.PaperSizeNameSizeArray.length; i++) {
            Object[] tmpPaperSizeNameArray = ReportConstants.PaperSizeNameSizeArray[i];
            predefinedPaperSizeComboBox.addItem(tmpPaperSizeNameArray[1]);
        }
        predefinedPaperSizeComboBox.setRenderer(new UIComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof PaperSize) {
                    PaperSize paperSize = (PaperSize) value;
                    for (int i = 0; i < ReportConstants.PaperSizeNameSizeArray.length; i++) {
                        Object[] tmpPaperSizeNameArray = ReportConstants.PaperSizeNameSizeArray[i];

                        if (ComparatorUtils.equals(paperSize, tmpPaperSizeNameArray[1])) {
                            String sbuf = tmpPaperSizeNameArray[0].toString() + " [" +
                                    Utils.convertNumberStringToString(paperSize.getWidth().toMMValue4Scale2()) +
                                    'x' +
                                    Utils.convertNumberStringToString(paperSize.getHeight().toMMValue4Scale2()) +
                                    ' ' +
                                    com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_PageSetup_mm") +
                                    ']';
                            this.setText(sbuf);
                            break;
                        }
                    }
                }

                return this;
            }
        });

        JPanel panel = FRGUIPaneFactory.createLeftFlowZeroGapBorderPane();
        panel.add(predefinedPaperSizeComboBox);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        return panel;
    }

    private JPanel getLayoutSettingPane() {
        JPanel layoutSettingPane = FRGUIPaneFactory.createLeftFlowZeroGapBorderPane();
        layoutSettingPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        portraitRadioButton = new UIRadioButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_PageSetup_Portrait"));
        portraitRadioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        landscapeRadioButton = new UIRadioButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_PageSetup_Landscape"));
        layoutSettingPane.add(portraitRadioButton);
        layoutSettingPane.add(landscapeRadioButton);

        ButtonGroup layoutButtonGroup = new ButtonGroup();
        layoutButtonGroup.add(portraitRadioButton);
        layoutButtonGroup.add(landscapeRadioButton);

        portraitRadioButton.setSelected(true);
        return layoutSettingPane;
    }

    // 页码范围
    private JPanel getPrintAreaPane() {
        allPageRadioButton = new UIRadioButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_All_Pages"));
        currentPageRadioButton = new UIRadioButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Current_Page"));
        customPageRadioButton = new UIRadioButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Engine_HJS-Specified_Pages"));
        ButtonGroup group = new ButtonGroup();
        group.add(allPageRadioButton);
        group.add(currentPageRadioButton);
        group.add(customPageRadioButton);
        allPageRadioButton.setSelected(true);

        specifiedAreaField = new UITextField(20) {
            @Override
            public void setEnabled(boolean enabled) {
                // 如果未选中"指定页"，此输入框始终不可用
                if (enabled && !customPageRadioButton.isSelected()) {
                    return;
                }
                super.setEnabled(enabled);
            }
        };
        UILabel areaFieldTip = GUICoreUtils.createTipLabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Print_Area_Tip"));

        // TableLayout
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p};
        double[] columnSize = {p, p, p};
        Component[][] components = {
                {allPageRadioButton, null, null},
                {currentPageRadioButton, null, null},
                {customPageRadioButton, specifiedAreaField, areaFieldTip}
        };
        return TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, 0, 0);
    }

    // 返回包含一个标签的 panel，标签始终位于 panel 顶部
    private JPanel getTopAlignLabelPane(String labelText) {
        JPanel labelPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        labelPane.add(new UILabel(labelText), BorderLayout.NORTH);
        return labelPane;
    }

    public void populate(NativePrintAttr nativePrintAttr) {
        isShowDialogCheck.setSelected(nativePrintAttr.isShowDialog());
        printerComboBox.setSelectedItem(nativePrintAttr.getPrinterName());
        copySpinner.setValue(nativePrintAttr.getCopy());

        if (nativePrintAttr.getPageType().equals(NativePrintAttr.PageType.ALL_PAGES)) {
            allPageRadioButton.setSelected(true);
        } else if (nativePrintAttr.getPageType().equals(NativePrintAttr.PageType.CURRENT_PAGE)) {
            currentPageRadioButton.setSelected(true);
        } else {
            customPageRadioButton.setSelected(true);
            specifiedAreaField.setText(nativePrintAttr.getArea());
        }
        specifiedAreaField.setEnabled(customPageRadioButton.isSelected());

        inheritPagePaperSettingCheck.setSelected(nativePrintAttr.isInheritPagePaperSetting());
        predefinedPaperSizeComboBox.setSelectedItem(nativePrintAttr.getPaperSize());
        inheritPageLayoutSettingCheck.setSelected(nativePrintAttr.isInheritPageLayoutSetting());
        if (nativePrintAttr.getOrientation() == ReportConstants.PORTRAIT) {
            portraitRadioButton.setSelected(true);
        } else {
            landscapeRadioButton.setSelected(true);
        }
        inheritPageMarginSettingCheck.setSelected(nativePrintAttr.isInheritPageMarginSetting());
        pageMarginSettingPane.populate(nativePrintAttr.getMargin());
        fitPaperSizeCheck.setSelected(nativePrintAttr.isFitPaperSize());
    }

    public void update(NativePrintAttr nativePrintAttr) {
        nativePrintAttr.setShowDialog(isShowDialogCheck.isSelected());
        if (printerComboBox.getSelectedItem() != null) {
            nativePrintAttr.setPrinterName(printerComboBox.getSelectedItem().toString());
        }
        nativePrintAttr.setCopy((int)copySpinner.getValue());

        // 页码
        if (allPageRadioButton.isSelected()) {
            nativePrintAttr.setPageType(NativePrintAttr.PageType.ALL_PAGES);
        } else if (currentPageRadioButton.isSelected()) {
            nativePrintAttr.setPageType(NativePrintAttr.PageType.CURRENT_PAGE);
        } else {
            nativePrintAttr.setPageType(NativePrintAttr.PageType.SPECIFIED_PAGES);
            nativePrintAttr.setArea(specifiedAreaField.getText());
        }

        nativePrintAttr.setInheritPagePaperSetting(inheritPagePaperSettingCheck.isSelected());
        nativePrintAttr.setPaperSize((PaperSize) predefinedPaperSizeComboBox.getSelectedItem());
        nativePrintAttr.setInheritPageLayoutSetting(inheritPageLayoutSettingCheck.isSelected());
        nativePrintAttr.setOrientation(portraitRadioButton.isSelected() ?
                ReportConstants.PORTRAIT : ReportConstants.LANDSCAPE);
        nativePrintAttr.setInheritPageMarginSetting(inheritPageMarginSettingCheck.isSelected());
        nativePrintAttr.setMargin(pageMarginSettingPane.updateBean());
        nativePrintAttr.setFitPaperSize(fitPaperSizeCheck.isSelected());
    }

    // 刷新面板可用状态
    public void checkEnabled() {
        GUICoreUtils.setEnabled(centerPane, !isShowDialogCheck.isSelected());
    }
}

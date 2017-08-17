package com.fr.plugin.chart.gauge;

import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.ConditionAttr;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.design.mainframe.chart.gui.style.series.ColorPickerPaneWithFormula;
import com.fr.design.mainframe.chart.gui.style.series.UIColorPickerPane;
import com.fr.design.style.color.ColorSelectBox;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.plugin.chart.attr.GaugeDetailStyle;
import com.fr.plugin.chart.attr.plot.VanChartPlot;
import com.fr.plugin.chart.base.AttrLabel;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.style.series.VanChartAbstractPlotSeriesPane;
import com.fr.plugin.chart.pie.RadiusCardLayoutPane;
import com.fr.plugin.chart.type.GaugeStyle;
import com.fr.stable.Constants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by Mitisky on 15/11/27.
 */
public class VanChartGaugeSeriesPane extends VanChartAbstractPlotSeriesPane {

    private static final long serialVersionUID = -4414343926082129759L;
    private UIButtonGroup gaugeLayout;//布局：横向、纵向

    private ColorSelectBox hingeColor;//枢纽颜色
    private ColorSelectBox hingeBackgroundColor;//枢纽背景颜色
    private ColorSelectBox needleColor;//指针颜色
    private ColorSelectBox paneBackgroundColor;//底盘背景颜色

    private ColorSelectBox slotBackgroundColor;//刻度槽颜色

    private UIButtonGroup rotate;//旋转方向
    private ColorSelectBox innerPaneBackgroundColor;//内底盘背景颜色

    private UIColorPickerPane colorPickerPane;

    public VanChartGaugeSeriesPane(ChartStylePane parent, Plot plot) {
        super(parent, plot);
    }

    protected JPanel getContentInPlotType() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {f};
        double[] rowSize = {p,p,p,p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{getColorPane()},
                new Component[]{createGaugeLayoutPane()},
                new Component[]{createGaugeStylePane(rowSize, new double[]{p,f})},
                new Component[]{createGaugeBandsPane()}
        };

       return TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
    }

    //设置色彩面板内容
    protected void setColorPaneContent (JPanel panel) {
        panel.add(getFillStylePane(), BorderLayout.NORTH);
    }

    private JPanel createGaugeLayoutPane() {
        gaugeLayout = new UIButtonGroup(new String[]{Inter.getLocText("FR-Chart-Direction_Horizontal"), Inter.getLocText("FR-Chart-Direction_Vertical")});
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("PageSetup-Orientation"),gaugeLayout);
        gaugeLayout.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelPosition();
            }
        });
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("FR-Chart_Layout"), panel);
    }

    private void changeLabelPosition() {
        if(plot instanceof VanChartGaugePlot){
            VanChartGaugePlot gaugePlot = (VanChartGaugePlot)plot;
            if (ComparatorUtils.equals(gaugePlot.getGaugeStyle(), GaugeStyle.THERMOMETER)){
                ConditionAttr attrList = gaugePlot.getConditionCollection().getDefaultAttr();
                AttrLabel attrLabel = (AttrLabel)attrList.getExisted(AttrLabel.class);
                if(attrLabel == null){
                    return;
                }
                if(gaugeLayout.getSelectedIndex() == 0){
                    attrLabel.getAttrLabelDetail().setPosition(Constants.LEFT);
                    attrLabel.getAttrLabelDetail().getTextAttr().setFRFont(VanChartGaugePlot.THERMOMETER_VERTICAL_PERCENT_LABEL_FONT);
                    attrLabel.getGaugeValueLabelDetail().setPosition(Constants.LEFT);
                } else {
                    attrLabel.getAttrLabelDetail().setPosition(Constants.BOTTOM);
                    attrLabel.getAttrLabelDetail().getTextAttr().setFRFont(VanChartGaugePlot.THERMOMETER_PERCENT_LABEL_FONT);
                    attrLabel.getGaugeValueLabelDetail().setPosition(Constants.BOTTOM);
                }
            }
        }
    }

    private JPanel createGaugeStylePane(double[] row, double[] col) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        JPanel centerPanel = TableLayoutHelper.createTableLayoutPane(getDiffComponentsWithGaugeStyle(), row, col);
        panel.add(centerPanel, BorderLayout.CENTER);
        if(rotate != null){
            panel.add(rotate, BorderLayout.NORTH);
        }
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("FR-Designer-Widget_Style"), panel);
    }

    private Component[][] getDiffComponentsWithGaugeStyle() {
        GaugeStyle style = plot == null ? GaugeStyle.POINTER : ((VanChartGaugePlot)plot).getGaugeStyle();
        switch (style) {
            case RING:
                initRotate();
                return new Component[][]{
                        new Component[]{null, null},
                        getPaneBackgroundColor(),
                        getInnerPaneBackgroundColor(),
                        new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Radius_Set")),createRadiusPane()}
                };
            case SLOT:
                return new Component[][]{
                        new Component[]{null, null},
                        getNeedleColor(),
                        getSlotBackgroundColor(),
                        new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Radius_Set")),createRadiusPane()}
                };
            case THERMOMETER:
                return new Component[][]{
                        new Component[]{null, null},
                        getNeedleColor(),
                        getSlotBackgroundColor(),
                        new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Radius_Set")),createRadiusPane()}
                };
            default:
                return new Component[][]{
                        new Component[]{null, null},
                        getHingeColor(),
                        getHingeBackgroundColor(),
                        getNeedleColor(),
                        getPaneBackgroundColor(),
                        new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Radius_Set")),createRadiusPane()}
                };
        }
    }

    //半径界面
    protected RadiusCardLayoutPane initRadiusPane() {
        return ((VanChartPlot)plot).isInCustom() ? null : new RadiusCardLayoutPane();
    }

    private Component[] getHingeColor() {
        hingeColor = new ColorSelectBox(120);
        return new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Hinge")),hingeColor};
    }

    private Component[] getHingeBackgroundColor() {
        hingeBackgroundColor = new ColorSelectBox(120);
        return new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_HingeBackground")),hingeBackgroundColor};
    }

    private Component[] getNeedleColor() {
        needleColor = new ColorSelectBox(120);
        return new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Needle")),needleColor};
    }

    private Component[] getPaneBackgroundColor() {
        paneBackgroundColor = new ColorSelectBox(120);
        return  new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_PaneBackground")),paneBackgroundColor};
    }

    private Component[] getSlotBackgroundColor() {
        slotBackgroundColor = new ColorSelectBox(120);
        return new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_SlotBackground")),slotBackgroundColor};
    }

    private void initRotate() {
        rotate = new UIButtonGroup(new String[]{Inter.getLocText("Plugin-ChartF_AntiClockWise"), Inter.getLocText("Plugin-ChartF_ClockWise")});
    }

    private Component[] getInnerPaneBackgroundColor() {
        innerPaneBackgroundColor = new ColorSelectBox(120);
        return new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_InnerPaneBackground")),innerPaneBackgroundColor};
    }

    private JPanel createGaugeBandsPane() {
        colorPickerPane = new ColorPickerPaneWithFormula("meterString");
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane("",colorPickerPane);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Range"), panel);
    }


    public void populateBean(Plot plot) {
        if(plot == null) {
            return;
        }
        super.populateBean(plot);
        if(plot instanceof VanChartGaugePlot){
            VanChartGaugePlot gaugePlot = (VanChartGaugePlot)plot;
            GaugeDetailStyle detailStyle = gaugePlot.getGaugeDetailStyle();
            gaugeLayout.setSelectedIndex(detailStyle.isHorizontalLayout() ? 0 : 1);

            if(hingeColor != null){
                hingeColor.setSelectObject(detailStyle.getHingeColor());
            }
            if(hingeBackgroundColor != null){
                hingeBackgroundColor.setSelectObject(detailStyle.getHingeBackgroundColor());
            }
            if(needleColor != null){
                needleColor.setSelectObject(detailStyle.getNeedleColor());
            }
            if(paneBackgroundColor != null){
                paneBackgroundColor.setSelectObject(detailStyle.getPaneBackgroundColor());
            }
            if(slotBackgroundColor != null){
                slotBackgroundColor.setSelectObject(detailStyle.getSlotBackgroundColor());
            }
            if(rotate != null){
                rotate.setSelectedIndex(detailStyle.isAntiClockWise() ? 0 : 1);
            }
            if(innerPaneBackgroundColor != null){
                innerPaneBackgroundColor.setSelectObject(detailStyle.getInnerPaneBackgroundColor());
            }

            colorPickerPane.populateBean(detailStyle.getHotAreaColor());
        }
    }

    @Override
    public void updateBean(Plot plot) {
        if(plot == null){
            return;
        }
        super.updateBean(plot);
        if(plot instanceof VanChartGaugePlot){
            VanChartGaugePlot gaugePlot = (VanChartGaugePlot)plot;

            GaugeDetailStyle detailStyle = gaugePlot.getGaugeDetailStyle();
            detailStyle.setHorizontalLayout(gaugeLayout.getSelectedIndex() == 0);

            if(hingeColor != null){
                detailStyle.setHingeColor(hingeColor.getSelectObject());
            }
            if(hingeBackgroundColor != null){
                detailStyle.setHingeBackgroundColor(hingeBackgroundColor.getSelectObject());
            }
            if(needleColor != null){
                detailStyle.setNeedleColor(needleColor.getSelectObject());
            }
            if(paneBackgroundColor != null){
                detailStyle.setPaneBackgroundColor(paneBackgroundColor.getSelectObject());
            }
            if(slotBackgroundColor != null){
                detailStyle.setSlotBackgroundColor(slotBackgroundColor.getSelectObject());
            }
            if(rotate != null){
                detailStyle.setAntiClockWise(rotate.getSelectedIndex() == 0);
            }
            if(innerPaneBackgroundColor != null){
                detailStyle.setInnerPaneBackgroundColor(innerPaneBackgroundColor.getSelectObject());
            }

            colorPickerPane.updateBean(detailStyle.getHotAreaColor());
        }
    }
}
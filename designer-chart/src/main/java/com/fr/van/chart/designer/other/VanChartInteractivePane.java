package com.fr.van.chart.designer.other;

import com.fr.base.BaseFormula;
import com.fr.base.Utils;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ibutton.UIToggleButton;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;

import com.fr.plugin.chart.attr.axis.VanChartAxis;
import com.fr.plugin.chart.attr.plot.VanChartPlot;
import com.fr.plugin.chart.attr.plot.VanChartRectanglePlot;
import com.fr.plugin.chart.axis.type.AxisPlotType;
import com.fr.plugin.chart.base.RefreshMoreLabel;
import com.fr.plugin.chart.base.VanChartConstants;
import com.fr.plugin.chart.base.VanChartTools;
import com.fr.plugin.chart.base.VanChartZoom;
import com.fr.plugin.chart.vanchart.VanChart;
import com.fr.stable.StableUtils;
import com.fr.van.chart.custom.component.VanChartHyperLinkPane;
import com.fr.van.chart.designer.AbstractVanChartScrollPane;
import com.fr.van.chart.designer.PlotFactory;
import com.fr.van.chart.designer.TableLayout4VanChartHelper;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VanChartInteractivePane extends AbstractVanChartScrollPane<Chart> {

    private static final long serialVersionUID = 8135452818502145597L;
    private static final int AUTO_REFRESH_LEFT_GAP = 18;

    protected UICheckBox isSort;
    protected UICheckBox exportImages;
    protected UICheckBox fullScreenDisplay;
    protected UIToggleButton collapse;

    protected UIButtonGroup isChartAnimation;

    //坐标轴翻转属性
    private UIButtonGroup<Integer> axisRotation;

    private AutoRefreshPane autoRefreshPane;

    private UIButtonGroup zoomWidget;
    protected UIButtonGroup zoomGesture;//地图手势缩放
    private UIButtonGroup zoomResize;
    private TinyFormulaPane from;
    private TinyFormulaPane to;
    private UIButtonGroup<String> zoomType;
    private JPanel changeEnablePane;
    private JPanel zoomTypePane;

    protected VanChartHyperLinkPane superLink;

    protected Chart chart;
    protected JPanel interactivePane;

    /**
     * 界面标题.
     * @return 返回标题.
     */


    public String title4PopupWindow() {
        return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Interactive_Tab");
    }

    @Override
    protected JPanel createContentPane() {
        return new JPanel();
    }

    private void reLayoutContentPane(VanChartPlot plot){
        if (interactivePane != null) {
            interactivePane.removeAll();
        }
        interactivePane = getInteractivePane(plot);
        reloaPane(interactivePane);
    }

    protected JPanel getInteractivePane(VanChartPlot plot){
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        double[] rowSize = {p,p,p,p,p,p};


        Component[][] components = new Component[][]{
                new Component[]{createToolBarPane(getToolBarRowSize(), columnSize),null},
                new Component[]{createAnimationPane(),null},
                new Component[]{createAxisRotationPane(new double[]{p,p}, columnSize, plot),null},
                new Component[]{createZoomPane(new double[]{p,p,p}, columnSize, plot),null},
                new Component[]{createAutoRefreshPane(plot),null},
                new Component[]{createHyperlinkPane(),null}
        };

        return TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
    }

    protected JPanel createZoomPane(double[] row, double[] col, VanChartPlot plot) {
        if (!plot.isSupportZoomDirection()) {
            return null;
        }
        zoomWidget = new UIButtonGroup(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Open"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Close")});
        zoomResize = new UIButtonGroup(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Change"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Non_Adjustment")});
        from = new TinyFormulaPane();
        to = new TinyFormulaPane();
        zoomType = new UIButtonGroup(getNameArray(), getValueArray());
        zoomGesture = new UIButtonGroup(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Open"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Close")});

        JPanel zoomWidgetPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Zoom_Widget"), zoomWidget);
        JPanel zoomGesturePane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_ZoomGesture"), zoomGesture);

        Component[][] components = new Component[][]{
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Widget_Boundary")), zoomResize},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_From")), from},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_To")), to},
        };

        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        changeEnablePane = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, columnSize);
        changeEnablePane.setBorder(BorderFactory.createEmptyBorder(10,12,0,0));
        zoomTypePane = getzoomTypePane(zoomType);
        JPanel panel = createZoomPaneContent(zoomWidgetPane, zoomGesturePane, changeEnablePane, zoomTypePane, plot);
        zoomWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkZoomPane();
            }
        });
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Use_Zoom"), panel);
    }


    protected JPanel getzoomTypePane(UIButtonGroup zoomType) {
        return TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Zoom_Direction"), zoomType);
    }

    protected JPanel createZoomPaneContent(JPanel zoomWidgetPane, JPanel zoomGesturePane, JPanel changeEnablePane, JPanel zoomTypePane, VanChartPlot plot) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        if (plot.isSupportZoomCategoryAxis()) {//支持缩放控件
            panel.add(zoomWidgetPane, BorderLayout.NORTH);
            panel.add(changeEnablePane, BorderLayout.CENTER);
        }
        panel.add(zoomTypePane, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAxisRotationPane(double[] row, double[] col, VanChartPlot plot){
        if (!(plot.getAxisPlotType() == AxisPlotType.RECTANGLE)){
            return null;
        }
        axisRotation = new UIButtonGroup<Integer>(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Open"),
                com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Close")});
        axisRotation.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                checkZoomEnabled();
            }
        });
        Component[][] components = new Component[][]{
                new Component[]{null,null},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Reversal")),axisRotation}
        };
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis"), panel);
    }

    private void checkZoomEnabled() {
        if (zoomWidget != null && axisRotation != null) {
            if (axisRotation.getSelectedIndex() == 0) {
                //只有开启坐标轴翻转，才需要将缩放控件强制关闭。
                zoomWidget.setSelectedIndex(1);
            }
            checkZoomPane();
            zoomWidget.setEnabled(axisRotation.getSelectedIndex() == 1);
        }
    }


    protected String[] getNameArray() {
        return new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_X_Axis"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Y_Axis")
                ,com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_XY_Axis"),com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Use_None")};
    }

    protected String[] getValueArray() {
        return new String[]{VanChartConstants.ZOOM_TYPE_X, VanChartConstants.ZOOM_TYPE_Y
                ,VanChartConstants.ZOOM_TYPE_XY, VanChartConstants.ZOOM_TYPE_NONE};

    }

    protected JPanel createToolBarPane(double[] row, double[] col){
        isSort = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Sort"));
        exportImages = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Export_Image"));
        fullScreenDisplay = new UICheckBox(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_FullScreen_Display"));
        collapse = new UIToggleButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Collapse"));

        Component[][] components = createToolBarComponents();

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_ToolBar"), panel);
    }

    protected double[] getToolBarRowSize () {
        double p = TableLayout.PREFERRED;
        return new double[]{p,p,p,p,p};
    }

    protected Component[][] createToolBarComponents() {
        return new Component[][]{
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Content")),isSort},
                new Component[]{null, exportImages},
                new Component[]{null, fullScreenDisplay},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout")),collapse},
        };
    }

    protected Component[][] createToolBarComponentsWithOutSort() {
        return new Component[][]{
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Content")), exportImages},
                new Component[]{null, fullScreenDisplay},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout")),collapse}
        };
    }


    protected JPanel createAnimationPane(){
        isChartAnimation = new UIButtonGroup(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Open"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Close")});
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Animation_Effects"), isChartAnimation);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Animation"), panel);
    }

    protected JPanel createAutoRefreshPane(VanChartPlot plot){

        autoRefreshPane = getMoreLabelPane(plot);

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Moniter_refresh"), autoRefreshPane);
    }

    protected AutoRefreshPane getMoreLabelPane(VanChartPlot plot) {
        boolean isLargeModel = largeModel(plot);
        return new AutoRefreshPane((VanChart) chart, isLargeModel);
    }

    protected JPanel createHyperlinkPane() {
        superLink = new VanChartHyperLinkPane();
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_M_Insert_Hyperlink"), superLink);
    }


    private void checkZoomPane() {
        boolean zoomWidgetEnabled = zoomWidget.getSelectedIndex() == 0;
        changeEnablePane.setVisible(zoomWidgetEnabled);
        zoomType.setEnabled(!zoomWidgetEnabled);
    }

    @Override
    public void populateBean(Chart chart) {
        if (chart == null || chart.getPlot() == null) {
            return;
        }
        this.chart = chart;
        VanChartPlot plot = (VanChartPlot)chart.getPlot();

        if(interactivePane == null){
            this.remove(leftcontentPane);
            reLayoutContentPane(plot);
        }
        if(plot.isSupportZoomDirection()){//支持缩放方向=方向+控件
            populateChartZoom((VanChart)chart);
            checkZoomPane();
        }

        if (plot.getAxisPlotType() == AxisPlotType.RECTANGLE){
            populateChartAxisRotation(plot);
        }

        populateChartTools((VanChart) chart);
        populateChartAnimate(chart, plot);
        populateAutoRefresh((VanChart)chart);

        populateHyperlink(plot);

        checkZoomEnabled();
    }


    protected void populateHyperlink(Plot plot) {
        superLink.populate(plot);
    }

    private void populateChartTools(VanChart chart) {
        VanChartTools vanChartTools = chart.getVanChartTools();
        isSort.setSelected(vanChartTools.isSort());
        exportImages.setSelected(vanChartTools.isExport());
        fullScreenDisplay.setSelected(vanChartTools.isFullScreen());
        collapse.setSelected(vanChartTools.isHidden());
    }

    private void populateChartZoom(VanChart chart) {
        VanChartZoom zoom = chart.getVanChartZoom();
        if(zoom == null){
            zoom = new VanChartZoom();
        }
        zoomWidget.setSelectedIndex(zoom.isZoomVisible() ? 0 : 1);
        zoomGesture.setSelectedIndex(zoom.isZoomGesture() ? 0 : 1);
        zoomResize.setSelectedIndex(zoom.isZoomResize() ? 0 : 1);
        if (zoom.getFrom() instanceof BaseFormula) {
            from.populateBean(((BaseFormula) zoom.getFrom()).getContent());
        } else {
            from.populateBean(Utils.objectToString(zoom.getFrom()));
        }
        if (zoom.getTo() instanceof BaseFormula) {
            to.populateBean(((BaseFormula) zoom.getTo()).getContent());
        } else {
            to.populateBean(Utils.objectToString(zoom.getTo()));
        }
        zoomType.setSelectedItem(zoom.getZoomType());
    }

    private void populateChartAxisRotation(VanChartPlot plot) {
        axisRotation.setSelectedIndex(plot.isAxisRotation() ? 0 : 1);
    }

    private void populateChartAnimate(Chart chart, Plot plot) {
        if(plot.isSupportAnimate()) {
            isChartAnimation.setSelectedIndex(chart.isJSDraw() ? 0 : 1);
            isChartAnimation.setEnabled(!largeModel(plot));
        }
    }

    protected boolean largeModel(Plot plot) {
        return PlotFactory.largeDataModel(plot);
    }

    protected void populateAutoRefresh(VanChart chart) {
        VanChartPlot plot = (VanChartPlot)chart.getPlot();

        RefreshMoreLabel refreshMoreLabel = chart.getRefreshMoreLabel();
        if(refreshMoreLabel == null) {
            refreshMoreLabel = new RefreshMoreLabel(((VanChartPlot)chart.getPlot()).getAutoAttrTooltip());
        }

        autoRefreshPane.populateBean(refreshMoreLabel);

    }

    @Override
    public void updateBean(Chart chart) {
        if (chart == null || chart.getPlot() == null) {
            return;
        }

        VanChartPlot plot = (VanChartPlot)chart.getPlot();

        if(plot.isSupportZoomDirection()){
            updateChartZoom((VanChart)chart);
        }
        if(plot.getAxisPlotType() == AxisPlotType.RECTANGLE){
            updateChartAxisRotation((VanChart)chart);
        }
        updateChartTools((VanChart)chart);
        updateChartAnimate(chart, plot);
        updateAutoRefresh((VanChart)chart);
        updateHyperlink(plot);
    }

    protected void updateHyperlink(Plot plot){
        superLink.update(plot);
    }

    private void updateChartTools(VanChart chart) {
        VanChartTools vanChartTools = new VanChartTools();
        vanChartTools.setExport(exportImages.isSelected());
        vanChartTools.setFullScreen(fullScreenDisplay.isSelected());
        vanChartTools.setSort(isSort.isSelected());
        vanChartTools.setHidden(collapse.isSelected());
        chart.setVanChartTools(vanChartTools);
    }

    private void updateChartZoom(VanChart chart) {
        VanChartZoom zoom = chart.getVanChartZoom();
        if(zoom == null){
            zoom = new VanChartZoom();
            chart.setVanChartZoom(zoom);
        }
        zoom.setZoomVisible(zoomWidget.getSelectedIndex() == 0);
        zoom.setZoomGesture(zoomGesture.getSelectedIndex() == 0);
        zoom.setZoomResize(zoomResize.getSelectedIndex() == 0);
        String fromString = from.updateBean();
        Object fromObject;
        if (StableUtils.maybeFormula(fromString)) {
            fromObject = BaseFormula.createFormulaBuilder().build(fromString);
        } else {
            fromObject = fromString;
        }
        zoom.setFrom(fromObject);
        String toString = to.updateBean();
        Object toObject;
        if (StableUtils.maybeFormula(toString)) {
            toObject = BaseFormula.createFormulaBuilder().build(toString);
        } else {
            toObject = toString;
        }
        zoom.setTo(toObject);
        zoom.setZoomType(zoomType.getSelectedItem());
    }

    private void updateChartAxisRotation(VanChart chart) {
        //坐标轴和plot都需要这个属性，因为坐标轴和plot是分开画的
        VanChartPlot plot = (VanChartPlot) chart.getPlot();
        plot.setAxisRotation(axisRotation.getSelectedIndex() == 0);
        //同时更新坐标轴旋转属性
        for (VanChartAxis axis : ((VanChartRectanglePlot) plot).getXAxisList()) {
            axis.setRotation(plot.isAxisRotation());
        }

        for (VanChartAxis axis : ((VanChartRectanglePlot) plot).getYAxisList()) {
            axis.setRotation(plot.isAxisRotation());
        }

        //更新数据表属性
        if (plot.isAxisRotation()){
            plot.getDataSheet().setVisible(false);
        }
    }

    private void updateChartAnimate(Chart chart, Plot plot) {
        if(plot.isSupportAnimate()) {
            chart.setJSDraw(isChartAnimation.getSelectedIndex()==0);
        }
    }


    private void updateAutoRefresh(VanChart chart) {

        RefreshMoreLabel refreshMoreLabel = chart.getRefreshMoreLabel();
        if(refreshMoreLabel == null) {
            refreshMoreLabel = new RefreshMoreLabel(((VanChartPlot)chart.getPlot()).getAutoAttrTooltip());
            chart.setRefreshMoreLabel(refreshMoreLabel);
        }
        autoRefreshPane.updateBean(refreshMoreLabel);
    }

    @Override
    public Chart updateBean() {
        return null;
    }

    /**
     * 组件是否需要响应添加的观察者事件
     *
     * @return 如果需要响应观察者事件则返回true，否则返回false
     */
    public boolean shouldResponseChangeListener() {
        return true;
    }
}

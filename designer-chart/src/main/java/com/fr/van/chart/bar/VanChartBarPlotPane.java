package com.fr.van.chart.bar;

import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.log.FineLoggerFactory;

import com.fr.plugin.chart.bar.BarIndependentVanChart;
import com.fr.plugin.chart.column.VanChartColumnPlot;
import com.fr.van.chart.designer.type.AbstractVanChartTypePane;

/**
 * Created by Mitisky on 15/10/20.
 */
public class VanChartBarPlotPane extends AbstractVanChartTypePane {
    public static final String TITLE = com.fr.design.i18n.Toolkit.i18nText("Plugin-ChartF_NewBar");

    private static final long serialVersionUID = 2879689884048643002L;

    @Override
    protected String[] getTypeIconPath() {
        return new String[]{"/com/fr/van/chart/bar/images/bar.png",
                "/com/fr/van/chart/bar/images/stack.png",
                "/com/fr/van/chart/bar/images/percentstack.png",
                "/com/fr/van/chart/bar/images/custom.png",
        };
    }

    @Override
    protected String[] getTypeTipName() {
        String bar = com.fr.design.i18n.Toolkit.i18nText("FR-Chart-Type_Bar");
        String stack = com.fr.design.i18n.Toolkit.i18nText("FR-Chart-Type_Stacked");
        String percent = com.fr.design.i18n.Toolkit.i18nText("FR-Chart-Use_Percent");
        return new String[]{
                bar,
                stack + bar,
                percent + stack + bar,
                com.fr.design.i18n.Toolkit.i18nText("FR-Chart-Mode_Custom")
        };
    }

    /**
     * 返回界面标题
     * @return 界面标题
     */
    public String title4PopupWindow() {
        return com.fr.design.i18n.Toolkit.i18nText("Plugin-ChartF_NewBar");
    }


    /**
     * 获取各图表类型界面ID, 本质是plotID
     *
     * @return 图表类型界面ID
     */
    @Override
    protected String getPlotTypeID() {
        return VanChartColumnPlot.VAN_CHART_BAR_PLOT_ID;
    }

    protected Plot getSelectedClonedPlot(){
        VanChartColumnPlot newPlot = null;
        Chart[] barChart = BarIndependentVanChart.BarVanChartTypes;
        for(int i = 0, len = barChart.length; i < len; i++){
            if(typeDemo.get(i).isPressing){
                newPlot = (VanChartColumnPlot)barChart[i].getPlot();
            }
        }

        Plot cloned = null;
        try {
            cloned = (Plot)newPlot.clone();
        } catch (CloneNotSupportedException e) {
            FineLoggerFactory.getLogger().error("Error In ColumnChart");
        }
        return cloned;
    }

    public Chart getDefaultChart() {
        return BarIndependentVanChart.BarVanChartTypes[0];
    }

}
package com.fr.van.chart.map.designer.other.condition.item;

import com.fr.chart.base.DataSeriesCondition;
import com.fr.design.condition.ConditionAttributesPane;

import com.fr.plugin.chart.base.VanChartAttrMarker;
import com.fr.van.chart.designer.component.marker.VanChartImageMarkerPane;
import com.fr.van.chart.designer.other.condition.item.AbstractNormalMultiLineConditionPane;

import javax.swing.JPanel;

/**
 * Created by Mitisky on 16/5/23.
 */
public class VanChartImageMarkerConditionPane extends AbstractNormalMultiLineConditionPane{
    private VanChartImageMarkerPane imageMarkerPane;

    public VanChartImageMarkerConditionPane(ConditionAttributesPane conditionAttributesPane) {
        super(conditionAttributesPane);
    }

    @Override
    protected String getItemLabelString() {
        return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Marker");
    }

    @Override
    protected JPanel initContentPane() {
        imageMarkerPane = new VanChartImageMarkerPane();
        return imageMarkerPane;
    }

    /**
     * 条目名称
     *
     * @return 名称
     */
    @Override
    public String nameForPopupMenuItem() {
        return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Marker");
    }

    @Override
    public void populate(DataSeriesCondition condition) {
        if(condition instanceof VanChartAttrMarker){
            imageMarkerPane.populateBean((VanChartAttrMarker)condition);
        }

    }

    @Override
    public DataSeriesCondition update() {
        return imageMarkerPane.updateBean();
    }
}

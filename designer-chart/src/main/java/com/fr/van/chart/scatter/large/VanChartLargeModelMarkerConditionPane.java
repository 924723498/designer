package com.fr.van.chart.scatter.large;

import com.fr.design.condition.ConditionAttributesPane;
import com.fr.van.chart.designer.other.condition.item.VanChartMarkerConditionPane;

/**
 * 标记点条件属性界面
 */
public class VanChartLargeModelMarkerConditionPane extends VanChartMarkerConditionPane {

    public VanChartLargeModelMarkerConditionPane(ConditionAttributesPane conditionAttributesPane) {
        super(conditionAttributesPane);
    }

    protected void initMarkerPane() {
        markerPane = new VanChartLargeModelMarkerPane();
    }
}
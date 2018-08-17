package com.fr.design.chart.report;

import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartattr.MapPlot;
import com.fr.design.dialog.BasicPane;
import com.fr.design.dialog.MultiTabPane;


import java.util.ArrayList;
import java.util.List;

/**
 * 地图多层钻取界面, 就是一个多层Tab切换界面
 * @author kunsnat E-mail:kunsnat@gmail.com
 * @version 创建时间：2012-10-22 上午10:23:37
 */
public class MapMoreCubeLayerPane extends MultiTabPane<ChartCollection>{
	private static final long serialVersionUID = -174286187746442527L;
	
	private MapCubeLayerPane layerPane;
	private MapCubeDataPane dataPane;
	
	@Override
	protected List<BasicPane> initPaneList() {
		List<BasicPane> paneList = new ArrayList<BasicPane>();

		paneList.add(layerPane = new MapCubeLayerPane());
		paneList.add(dataPane = new MapCubeDataPane());
		
		return paneList;
	}
	
	public ChartCollection updateBean() {
		return null;// do nothing
	}
	
	public void populateBean(ChartCollection collection) {
		Chart selectChart = collection.getSelectedChart();
		if(selectChart != null && selectChart.getPlot() instanceof MapPlot) {
			MapPlot map = (MapPlot)selectChart.getPlot();
            layerPane.setSvg(map.isSvgMap());
			layerPane.populateBean(map.getMapName());
		}

		// 确认层级关系 
		dataPane.populateBean(collection.getSelectedChart().getFilterDefinition());
	}

	public void updateBean(ChartCollection collection) {
		
		collection.getSelectedChart().setFilterDefinition(dataPane.update());
		
		Chart selectChart = collection.getSelectedChart();
		if(selectChart != null && selectChart.getPlot() instanceof MapPlot) {
			MapPlot map = (MapPlot)selectChart.getPlot();
			layerPane.updateBean(map.getMapName());// 确定更新地图名称所对应的层级关系
		}
	}
	
	/**
	 * 刷新层级树 和 数据中populate 数据的层数
     * @param collection  图表收集器.
	 */
	public void init4PopuMapTree(ChartCollection collection) {
		Chart selectChart = collection.getSelectedChart();
		if(selectChart != null && selectChart.getPlot() instanceof MapPlot) {
			MapPlot map = (MapPlot)selectChart.getPlot();
			if(layerPane != null) {
                layerPane.setSvg(map.isSvgMap());
				layerPane.initRootTree(map.getMapName());
			}
		}
	}

    /**
     * 判断是否合格
     * @param ob  参数判断
     * @return 默认合格.
     */
	public boolean accept(Object ob) {
		return true;
	}

    /**
     * 界面标题
     * @return 返回标题
     */
	public String title4PopupWindow() {
		return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Muiti_In");
	}

    /**
     * 重置
     */
	public void reset() {

	}
	
	/**
	 * 设置是否支持单元格数据.
	 */
	public void setSurpportCellData(boolean surpportCellData) {
		dataPane.justSupportOneSelect(surpportCellData);
	}
}
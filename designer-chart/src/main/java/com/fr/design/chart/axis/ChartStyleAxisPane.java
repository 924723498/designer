package com.fr.design.chart.axis;

import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fr.chart.chartattr.Axis;
import com.fr.chart.chartattr.Plot;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.mainframe.chart.gui.style.axis.ChartAxisUsePane;
import com.fr.design.dialog.BasicPane;


/**
 * Created by IntelliJ IDEA.
 * Author : Richer
 * Version: 6.5.6
 * Date   : 11-12-2
 * Time   : 上午8:48
 */
public abstract class ChartStyleAxisPane extends BasicPane implements ListSelectionListener {
    protected static final String CATE_AXIS = com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Category");
    protected static final String VALUE_AXIS = com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Radar_Value_Axis");
    protected static final String SECOND_AXIS = com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Second_Value_Axis");
    private JList mainList;
    private CardLayout cardLayout;
    private JPanel cardDisplayPane = null;
    private java.util.List<ChartAxisUsePane> axisStylePaneList = new ArrayList<ChartAxisUsePane>();

    public ChartStyleAxisPane(Plot plot) {
        initComponents(plot);
    }

    private void initComponents(Plot plot) {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());

        DefaultListModel listModel = new DefaultListModel();
        mainList = new JList(listModel);
        AxisStyleObject[] axisStyleObjects = createAxisStyleObjects(plot);
        cardLayout = new CardLayout();
        cardDisplayPane = FRGUIPaneFactory.createCardLayout_S_Pane();
        cardDisplayPane.setLayout(cardLayout);
        for (int i = 0; i < axisStyleObjects.length; i ++) {
            AxisStyleObject as = axisStyleObjects[i];
            listModel.addElement(as.getName());
            ChartAxisUsePane axisStylePane = as.getAxisStylePane();
            axisStylePaneList.add(axisStylePane);
            cardDisplayPane.add(axisStylePane, as.getName());
        }
        mainList.setSelectedIndex(0);
        mainList.addListSelectionListener(this);
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mainList, cardDisplayPane));
    }

    @Override
    protected String title4PopupWindow() {
        return "Axis";
    }

    public void valueChanged(ListSelectionEvent e) {
       cardLayout.show(cardDisplayPane, (String)mainList.getSelectedValue());
    }

    public abstract AxisStyleObject[] createAxisStyleObjects(Plot plot);

    public void populate(Plot plot) {
        for (int i = 0, len = axisStylePaneList.size(); i < len; i++) {
//            axisStylePaneList.get(i).populate(getAxisFromPlotByListIndex(plot, i));
        }
    }

    public void update(Plot plot) {
        for (int i = 0, len = axisStylePaneList.size(); i < len; i++) {
//            axisStylePaneList.get(i).update(getAxisFromPlotByListIndex(plot, i));
        }
    }

    private Axis getAxisFromPlotByListIndex(Plot plot, int index) {
        return plot.getAxis(index);
    }
}
package com.fr.design.widget.ui.designer.layout;

import com.fr.design.data.DataCreatorUI;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.designer.creator.XWAbsoluteLayout;
import com.fr.design.designer.properties.items.FRAbsoluteConstraintsItems;
import com.fr.design.designer.properties.items.Item;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.widget.ui.designer.AbstractDataModify;
import com.fr.design.widget.ui.designer.component.WidgetBoundPane;
import com.fr.form.ui.container.WAbsoluteLayout;
import com.fr.form.ui.container.WBodyLayoutType;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ibm on 2017/8/2.
 */
public class FRAbsoluteLayoutDefinePane extends AbstractDataModify<WAbsoluteLayout> {
    private XWAbsoluteLayout xwAbsoluteLayout;
    private WAbsoluteLayout wAbsoluteLayout;
    protected UIComboBox comboBox;
    private WBodyLayoutType layoutType = WBodyLayoutType.ABSOLUTE;
    private WidgetBoundPane boundPane;

    public FRAbsoluteLayoutDefinePane(XCreator xCreator) {
        super(xCreator);
        this.xwAbsoluteLayout = (XWAbsoluteLayout) xCreator;
        wAbsoluteLayout = xwAbsoluteLayout.toData();
        initComponent();

    }


    public void initComponent() {
        boundPane = new WidgetBoundPane(creator);
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        this.add(boundPane, BorderLayout.NORTH);
        initUIComboBox();
        JPanel thirdPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        thirdPane.add(createThirdPane(), BorderLayout.CENTER);
        thirdPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        UIExpandablePane layoutExpandablePane = new UIExpandablePane(Inter.getLocText("FR-Designer-Widget_Area_Scaling"), 280, 20,thirdPane );

        this.add(layoutExpandablePane, BorderLayout.CENTER);
    }

    public JPanel createThirdPane() {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer-Widget_Scaling_Mode")), comboBox},
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 7);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return panel;
    }


    public void initUIComboBox() {
        Item[] items = FRAbsoluteConstraintsItems.ITEMS;
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Item item : items) {
            model.addElement(item);
        }
        comboBox = new UIComboBox(model);
    }

    @Override
    public String title4PopupWindow() {
        return "absoluteLayout";
    }

    @Override
    public void populateBean(WAbsoluteLayout ob) {
        populateSubPane(ob);
        comboBox.setSelectedIndex(ob.getCompState());
        boundPane.populate();
    }


    @Override
    public WAbsoluteLayout updateBean() {
        WAbsoluteLayout wAbsoluteLayout = updateSubPane();
        wAbsoluteLayout.setCompState(comboBox.getSelectedIndex());
        boundPane.update();
        return wAbsoluteLayout;

    }

    public WAbsoluteLayout updateSubPane() {
        return new WAbsoluteLayout();
    }

    public void populateSubPane(WAbsoluteLayout ob) {

    }

    @Override
    public DataCreatorUI dataUI() {
        return null;
    }

}


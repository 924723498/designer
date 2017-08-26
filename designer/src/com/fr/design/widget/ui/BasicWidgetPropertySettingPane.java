package com.fr.design.widget.ui;

import com.fr.design.constants.LayoutConstants;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.dialog.BasicPane;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.form.ui.NoneWidget;
import com.fr.form.ui.Widget;
import com.fr.general.Inter;
import com.fr.design.utils.gui.GUICoreUtils;

import javax.swing.*;
import java.awt.*;

public class BasicWidgetPropertySettingPane extends BasicPane {
	private ParameterTreeComboBox widgetNameComboBox;
	private UICheckBox enableCheckBox;
	private UICheckBox visibleCheckBox;
	private Widget widget;
	
	public BasicWidgetPropertySettingPane() {
		this.setLayout(new BorderLayout());
		enableCheckBox = new UICheckBox(Inter.getLocText("FR-Designer_Enabled"), true);
		enableCheckBox.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		visibleCheckBox = new UICheckBox(Inter.getLocText("FR-Designer_Widget-Visible"), true);
		visibleCheckBox.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		widgetNameComboBox = new ParameterTreeComboBox();
		widgetNameComboBox.refreshTree();
		JPanel widgetNamePane = new JPanel(new BorderLayout());
		widgetNamePane.add(widgetNameComboBox, BorderLayout.CENTER);
		widgetNamePane.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
		double f = TableLayout.FILL;
		double p = TableLayout.PREFERRED;
		Component[][] components = new Component[][]{
				new Component[]{new UILabel(Inter.getLocText("FR-Designer_Form-Widget_Name")), widgetNamePane},
				new Component[]{enableCheckBox, null},
				new Component[]{visibleCheckBox, null},
		};
		double[] rowSize = {p, p, p};
		double[] columnSize = {p, f};
		int[][] rowCount = {{1, 1},{1, 1},{1, 1},{1, 1}};
		JPanel pane = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, LayoutConstants.VGAP_LARGE, LayoutConstants.VGAP_LARGE);
		pane.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

		this.add(pane, BorderLayout.CENTER);

	}
	
	@Override
	protected String title4PopupWindow() {
		return "property";
	}
	
	public void populate(Widget widget){
		//:jackie
		if(widget instanceof NoneWidget){
			this.widget = null;
			GUICoreUtils.setEnabled(this, false);
			return;
		} else{
			GUICoreUtils.setEnabled(this, true);
			this.widget = widget;
			widgetNameComboBox.setSelectedItem(widget.getWidgetName());
			enableCheckBox.setSelected(this.widget.isEnabled());
			visibleCheckBox.setSelected(this.widget.isVisible());
		}
	}
	
	public void update(Widget widget){
		if(this.widget == null)
			return ;
		widget.setWidgetName(widgetNameComboBox.getSelectedParameterName());
		widget.setEnabled(enableCheckBox.isSelected());
		widget.setVisible(visibleCheckBox.isSelected());
	}
}
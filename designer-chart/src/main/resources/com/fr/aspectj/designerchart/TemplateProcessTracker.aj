package com.fr.aspectj.designerchart;

/**
 * Created by plough on 2017/3/3.
 */
import com.fr.chart.chartattr.Chart;
import com.fr.design.mainframe.templateinfo.TemplateInfoCollector;
import org.aspectj.lang.reflect.SourceLocation;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Date;

public aspect TemplateProcessTracker {
    //声明一个pointcut，匹配你需要的方法
    pointcut onMouseClicked(MouseEvent e) :
            execution(* mouseClicked(MouseEvent)) && args(e);
    pointcut onMousePressed(MouseEvent e) :
            execution(* mousePressed(MouseEvent)) && args(e);
    pointcut onMouseReleased(MouseEvent e) :
            execution(* mouseReleased(MouseEvent)) && args(e);
    pointcut onActionPerformed(ActionEvent e) :
            execution(* actionPerformed(ActionEvent)) && args(e);
    pointcut onChartUpdate(Chart c) :
            execution(* update(Chart)) && args(c);

    //before表示之前的意思
    //这整个表示在MouseAdapter的public void mouseXXX(MouseEvent)方法调用之前，你想要执行的代码
    before(MouseEvent e) : onMouseClicked(e) || onMousePressed(e) || onMouseReleased(e) {
        SourceLocation sl = thisJoinPoint.getSourceLocation();//切面对应的代码位置

        //String log = String.format("%s:\n%s\n%s\n%s\n\n", new Date(), sl, e, e.getSource());
        String log = "";
        //TemplateInfoCollector.appendProcess(log);
    }
    //同上
    before(ActionEvent e) : onActionPerformed(e) {
        SourceLocation sl = thisJoinPoint.getSourceLocation();
        // !within(LogHandlerBar) 没用, 手动过滤
        if (e != null && e.getSource().toString().contains("javax.swing.Timer")) {
            return;
        }

        //String log = String.format("%s:\n%s\n%s\n%s\n\n", new Date(), sl, e, e.getSource());
        String log = "";
        //TemplateInfoCollector.appendProcess(log);

    }
    //同上
    before(Chart c) : onChartUpdate(c) {
        SourceLocation sl = thisJoinPoint.getSourceLocation();
        //String log = String.format("%s:\n%s\n插入新图表：%s\n\n", new Date(), sl, c.getChartName());
        String log = "";
        //TemplateInfoCollector.appendProcess(log);

    }

}

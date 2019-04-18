package com.fr.design.mainframe.template.info;

import com.fr.config.MarketConfig;
import com.fr.design.DesignerEnvManager;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONObject;
import com.fr.stable.ProductConstants;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLWriter;
import com.fr.stable.xml.XMLableReader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 对应一张模版的记录
 * Created by plough on 2019/4/18.
 */
class TemplateInfo implements XMLReadable, XMLWriter {
    static final String XML_TAG = "TemplateInfo";

    private static final String XML_PROCESS_MAP = "processMap";
    private static final String XML_CONSUMING_MAP = "consumingMap";
    private static final String ATTR_DAY_COUNT = "day_count";
    private static final String ATTR_TEMPLATE_ID = "templateID";
    private static final String ATTR_PROCESS = "process";
    private static final String ATTR_FLOAT_COUNT = "float_count";
    private static final String ATTR_WIDGET_COUNT = "widget_count";
    private static final String ATTR_CELL_COUNT = "cell_count";
    private static final String ATTR_BLOCK_COUNT = "block_count";
    private static final String ATTR_REPORT_TYPE = "report_type";
    private static final String ATTR_ACTIVITYKEY = "activitykey";
    private static final String ATTR_JAR_TIME = "jar_time";
    private static final String ATTR_CREATE_TIME = "create_time";
    private static final String ATTR_UUID = "uuid";
    private static final String ATTR_TIME_CONSUME = "time_consume";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_USERNAME = "username";

    private static final int VALID_CELL_COUNT = 5;  // 有效报表模板的格子数
    private static final int VALID_WIDGET_COUNT = 5;  // 有效报表模板的控件数
    private static final int COMPLETE_DAY_COUNT = 15;  // 判断模板是否完成的天数


    private int idleDayCount;  // 到现在为止，模版闲置（上次保存后没有再编辑过）的天数
    private String templateID;
    private HashMap<String, Object> processMap = new HashMap<>();
    private HashMap<String, Object> consumingMap = new HashMap<>();

    private TemplateInfo() {
    }

    static TemplateInfo newInstanceByRead(XMLableReader reader) {
        TemplateInfo templateInfo = new TemplateInfo();
        reader.readXMLObject(templateInfo);
        return templateInfo;
    }

    static TemplateInfo newInstance(String templateID) {
        HashMap<String, Object> consumingMap = new HashMap<>();

        String username = MarketConfig.getInstance().getBbsUsername();
        String uuid = DesignerEnvManager.getEnvManager().getUUID();
        String activitykey = DesignerEnvManager.getEnvManager().getActivationKey();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        String jarTime = GeneralUtils.readBuildNO();
        String version = ProductConstants.VERSION;
        consumingMap.put(ATTR_USERNAME, username);
        consumingMap.put(ATTR_UUID, uuid);
        consumingMap.put(ATTR_ACTIVITYKEY, activitykey);
        consumingMap.put(ATTR_TEMPLATE_ID, templateID);
        consumingMap.put(ATTR_CREATE_TIME, createTime);
        consumingMap.put(ATTR_TIME_CONSUME, 0);
        consumingMap.put(ATTR_JAR_TIME, jarTime);
        consumingMap.put(ATTR_VERSION, version);

        TemplateInfo templateInfo = new TemplateInfo();
        templateInfo.consumingMap = consumingMap;

        return templateInfo;
    }

    String getTemplateID() {
        return templateID;
    }

    HashMap<String, Object> getTemplateInfo() {
        HashMap<String, Object> templateInfo = new HashMap<>();
        templateInfo.put(XML_PROCESS_MAP, processMap);
        templateInfo.put(XML_CONSUMING_MAP, consumingMap);
        templateInfo.put(ATTR_DAY_COUNT, idleDayCount);
        return templateInfo;
    }

    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        if (StringUtils.isNotEmpty(templateID)) {
            writer.attr(ATTR_TEMPLATE_ID, this.templateID);
        }
        if (idleDayCount >= 0) {
            writer.attr(ATTR_DAY_COUNT, this.idleDayCount);
        }
        writeProcessMap(writer);
        writeConsumingMap(writer);

        writer.end();
    }

    private void writeProcessMap(XMLPrintWriter writer) {
        writer.startTAG(XML_PROCESS_MAP);
        writer.attr(ATTR_PROCESS, (String) processMap.get(ATTR_PROCESS));
        writer.attr(ATTR_FLOAT_COUNT, (int) processMap.get(ATTR_FLOAT_COUNT));
        writer.attr(ATTR_WIDGET_COUNT, (int) processMap.get(ATTR_WIDGET_COUNT));
        writer.attr(ATTR_CELL_COUNT, (int) processMap.get(ATTR_CELL_COUNT));
        writer.attr(ATTR_BLOCK_COUNT, (int) processMap.get(ATTR_BLOCK_COUNT));
        writer.attr(ATTR_REPORT_TYPE, (int) processMap.get(ATTR_REPORT_TYPE));
        writer.end();
    }

    private void writeConsumingMap(XMLPrintWriter writer) {
        writer.startTAG(XML_CONSUMING_MAP);
        writer.attr(ATTR_ACTIVITYKEY, (String) consumingMap.get(ATTR_ACTIVITYKEY));
        writer.attr(ATTR_JAR_TIME, (String) consumingMap.get(ATTR_JAR_TIME));
        writer.attr(ATTR_CREATE_TIME, (String) consumingMap.get(ATTR_CREATE_TIME));
        writer.attr(ATTR_UUID, (String) consumingMap.get(ATTR_UUID));
        writer.attr(ATTR_TIME_CONSUME, (long) consumingMap.get(ATTR_TIME_CONSUME));
        writer.attr(ATTR_VERSION, (String) consumingMap.get(ATTR_VERSION));
        writer.attr(ATTR_USERNAME, (String) consumingMap.get(ATTR_USERNAME));
        writer.end();
    }

    public void readXML(XMLableReader reader) {
        if (!reader.isChildNode()) {
            idleDayCount = reader.getAttrAsInt(ATTR_DAY_COUNT, 0);
            templateID = reader.getAttrAsString(ATTR_TEMPLATE_ID, StringUtils.EMPTY);
        } else {
            try {
                String name = reader.getTagName();
                if (XML_PROCESS_MAP.equals(name)) {
                    processMap.put(ATTR_PROCESS, reader.getAttrAsString(ATTR_PROCESS, StringUtils.EMPTY));
                    processMap.put(ATTR_FLOAT_COUNT, reader.getAttrAsInt(ATTR_FLOAT_COUNT, 0));
                    processMap.put(ATTR_WIDGET_COUNT, reader.getAttrAsInt(ATTR_WIDGET_COUNT, 0));
                    processMap.put(ATTR_CELL_COUNT, reader.getAttrAsInt(ATTR_CELL_COUNT, 0));
                    processMap.put(ATTR_BLOCK_COUNT, reader.getAttrAsInt(ATTR_BLOCK_COUNT, 0));
                    processMap.put(ATTR_REPORT_TYPE, reader.getAttrAsInt(ATTR_REPORT_TYPE, 0));
                    processMap.put(ATTR_TEMPLATE_ID, templateID);
                } else if (XML_CONSUMING_MAP.equals(name)) {
                    consumingMap.put(ATTR_ACTIVITYKEY, reader.getAttrAsString(ATTR_ACTIVITYKEY, StringUtils.EMPTY));
                    consumingMap.put(ATTR_JAR_TIME, reader.getAttrAsString(ATTR_JAR_TIME, StringUtils.EMPTY));
                    consumingMap.put(ATTR_CREATE_TIME, reader.getAttrAsString(ATTR_CREATE_TIME, StringUtils.EMPTY));
                    consumingMap.put(ATTR_TEMPLATE_ID, templateID);
                    consumingMap.put(ATTR_UUID, reader.getAttrAsString(ATTR_UUID, StringUtils.EMPTY));
                    consumingMap.put(ATTR_TIME_CONSUME, reader.getAttrAsLong(ATTR_TIME_CONSUME, 0));
                    consumingMap.put(ATTR_VERSION, reader.getAttrAsString(ATTR_VERSION, "8.0"));
                    consumingMap.put(ATTR_USERNAME, reader.getAttrAsString(ATTR_USERNAME, StringUtils.EMPTY));
                }
            } catch (Exception ex) {
                // 什么也不做，使用默认值
            }
        }
    }

    boolean isTestTemplate() {
        if (!isComplete()) {
            return false;
        }

        int reportType = (int) processMap.get(ATTR_REPORT_TYPE);
        int cellCount = (int) processMap.get(ATTR_CELL_COUNT);
        int floatCount = (int) processMap.get(ATTR_FLOAT_COUNT);
        int blockCount = (int) processMap.get(ATTR_BLOCK_COUNT);
        int widgetCount = (int) processMap.get(ATTR_WIDGET_COUNT);
        boolean isTestTemplate = false;
        if (reportType == 0) {  // 普通报表
            isTestTemplate = cellCount <= VALID_CELL_COUNT && floatCount <= 1 && widgetCount <= VALID_WIDGET_COUNT;
        } else if (reportType == 1) {  // 聚合报表
            isTestTemplate = blockCount <= 1 && widgetCount <= VALID_WIDGET_COUNT;
        } else {  // 表单(reportType == 2)
            isTestTemplate = widgetCount <= 1;
        }
        return isTestTemplate;
    }

    boolean isComplete() {
        return idleDayCount > COMPLETE_DAY_COUNT;
    }

    String getConsumingMapJsonString() {
        return new JSONObject(consumingMap).toString();
    }

    String getProcessMapJsonString() {
        return new JSONObject(processMap).toString();
    }

    boolean isReadyForSend() {
        return isComplete() && !isTestTemplate();
    }

    void addTimeConsume(long timeConsume) {
        timeConsume += (long) consumingMap.get(ATTR_TIME_CONSUME);  // 加上之前的累计编辑时间
        consumingMap.put(ATTR_TIME_CONSUME, timeConsume);
    }

    void updateProcessMap(TemplateProcessInfo processInfo) {
        HashMap<String, Object> processMap = new HashMap<>();

        // 暂不支持模版制作过程的收集
        processMap.put(ATTR_PROCESS, StringUtils.EMPTY);

        processMap.put(ATTR_REPORT_TYPE, processInfo.getReportType());
        processMap.put(ATTR_CELL_COUNT, processInfo.getCellCount());
        processMap.put(ATTR_FLOAT_COUNT, processInfo.getFloatCount());
        processMap.put(ATTR_BLOCK_COUNT, processInfo.getBlockCount());
        processMap.put(ATTR_WIDGET_COUNT, processInfo.getWidgetCount());

        this.processMap = processMap;
    }

    void setIdleDayCount(int idleDayCount) {
        this.idleDayCount = idleDayCount;
    }

    void addIdleDayCountByOne() {
        this.idleDayCount += 1;
    }
}
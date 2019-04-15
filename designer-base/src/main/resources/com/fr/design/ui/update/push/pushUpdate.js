function i18nText(key) {
    return Pool.data.i18nText(key);
}

window.addEventListener("load", function (ev) {
    var title = BI.createWidget({
        type: "bi.vertical",
        items: [
            {
                type: "bi.label",
                text: i18nText("Fine-Design_Find_New_Version"),
                cls: "title",
                textAlign: "left"
            },
            {
                type: "bi.label",
                text: Pool.data.getVersion(),
                textAlign: "left"
            }
        ]
    });

    var desc = BI.createWidget({
        type: "bi.vertical",
        cls: "desc",
        items: [
            {
                type: "bi.label",
                text: Pool.data.getContent(),
                textAlign: "left"
            }
        ]
    });

    var moreInfo = BI.createWidget({
        type: "bi.text_button",
        text: i18nText("Fine-Design_Basic_More_Information"),
        cls: "moreInfo",
        textAlign: "left"
    });

    var buttonGroup = BI.createWidget({
        type: 'bi.left',
        cls: "buttonGroup",
        items: [
            {
                type: 'bi.button',
                text: i18nText("Fine-Design_Update_Now"),
                level: 'common',
                height: 30,
                handler: function() {
                    Pool.data.updateNow();
                }
            },
            {
                el: {
                    type: 'bi.button',
                    text: i18nText("Fine-Design_Remind_Me_Next_Time"),
                    level: 'ignore',
                    height: 30,
                    handler: function() {
                        Pool.data.remindNextTime();
                    }
                },
                lgap: 10
            },
            {
                el: {
                    type: 'bi.button',
                    text: i18nText("Fine-Design_Skip_This_Version"),
                    level: 'ignore',
                    height: 30,
                    handler: function() {
                        Pool.data.skipThisVersion();
                    }
                },
                lgap: 10
            }
        ]
    });

    BI.createWidget({
        type:"bi.vertical",
        element: "body",
        cls: "container",
        items: [
            title,
            desc,
            moreInfo,
            buttonGroup
        ]
    });

    $(".container").css("background", "url(" + Pool.data.getBackgroundUrl() + ")");
    $(".button-ignore").css({
        "background-color": "white",
        "border": "1px solid white"
    });
});
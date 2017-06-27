package com.fr.design.mainframe.alphafine.cell.model;

import com.fr.design.mainframe.alphafine.CellType;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by XiaXiang on 2017/4/20.
 */
public class MoreModel extends AlphaCellModel {
    private boolean needMore;
    private boolean isLoading;

    public MoreModel(String name, String content, boolean needMore, CellType type) {
        super(name, content, type);
        this.needMore = needMore;
    }

    public MoreModel(String name, CellType type) {
        super(name, null, type);
        this.needMore = false;
    }

    public MoreModel(String name, boolean isLoading) {
        super(name, null);
        this.isLoading = isLoading;
    }

    public boolean isNeedMore() {
        return needMore;
    }

    public void setNeedMore(boolean needMore) {
        this.needMore = needMore;
    }

    @Override
    public JSONObject ModelToJson() throws JSONException {
        return null;
    }

    @Override
    public String getStoreInformation() {
        return null;
    }

    @Override
    public void doAction() {

    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public boolean hasNoResult() {
        return true;
    }

    @Override
    public boolean isNeedToSendToServer() {
        return false;
    }
}

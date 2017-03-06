package com.example.administrator.greendao.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class UpdateStep {

    private String versionTo;

    private String[] versionfroms;

    private List<UpdateDb> updateDbs;

    public UpdateStep(Element element) {
        versionTo = element.getAttribute("versionTo");
        String versionfrom = element.getAttribute("versionfrom");
        versionfroms = versionfrom.split(",");
        updateDbs = new ArrayList<>();

        NodeList nodeList = element.getElementsByTagName("updateDb");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element1 = (Element) nodeList.item(i);
            UpdateDb updateDb = new UpdateDb(element1);
            updateDbs.add(updateDb);
        }
    }

    public String getVersionTo() {
        return versionTo;
    }

    public String[] getVersionfroms() {
        return versionfroms;
    }

    public List<UpdateDb> getUpdateDbs() {
        return updateDbs;
    }
}

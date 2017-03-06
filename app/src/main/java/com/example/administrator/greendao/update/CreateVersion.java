package com.example.administrator.greendao.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class CreateVersion {

    private String version;

    private List<CreateDb> createDbs;

    public CreateVersion(Element element) {

        createDbs=  new ArrayList<>();
        version = element.getAttribute("version");

        NodeList nodeList = element.getElementsByTagName("createDb");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element1 = (Element) nodeList.item(i);
            CreateDb db = new CreateDb(element1);
            createDbs.add(db);
        }

    }

    public String getVersion() {
        return version;
    }

    public List<CreateDb> getCreateDbs() {
        return createDbs;
    }
}

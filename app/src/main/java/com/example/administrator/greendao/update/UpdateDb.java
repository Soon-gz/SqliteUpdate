package com.example.administrator.greendao.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class UpdateDb {

    private String name;

    private List<String> sqlBefores;

    private List<String> sqlAfters;

    public UpdateDb(Element element1) {
        name = element1.getAttribute("name");
        sqlAfters = new ArrayList<>();
        sqlBefores = new ArrayList<>();

        NodeList nodeList = element1.getElementsByTagName("sql_before");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String sqlBefore = element.getTextContent();
            sqlBefore.replaceAll("\r\n"," ");
            sqlBefore.replaceAll("\\n"," ");
            sqlBefores.add(sqlBefore);
        }

        NodeList nodeList1 = element1.getElementsByTagName("sql_after");
        for (int i = 0; i < nodeList1.getLength(); i++) {
            Element element = (Element) nodeList1.item(i);
            String sqlAfter = element.getTextContent();
            sqlAfter.replaceAll("\r\n"," ");
            sqlAfter.replaceAll("\\n"," ");
            sqlAfters.add(sqlAfter);
        }

    }

    public String getName() {
        return name;
    }

    public List<String> getSqlBefores() {
        return sqlBefores;
    }

    public List<String> getSqlAfters() {
        return sqlAfters;
    }
}

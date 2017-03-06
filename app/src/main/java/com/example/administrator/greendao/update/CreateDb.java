package com.example.administrator.greendao.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class CreateDb {

    private String name;

    private List<String> sqlCreates;

    public CreateDb(Element element1) {
        sqlCreates = new ArrayList<>();
        name = element1.getAttribute("name");
        NodeList nodeList = element1.getElementsByTagName("sql_createtable");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String sql = element.getTextContent();
            sql.replaceAll("\r\n"," ");
            sql.replaceAll("\\n"," ");
            sqlCreates.add(sql);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getSqlCreates() {
        return sqlCreates;
    }
}

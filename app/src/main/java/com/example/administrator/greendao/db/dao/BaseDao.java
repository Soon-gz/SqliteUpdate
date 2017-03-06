package com.example.administrator.greendao.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.greendao.db.annotation.TbClass;
import com.example.administrator.greendao.db.annotation.TbField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase database;
    private HashMap<String,Field> cacheMap;
    private String tableName;
    private Class<T> entityClazz;
    private boolean isInit = false;

    protected synchronized boolean init(Class<T> entity,SQLiteDatabase sqLiteDatabase){
        if (!isInit){
            this.database = sqLiteDatabase;
            this.entityClazz = entity;
            if (entityClazz.getAnnotation(TbClass.class) != null){
                tableName = entityClazz.getAnnotation(TbClass.class).value();
            }else {
                tableName = entity.getSimpleName();
            }
            if (!sqLiteDatabase.isOpen()){
                return false;
            }
            if (!TextUtils.isEmpty(createTable(entity))){
                database.execSQL(createTable(entity));
            }
            cacheMap = new HashMap<>();
            initCacheMap();

        }
        return isInit;
    }

    protected String createTable(Class<T> entity){
        Field[]fields = entity.getDeclaredFields();
        String tableName;
        if (entity.getAnnotation(TbClass.class) != null){
            tableName = ((TbClass)entity.getAnnotation(TbClass.class)).value();
        }else {
            tableName = entity.getSimpleName();
        }
        List<Field> aFileds = new ArrayList<>();
        for (Field field:fields) {
            if (field.getAnnotation(TbField.class) != null){
                aFileds.add(field);
            }
        }

        List<String> fieldNames = new ArrayList<>();
        List<String> fieldTypes = new ArrayList<>();
        for (int i = 0;i < aFileds.size() ;i++) {
            String fieldName;
            String fieldType;
            if (aFileds.get(i).getAnnotation(TbField.class) != null){
                fieldName = aFileds.get(i).getAnnotation(TbField.class).value();
                Class type = aFileds.get(i).getType();
                if(type==String.class){
                    fieldType = "TEXT";
                }else if(type==Double.class){
                    fieldType = "REAL";
                }else if(type== Integer.class){
                    fieldType = "INTEGER";
                }else if(type == Long.class){
                    fieldType = "LONG";
                }else if(type == byte[].class){
                    fieldType = "BLOB";
                }else{
                    /**
                     * 不支持的类型
                     */
                    continue;
                }
            }else {
                fieldType = "varchar(20)";
                fieldName = aFileds.get(i).getName();
            }
            fieldTypes.add(fieldType);
            fieldNames.add(fieldName);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists " + tableName +"(");
        for (int index = 0; index < fieldNames.size(); index++) {

            builder.append(fieldNames.get(index) +" "+ fieldTypes.get(index));
            if (index != fieldNames.size() - 1){
                builder.append(",");
            }else {
                builder.append(")");
            }
        }
        return builder.toString();
    }

    private void initCacheMap() {
        String sql = "select * from "+tableName+" limit 0,1";
        Cursor cursor = null;
        try{
            cursor = database.rawQuery(sql,null);
            String[]colNames = cursor.getColumnNames();
            Field[]fields = entityClazz.getDeclaredFields();
            for (Field field:fields) {
                field.setAccessible(true);
            }
            for (String colName:colNames) {
                Field colField = null;
                for (Field field:fields) {
                    String fieldName;
                    if (field.getAnnotation(TbField.class) != null){
                        fieldName = field.getAnnotation(TbField.class).value();
                    }else {
                        fieldName = field.getName();
                    }

                    if (fieldName.equals(colName)){
                        colField = field;
                        break;
                    }
                }

                if (colField != null){
                    cacheMap.put(colName,colField);
                }
            }
        }catch (Exception e){

        }finally {
            cursor.close();
        }
    }

    @Override
    public Long insert(T entity) {
        HashMap<String,String>map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        Long result = database.insert(tableName,null,contentValues);
        return result;
    }

    @Override
    public int update(T newEntity, T where) {

        HashMap<String,String> whereMap = getValues(where);

        HashMap<String,String> newEntityMap = getValues(newEntity);

        ContentValues contentValues = getContentValues(newEntityMap);

        Condition condition = new Condition(whereMap);

        int result = database.update(tableName,contentValues,condition.getWhereCause(),condition.getWhereCauseArry());

        return result;
    }

    @Override
    public int delete(T entity) {

        HashMap<String,String> whereMap = getValues(entity);

        Condition condition = new Condition(whereMap);

        int result = database.delete(tableName,condition.getWhereCause(),condition.getWhereCauseArry());
        return result;
    }

    @Override
    public List<T> query(T entity) {
        return query(entity,null,null,null);
    }

    @Override
    public List<T> query(T entity,String orderby,Integer startInt,Integer limit ) {

        HashMap<String,String> whereMap = getValues(entity);
        String lintStr = null;
        if (startInt != null && limit != null){
            lintStr  = startInt+" , "+limit;
        }

        Condition condition = new Condition(whereMap);

        Cursor cursor = database.query(tableName,null,condition.getWhereCause(),condition.getWhereCauseArry(),null,null,orderby,lintStr);

        List<T> result = getResult(cursor,entity);

        return result;
    }

    private List<T> getResult(Cursor cursor, T entity) {
        List result = new ArrayList();
        Object item;
        while (cursor.moveToNext()){
            try {
                item = entity.getClass().newInstance();
                Iterator<Map.Entry<String,Field>> colNameIterator = cacheMap.entrySet().iterator();
                while (colNameIterator.hasNext()){
                    Map.Entry<String,Field> entry = colNameIterator.next();
                    String colName = entry.getKey();
                    int colIndex = cursor.getColumnIndex(colName);
                    Field colField = entry.getValue();
                    Class type = colField.getType();
                    if(colIndex!=-1){
                        if(type==String.class){
                            colField.set(item,cursor.getString(colIndex));
                        }else if(type==Double.class){
                            colField.set(item,cursor.getDouble(colIndex));
                        }else if(type== Integer.class){
                            int value =cursor.getInt(colIndex);
                            Log.i("dongnao","value="+value);
                            colField.set(item,cursor.getInt(colIndex));
                        }else if(type == Long.class){
                            colField.set(item,cursor.getLong(colIndex));
                        }else if(type == byte[].class){
                            colField.set(item,cursor.getBlob(colIndex));
                        }else{
                            /**
                             * 不支持的类型
                             */
                            continue;
                        }
                    }

                }
                result.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private ContentValues getContentValues(HashMap<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Iterator<String>keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()){
            String cacheKey = keyIterator.next();
            String cacheValue = map.get(cacheKey);
            if (cacheValue != null){
                contentValues.put(cacheKey,cacheValue);
            }
        }
        return contentValues;
    }

    private HashMap<String,String> getValues(T entity){
        HashMap<String,String> map = new HashMap<>();
        Iterator<Field> valueIterator = cacheMap.values().iterator();
        while (valueIterator.hasNext()){
            String cacheKey;
            String cacheValue;
            Field colField = valueIterator.next();
            if (colField.getAnnotation(TbField.class) != null){
                cacheKey = colField.getAnnotation(TbField.class).value();
            }else {
                cacheKey = colField.getName();
            }
            try {
                if (null == colField.get(entity)){
                    continue;
                }
                cacheValue = colField.get(entity).toString();
                map.put(cacheKey,cacheValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private class Condition{
        private String whereCause;
        private String[] whereCauseArry;

        public Condition(HashMap<String,String> whereCache){
            Iterator<String> keyIterator = cacheMap.keySet().iterator();
            List<String> whereValues = new ArrayList<>();
            StringBuilder builder = new StringBuilder(" 1=1 ");
            while (keyIterator.hasNext()){
                String key = keyIterator.next();

                String whereValue = whereCache.get(key);
                if (whereValue != null){
                    builder.append("and "+key+" =? ");
                    whereValues.add(whereValue);
                }
            }
            whereCause = builder.toString();
            whereCauseArry = whereValues.toArray(new String[whereValues.size()]);
        }

        public String getWhereCause() {
            return whereCause;
        }

        public void setWhereCause(String whereCause) {
            this.whereCause = whereCause;
        }

        public String[] getWhereCauseArry() {
            return whereCauseArry;
        }

        public void setWhereCauseArry(String[] whereCauseArry) {
            this.whereCauseArry = whereCauseArry;
        }
    }
}

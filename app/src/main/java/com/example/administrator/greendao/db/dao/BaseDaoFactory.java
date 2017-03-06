package com.example.administrator.greendao.db.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by ShuWen on 2017/2/9.
 */

public class BaseDaoFactory {
    private static BaseDaoFactory instance;
    private String dbPath;
    private SQLiteDatabase database;
    private SQLiteDatabase personalDatabase;
    private HashMap<String,BaseDao> daoHashMap = new HashMap<>();

    public static BaseDaoFactory getInstance(){
        if (instance == null){
            synchronized (BaseDaoFactory.class){
                instance = new BaseDaoFactory();
            }
        }
        return instance;
    }

    public BaseDaoFactory(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"update");
        if (!file.exists()){
            file.mkdirs();
        }
        dbPath = file.getAbsolutePath() +File.separator+"user.db";
        openSqliteDataBase();
    }

    public synchronized <T extends BaseDao<M>,M> T getDataHelper(Class<T>daoClazz, Class<M> entityClazz){
        BaseDao baseDao = null;
        if (daoHashMap.get(daoClazz.getSimpleName()) != null){
            return (T) daoHashMap.get(daoClazz.getSimpleName());
        }
        try {
            baseDao = daoClazz.newInstance();
            baseDao.init(entityClazz,database);
            daoHashMap.put(daoClazz.getSimpleName(),baseDao);
        } catch (Exception e){

        }
        return (T) baseDao;
    }

    public synchronized <T extends BaseDao<M>,M> T getUserHelper(Class<T> daoClazz,Class<M> entityClazz){

        personalDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBaseEnums.database.getValue(),null);

        BaseDao baseDao = null;
        if (daoHashMap.get(daoClazz.getSimpleName()) != null){
            return (T) daoHashMap.get(daoClazz.getSimpleName());
        }
        try {
            baseDao = daoClazz.newInstance();
            baseDao.init(entityClazz,personalDatabase);
            daoHashMap.put(daoClazz.getSimpleName(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    private void openSqliteDataBase() {
        database = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
    }

}

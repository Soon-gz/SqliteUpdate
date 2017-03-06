package com.example.administrator.greendao.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.greendao.dataModel.User;
import com.example.administrator.greendao.dataModel.UserDao;
import com.example.administrator.greendao.db.dao.BaseDaoFactory;
import com.example.administrator.greendao.util.FileUtils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class UpdateManager {

    private static final String TAG = UpdateManager.class.getName();

    private List<User> userList;

    private final String FILE_DIV = "/";

    private File parentFile = new File(Environment.getExternalStorageDirectory(),"update");

    private File bakFile = new File(parentFile,"bakDb");

    private String oldVersion;

    private String newVersion;

    public UpdateManager(){
        sureExistsFile(parentFile);
        sureExistsFile(bakFile);
    }

    private void initUserList() {
        UserDao userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
        userList = userDao.query(new User());
    }

    private void sureExistsFile(File parentFile) {
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }
    }

    public void checkVersionTables(Context context){

        initUserList();

        String thisVersion = getVersionName(context);

        UpdateXml updateXml = readXml(context);

        CreateVersion  createVersion = analyseCreateVersion(updateXml,thisVersion);

        executeCreateVersion(createVersion);
    }

    private UpdateXml readXml(Context context) {
        UpdateXml updateXml = null;
        InputStream stream = null;
        Document document = null;
        try {
            stream = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }finally {
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateXml = new UpdateXml(document);
        return updateXml;
    }

    private void executeCreateVersion(CreateVersion createVersion) {
        List<CreateDb> createDbs = createVersion.getCreateDbs();
        if (createDbs == null){
            Log.i(TAG,"createDbs is null");
            return;
        }

        SQLiteDatabase database = null;
        for (CreateDb db:createDbs) {
            if ("user".equals(db.getName())){
                database = getDb(db.getName(),"",true);
                executeSql(database,db.getSqlCreates());
                database.close();
                continue;
            }

            for (User user: userList) {
                database = getDb(db.getName(),user.getUser_id(),false);
                executeSql(database,db.getSqlCreates());
                database.close();
            }

        }


    }

    private void executeSql(SQLiteDatabase database, List<String> sqlCreates) {

        database.beginTransaction();
        for (String sql:sqlCreates) {
            if (!"".equals(sql)){
                database.execSQL(sql);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private SQLiteDatabase getDb(String name, String s, boolean b) {
        SQLiteDatabase database = null;
        if (b){
            database = SQLiteDatabase.openOrCreateDatabase(parentFile.getAbsolutePath() + File.separator + name+".db",null);
        }else {
            File file = new File(parentFile,s);
            sureExistsFile(file);
            database = SQLiteDatabase.openOrCreateDatabase(file.getAbsolutePath() + File.separator + name + ".db",null);
        }
        return database;
    }

    private CreateVersion analyseCreateVersion(UpdateXml updateXml, String thisVersion) {
        CreateVersion createVersion = null;
        List<CreateVersion> createVersions = updateXml.getCreateVersions();
        if (createVersions == null){
            Log.i(TAG,"createversions is null");
            return null;
        }
        for (CreateVersion version:createVersions) {
            if (version.getVersion().equals(thisVersion)){
                createVersion = version;
            }
        }
        return createVersion;
    }


    private String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public boolean saveThisVersion(String serverVersion){
        boolean result = false;
        File file = new File(parentFile,"update.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write("V002" + FILE_DIV + serverVersion);
            writer.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (writer !=  null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public void startUpdateSqlite(Context context){
        initUserList();
        if (getLocationVersion(context)){
            for (User user:userList) {
                String userDbpath = parentFile.getAbsolutePath() + File.separator + user.getUser_id() + File.separator + "logic.db";
                String bak_userDbpath = bakFile.getAbsolutePath() + File.separator + user.getUser_id() + File.separator + "logic.db";
                FileUtils.copySingleFile(userDbpath,bak_userDbpath);
            }

            String userAllDbPath = parentFile.getAbsolutePath() + File.separator + "user.db";
            String bak_userAllDbPath = bakFile.getAbsolutePath() + File.separator + "user.db";
            FileUtils.copySingleFile(userAllDbPath,bak_userAllDbPath);

            UpdateXml updateXml = readXml(context);

            CreateVersion createVersion = analyseCreateVersion(updateXml,newVersion);

            UpdateStep updateStep = analyseUpdateStep(updateXml,newVersion);

            executeUpdateDb(updateStep,createVersion);

        }


    }

    private void executeUpdateDb(UpdateStep updateStep, CreateVersion createVersion) {
        List<UpdateDb> updateDbs = updateStep.getUpdateDbs();

        SQLiteDatabase database = null;

        for (UpdateDb updateDb:updateDbs) {

            List<String> sqlBefores = updateDb.getSqlBefores();

            List<String> sqlAfters = updateDb.getSqlAfters();

            if ("user".equals(updateDb.getName())){
                database = getDb(updateDb.getName(),"",true);
                executeBeforeAndAfters(database,sqlBefores,sqlAfters,createVersion);
                database.close();
                continue;
            }

            for (User user: userList) {
                database = getDb(updateDb.getName(),user.getUser_id(),false);
                executeBeforeAndAfters(database,sqlBefores,sqlAfters,createVersion);
                database.close();
            }
        }
    }

    private void executeBeforeAndAfters(SQLiteDatabase database, List<String> sqlBefores, List<String> sqlAfters, CreateVersion createVersion) {
        executeSql(database,sqlBefores);
        executeCreateVersion(createVersion);
        executeSql(database,sqlAfters);
    }

    private UpdateStep analyseUpdateStep(UpdateXml updateXml, String thisVersion) {
        List<UpdateStep> updateSteps = updateXml.getUpdateSteps();

        UpdateStep stepResult = null;

        for (UpdateStep step:updateSteps) {
            String[]versionFroms = step.getVersionfroms();
            for (String version:versionFroms) {
                if (version.equals(oldVersion) && step.getVersionTo().equals(thisVersion)){
                    stepResult = step;
                    break;
                }
            }
            if (stepResult != null){
                break;
            }
        }
        return stepResult;
    }

    private boolean getLocationVersion(Context context) {
        InputStream stream =  null;
        boolean result = false;
        File file = new File(parentFile,"update.txt");
        byte[]readBytes = new byte[100];
        int readNum = -1;
        StringBuilder builder = new StringBuilder();
        try {
            stream = new FileInputStream(file);
            while ((readNum = stream.read(readBytes)) != -1){
                builder.append(new String(readBytes,0,readNum));
            }
            String[]versions = builder.toString().split(FILE_DIV);
            if (versions.length == 2){
                oldVersion = versions[0];
                if (getVersionName(context).equals(versions[1])){
                    newVersion = versions[1];
                }else {
                    newVersion = getVersionName(context);
                }

                if (!oldVersion.equals(newVersion)){
                    result = true;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

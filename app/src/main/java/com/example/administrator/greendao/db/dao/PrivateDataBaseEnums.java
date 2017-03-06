package com.example.administrator.greendao.db.dao;

import android.os.Environment;

import com.example.administrator.greendao.dataModel.User;
import com.example.administrator.greendao.dataModel.UserDao;

import java.io.File;

/**
 * Created by ShuWen on 2017/2/12.
 */

public enum  PrivateDataBaseEnums {

    database("local/data/database/");

    private String value;

    PrivateDataBaseEnums(String s) {
        value = s;
    }

    public String getValue(){

        UserDao userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);

        User currentUser = userDao.getCurrentUser();

        if (currentUser != null){

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"update");
            if (!file.exists()){
                file.mkdirs();
            }

            File file1 = new File(file.getAbsolutePath(),currentUser.getUser_id());
            if (!file1.exists()){
                file1.mkdirs();
            }
            value = file.getAbsolutePath() + File.separator + currentUser.getUser_id() + File.separator + "logic.db";

        }

        return value;
    }
}

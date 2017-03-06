package com.example.administrator.greendao.dataModel;

import android.util.Log;

import com.example.administrator.greendao.dataModel.User;
import com.example.administrator.greendao.db.dao.BaseDao;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by ShuWen on 2017/2/12.
 */

public class UserDao extends BaseDao<User> {


    @Override
    public Long insert(User entity) {
        List<User> list=query(new User());
        User where = null;
        for (User user:list)
        {
            where =new User();
            where.setUser_id(user.getUser_id());
            user.setStatus(0);
            Log.i(TAG,"用户"+user.getName()+"更改为未登录状态");
            update(user,where);
        }
        Log.i(TAG,"用户"+entity.getName()+"登录");
        entity.setStatus(1);
        return super.insert(entity);
    }

    /**
     * 得到当前登录的User
     * @return
     */
    public User getCurrentUser() {
        User user=new User();
        user.setStatus(1);
        List<User> list=query(user);
        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;
    }
}

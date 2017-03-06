package com.example.administrator.greendao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.administrator.greendao.db.dao.BaseDaoFactory;
import com.example.administrator.greendao.dataModel.FileDao;
import com.example.administrator.greendao.db.dao.IBaseDao;
import com.example.administrator.greendao.dataModel.UserDao;
import com.example.administrator.greendao.dataModel.FileModel;
import com.example.administrator.greendao.dataModel.User;
import com.example.administrator.greendao.update.UpdateManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IBaseDao<User> iBaseDao;
    private int noId = 0;
    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iBaseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
        updateManager = new UpdateManager();
    }

    public void btnClick(View view){
        switch (view.getId()){
            case R.id.login:
                User user = new User();
                user.setName("舒文"+noId);
                user.setAddress("台州");
                user.setUser_id("N00"+noId);
                user.setPsw("123456"+noId);
                user.setStatus(0);
                noId = ++noId;
                iBaseDao.insert(user);
                updateManager.checkVersionTables(this);
                break;
            case R.id.add:
//                User user = new User();
//                user.setName("舒文"+noId);
//                user.setAddress("台州");
//                user.setUser_id("N00"+noId);
//                user.setPsw("123456"+noId);
//                noId = ++noId;
//                iBaseDao.insert(user);
                break;
            case R.id.query:
                User userWhere = new User();
                userWhere.setName("舒文");
                List<User>list = iBaseDao.query(userWhere);
                for (User user1:list) {
                    Log.i("TAG","查询的数据:"+user1.toString());
                }
                break;
            case R.id.delete:
                User userdelete = new User();
                userdelete.setName("苏文");
                iBaseDao.delete(userdelete);
                break;
            case R.id.update:
                User userupdate = new User();
                userupdate.setName("苏文");
                userupdate.setAddress("北京");
                userupdate.setPsw("123456789");
                userupdate.setStatus(0);
                userupdate.setUser_id("N001");
                User userWhereupdate = new User();
                userWhereupdate.setName("舒文");
                iBaseDao.update(userupdate,userWhereupdate);
                break;
            case R.id.insertfile:
                FileModel fileModel = new FileModel();
                fileModel.setFilePath("a/b/c");
                fileModel.setFileName("文件下载");
                fileModel.setFileId(1);

                IBaseDao<FileModel> fileDao = BaseDaoFactory.getInstance().getUserHelper(FileDao.class,FileModel.class);
                fileDao.insert(fileModel);

                break;
            case R.id.saveversion:
                String serverVersion = "V003";
                updateManager.saveThisVersion(serverVersion);
                break;
            case R.id.updateversion:
                updateManager.startUpdateSqlite(this);
                break;
        }
    }
}

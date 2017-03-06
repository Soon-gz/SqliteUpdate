package com.example.administrator.greendao.dataModel;

import com.example.administrator.greendao.db.annotation.TbClass;
import com.example.administrator.greendao.db.annotation.TbField;

/**
 * Created by ShuWen on 2017/2/9.
 */

@TbClass(value = "tb_user")
public class User {
    @TbField(value = "user_name")
    private String name;
    @TbField(value = "user_address")
    private String address;
    @TbField(value = "user_psw")
    private String psw;
    @TbField(value = "user_status")
    private Integer status;
    @TbField(value = "user_id")
    private String user_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", status=" + status +
                ", psw='" + psw + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

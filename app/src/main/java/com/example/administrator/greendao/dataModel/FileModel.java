package com.example.administrator.greendao.dataModel;

import com.example.administrator.greendao.db.annotation.TbClass;
import com.example.administrator.greendao.db.annotation.TbField;

/**
 * Created by ShuWen on 2017/2/9.
 */
@TbClass(value = "tb_file")
public class FileModel {
    @TbField(value = "tb_filename")
    private String fileName;
    @TbField(value = "tb_filepath")
    private String filePath;
    @TbField(value = "tb_fileid")
    private int fileId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}

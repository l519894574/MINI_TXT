package common_data;

import java.io.Serializable;

//小说类
public class Novel implements Serializable {
    private String name;
    private String author;
    private String desc;
    transient private String filename;
    private Classification cls;
    transient private String con;//预览文字

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public Classification getCls() {
        return cls;
    }

    public void setCls(Classification cls) {
        this.cls = cls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

package common_data;

import java.io.Serializable;

//数据传输（通过流实现 来回 所以用序列化）
public class DataTansfer<T extends Serializable> implements Serializable {
    private String key;     //关键字
    private T data;         //定义了数据类型（用泛型转换）有可能是用户、小说 很多种的信息
    private int result;     //结果（数字代替0、1）
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}

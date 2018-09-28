package Comm;

import common_data.DataTansfer;

import java.io.Serializable;

//约束客户端（要指定具体的泛型）
public interface Common<T extends Serializable,S extends Serializable> {
    //初始化，将主机地址和端口接受
    void init(String host,int port) throws Exception;
    
    //数据的传输（发送/接受）
    DataTansfer<S> commun(DataTansfer<T> input) throws Exception;
    
    //释放资源
    void destory()throws Exception;
}

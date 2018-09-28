package ServerMain;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

//服务器的功能（多线程）
public interface Service<T extends Serializable > extends Runnable{
    //初始化方法
    void init(Socket socket, ObjectInputStream OI, ObjectOutputStream OO,T data);
    //关闭
    void desorty() throws Exception;
}

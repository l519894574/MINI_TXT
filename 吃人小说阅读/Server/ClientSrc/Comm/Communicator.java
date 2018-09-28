package Comm;

import common_data.DataTansfer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

//实现通信接口
public class Communicator<T extends Serializable,S extends Serializable> implements Common<T,S>{
    private Socket socket;
    private ObjectInputStream OI;
    private ObjectOutputStream OO;
    @Override
    public void init(String host, int port) throws Exception {
        socket=new Socket(host,port);
        //（先出后入）与服务器相反
        OO=new ObjectOutputStream(socket.getOutputStream());
        OI= new ObjectInputStream(socket.getInputStream());
    }
    
    //通信
    @Override
    public DataTansfer<S> commun(DataTansfer<T> input) throws Exception {
        //客户端传给服务器的obj
        OO.writeObject(input);
        
        //返回客户端结果
        return (DataTansfer<S>) OI.readObject(); 
    }
    //关闭
    @Override
    public void destory() throws Exception {
        OI.close();
        OO.close();
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getOI() {
        return OI;
    }

    public void setOI(ObjectInputStream OI) {
        this.OI = OI;
    }

    public ObjectOutputStream getOO() {
        return OO;
    }

    public void setOO(ObjectOutputStream OO) {
        this.OO = OO;
    }
}

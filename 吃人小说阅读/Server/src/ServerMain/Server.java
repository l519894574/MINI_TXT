package ServerMain;

import common_data.DataTansfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        new Server().startserver();
    }
    public <E extends Serializable> void startserver(){
        try {
            ServerSocket server=new ServerSocket(Integer.valueOf(Init.getProps("socket.server.port")));
            System.out.println("服务器已启动！");
            while (true) {
                //连接客户端
                Socket socket = server.accept();
                System.out.println("客户端：" + socket.getInetAddress() + "正在访问！");

                //（先入后出）
                //客户端发来的obj
                ObjectInputStream OI = new ObjectInputStream(socket.getInputStream());
                //给客户端传送obj
                ObjectOutputStream OO = new ObjectOutputStream(socket.getOutputStream());

                //送流中获取dataTansfer的实例，从而判断客户端要干什么（但无法具体指定）
                DataTansfer<E> dataTansfer = (DataTansfer<E>) OI.readObject();
                System.out.println("请求：" + dataTansfer.getKey());//拿到关键字 

                //获取客户端要访问的功能
                Service<E> service =  ServerFactory.getServices(dataTansfer.getKey());;
                service.init(socket,OI,OO,dataTansfer.getData());
                new Thread(service).start();//开启线程
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

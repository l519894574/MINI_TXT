package Client;

import Common_constants.Constants;

import java.io.Serializable;

//客户端主入口
public class ClientMain {
    public static void main(String[] args) throws Exception{
        new ClientMain().StartClient();
        
    }
    
    public void StartClient() throws Exception{
        Service<? extends Serializable> service=ServerFactory.getServices(Constants.START);
        while (true) {
            //无操作要退出循环
            if (service==null){
                System.out.println("感谢您的使用！");
                break;
            }
            //拿到用户的下一个功能
            service = service.execute();
        }
    }
}

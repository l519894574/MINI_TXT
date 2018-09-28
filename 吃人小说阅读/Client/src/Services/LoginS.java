package Services;

import Client.BaseServices;
import Client.Init;
import Client.ServerFactory;
import Client.Service;
import Comm.Communicator;
import Common_constants.Constants;
import Common_constants.User_constants;
import common_data.DataTansfer;
import common_data.User;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;
//客户端登陆类
public class LoginS extends BaseServices<Serializable> {
    private String OUTPUT_TEXT_USERNAMEE = "请输入登录名：";
    private String OUTPUT_TEXT_PASSWORD = "请输入密码：";
    private String OUTPUT_TEXT_SUCCESS = "登录成功！";
    private String OUTPUT_TEXT_FAILED = "用户名或密码错误，请重新输入！";

    @Override
    public Service<? extends Serializable> execute() throws Exception{
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(OUTPUT_TEXT_USERNAMEE);
            String username = scanner.nextLine().trim();
            System.out.print(OUTPUT_TEXT_PASSWORD);
            String password = scanner.nextLine().trim();

            if (username.length() == 0 || password.length() == 0) {
                System.out.println(INVALIDINPUT);
                continue;
            }

            User user = new User();
            user.setPassword(password);
            user.setUsername(username);

            DataTansfer<User> dto = new DataTansfer<>();
            dto.setData(user);
            dto.setKey(Constants.LOGIN);
            
            
            
            //接受了服务器了信息
            Communicator<User, ?> comm = new Communicator<>();
            DataTansfer<?> response = null;
            try {
                comm.init(Init.getProps("socket.server.ip"), Integer
                        .parseInt(Init.getProps("socket.server.port")));
                response = comm.commun(dto);
            } catch (NumberFormatException | UnknownHostException
                    | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(ERROR);
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(SERVERERROR);
                try {
                    return ServerFactory.getServices(Constants.START);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    comm.destory();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (User_constants.SUCCESS == response.getResult()) {
                System.out.println(OUTPUT_TEXT_SUCCESS);
                //下一步
                return ServerFactory.getServices(Constants.GETCLASSES);
            } else if (User_constants.PASSWOED_ERROE == response.getResult()
                    || User_constants.USERNAME_NO== response.getResult()) {
                System.out.println(OUTPUT_TEXT_FAILED);
                continue;
            } else {
                System.out.println(SERVERERROR);
                continue;
            }
        }
    }

}
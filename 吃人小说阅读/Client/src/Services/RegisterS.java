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
//服务器注册功能
public class RegisterS extends BaseServices<Serializable> {
    private String USERNAME = "请输入登录名:";
    private String PASSWORD = "请输入密码:";
    private String PASSWORD2 = "请再次输入密码:";
    private String USEREXIST = "用户名已存在，请重新注册！";
    private String PASSWORDNOTEQUAL = "两次密码不一样！";
    private String SAVESUCESS = "用户注册成功！";
    private String SAVEFAIL = "用户注册失败，请重新注册！";


    @Override
    public Service<? extends Serializable> execute() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(USERNAME);
            String username = scanner.nextLine().trim();
            System.out.print(PASSWORD);
            String password = scanner.nextLine().trim();
            System.out.print(PASSWORD2);
            String repwd = scanner.nextLine().trim();

            if (username.length() == 0 || password.length() == 0) {
                System.out.println(INVALIDINPUT);
                continue;
            }
            if (!password.equals(repwd)) {
                System.out.println(PASSWORDNOTEQUAL);
                continue;
            }

            User user = new User();
            user.setPassword(password);
            user.setUsername(username);

            DataTansfer<User> dto = new DataTansfer<>();
            dto.setData(user);
            dto.setKey(Constants.REGISTER);

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
                return ServerFactory.getServices(Constants.START);
            } finally {
                try {
                    comm.destory();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (User_constants.SUCCESS == response.getResult()) {
                System.out.println(SAVESUCESS);
                return ServerFactory.getServices(Constants.START);
            } else if (User_constants.USERNAME_YES == response.getResult()) {
                System.out.println(USEREXIST);
                continue;
            } else {
                System.out.println(SAVEFAIL);
                return ServerFactory.getServices(Constants.START);
            }
        }
    }

}

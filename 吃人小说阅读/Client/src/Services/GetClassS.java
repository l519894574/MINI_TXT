package Services;

import Client.BaseServices;
import Client.Init;
import Client.ServerFactory;
import Client.Service;
import Comm.Communicator;
import Common_constants.Constants;
import common_data.Classification;
import common_data.DataTansfer;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;
//客户端小说类
public class GetClassS extends BaseServices<Serializable> {
    private String LINE = "-------------------------------------\n";
    private String SELECT = "请选择：";
    private String RETURN = "0.返回上一级菜单";
    private StringBuilder MENU_BEGIN = new StringBuilder(LINE).append(RETURN);
    private StringBuilder MENU_END = new StringBuilder(SELECT);

    @Override
    public Service<? extends Serializable> execute() throws Exception{
        DataTansfer<Serializable> dt = new DataTansfer<>();
        dt.setKey(Constants.GETCLASSES);

        Communicator<Serializable, Classification[]> comm = new Communicator<>();
        DataTansfer<Classification[]> response = null;
        try {
            comm.init(Init.getProps("socket.server.ip"),
                    Integer.parseInt(Init.getProps("socket.server.port")));
            response = comm.commun(dt);
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
        Classification[] cls = response.getData();
        System.out.println(MENU_BEGIN);
        int i = 0;
        System.out.println("-------------");
        for (Classification c : cls) {
            System.out.println("|\t"+(++i) + "." + c.getClassname()+"\t|");
            System.out.println("-------------");
        }
        System.out.print(MENU_END);
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print(INVALIDINPUT);
                continue;
            }
            if (choice < 0 || choice > cls.length) {
                System.out.print(INVALIDINPUT);
                continue;
            }
            break;
        }
        if (choice == 0) {
            return ServerFactory.getServices(Constants.START);
        } else {
            Service<Classification> next = null;
            next = ServerFactory.getServices(Constants.GETNOVELS);
            next.setInputData(cls[choice - 1]);
            return next;
        }
    }

}


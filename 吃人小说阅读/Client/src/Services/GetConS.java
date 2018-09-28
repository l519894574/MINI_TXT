package Services;

import Client.BaseServices;
import Client.Init;
import Client.ServerFactory;
import Client.Service;
import Comm.Communicator;
import Common_constants.Constants;
import common_data.Classification;
import common_data.DataTansfer;
import common_data.Novel;
import Common_constants.Novel_constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GetConS extends BaseServices<Novel> {
    private String ENDLINE = "\n......，省略内容请下载后阅读\n";
    private String SELECTLIST = "继续显示列表请输入1，下载TXT请输入2：";
    private String FILENOTFOUND = "文件未找到，可能已被删除！";
    private String FILECANNOTREAD = "文件读取错误，预览失败！";
    
    private StringBuilder MENU_END = new StringBuilder(ENDLINE).append(SELECTLIST);
    @Override
    public Service<? extends Serializable> execute() throws Exception {
        
        DataTansfer<Novel> dt=new DataTansfer<>();
        dt.setKey(Constants.GETCON);
        dt.setData(getInputData());

        Communicator<Novel,String> comm=new Communicator<>();
        DataTansfer<String> response = null;
        try {
            comm.init(Init.getProps("socket.server.ip"), Integer
                    .parseInt(Init.getProps("socket.server.port")));
            response = comm.commun(dt);
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
        
        if (response.getData()==null){
            //返回是空，检查状态码
            switch (response.getResult()){
                case Novel_constants.FILE_NOTFIND:
                    System.out.println(FILENOTFOUND);
                    break;
                case Novel_constants.FILE_CONTREAD:
                    System.out.println(FILECANNOTREAD);
                    break;
            }
            System.out.println(SELECTLIST);
        }else {
            //展示
            System.out.println("正在预览*****"+this.getInputData().getName()+"*****小说");
            System.out.println(response.getData());
            System.out.print(MENU_END);
        }
        Scanner input =new Scanner(System.in);
        while (true) {
            String choose = input.next().trim();
            switch (choose) {
                case "1":
                    //显示小说列表
                    //先传用户的当前小说种类
                    Service<Classification> getNovels=ServerFactory.getServices(Constants.GETNOVELS);
                    getNovels.setInputData(this.getInputData().getCls());
                    return getNovels;
                case "2":
                    //下载功能
                    Service<Novel> download=ServerFactory.getServices(Constants.DOWNLOAD);
                    download.setInputData(this.getInputData());
                    return download;
                default:
                    //重新输入
                    System.out.print(INVALIDINPUT);
                    break;
            }
        }
        
    }
}

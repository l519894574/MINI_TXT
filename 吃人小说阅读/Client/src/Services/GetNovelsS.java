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

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;

//客户端小说列表
public class GetNovelsS extends BaseServices<Classification> {
    private String READDOWN = "阅读和下载请选择文件序号，上传TXT请输入-1，返回请输入0：";
    private String HEAD = "序号\t\t名称\t\t作者\t\t简介";
    private String LINE="-------------------------------------\n";
    private String RETURN="0.返回上一级菜单\n";
    private String READ="1.在线阅读\n";
    private String DOWNLOAD="2.下载本小说TXT\n";
    private String SELECT="请选择： ";
    private StringBuffer MENU_FUNCTION = new StringBuffer(LINE)
            .append(RETURN)
            .append(READ)
            .append(DOWNLOAD)
            .append(LINE)
            .append(SELECT);

    @Override
    public Service<? extends Serializable> execute() throws Exception {
        System.out.println("**********"+this.getInputData().getClassname()+"类小说列表**********");
        System.out.println(HEAD);
        DataTansfer<Classification> dt=new DataTansfer<>();
        dt.setKey(Constants.GETNOVELS);
        dt.setData(this.getInputData());
        
        //发送的--cif--接受的---novel[]
        Communicator<Classification, Novel[]> comm=new Communicator<>();
        DataTansfer<Novel[]> response = null;
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
        //列表信息
        int i=0;
        for (Novel n:response.getData()){
            System.out.println((++i)+"\t\t"+n.getName()+"\t\t"+n.getAuthor()+"\t\t"+n.getDesc());
        }
        System.out.print(READDOWN);
        Scanner input =new Scanner(System.in);
        int choose=-2;
        while (true){
            try {
                choose =Integer.parseInt(input.next().trim());
            }catch (NumberFormatException e){
                System.out.println(INVALIDINPUT);
                continue;
            }
            //大于返回值的数组长度
            if (choose<-1||choose>response.getData().length){
                System.out.println(INVALIDINPUT);
                continue;
            }
            break;
        }
        if (choose==-1){
            //上传功能
            Service<Classification> upload=null;
            upload=ServerFactory.getServices(Constants.UPLOAD);
            upload.setInputData(this.getInputData());
            return upload;
        }else if(choose==0){
            //重选小说列表
            return ServerFactory.getServices(Constants.GETCLASSES);
        }else {
            //说明用户确认的小说（去获取）
            Novel novel=response.getData()[choose-1];
            
            //对具体的小说进行操作
            System.out.print(MENU_FUNCTION);
            while (true){
                try {
                    choose =Integer.parseInt(input.next().trim());
                }catch (NumberFormatException e){
                    System.out.println(INVALIDINPUT);
                    continue;
                }
                //输入0，1，2
                if (choose<0||choose>2){
                    System.out.println(INVALIDINPUT);
                    continue;
                }
                break;
            }
            switch (choose){
                case 0:
                    //重选本类小说列表，传入用户上次选择的分类方式
                    Service<Classification> getNoels=null;
                    getNoels=ServerFactory.getServices(Constants.GETNOVELS);
                    //传入自己（只是为了更新值）
                    getNoels.setInputData(this.getInputData());
                    return getNoels;
                case 1:
                    //查看txt的前几行，传入用户传入的哪个小说，上面的novel
                    Service<Novel> getCon=null;
                    getCon=ServerFactory.getServices(Constants.GETCON);
                    getCon.setInputData(novel);
                    return getCon;
                case 2:
                    //调用下载功能，同时也要传入novel
                    Service<Novel> download=null;
                    download=ServerFactory.getServices(Constants.DOWNLOAD);
                    download.setInputData(novel);
                    return download;
            }
        }
        return null;
    }
}

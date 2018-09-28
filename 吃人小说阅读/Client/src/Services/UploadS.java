package Services;

import Client.BaseServices;
import Client.Init;
import Client.ServerFactory;
import Client.Service;
import Comm.Communicator;
import Common_constants.Constants;
import Common_constants.Novel_constants;
import common_data.Classification;
import common_data.DataTansfer;
import common_data.Novel;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Scanner;

//客户端上传功能
public class UploadS extends BaseServices<Classification> {
    private String NAME = "请输入小说名：";
    private String AUTHOR = "请输入作者：";
    private String DESC = "请输入简介：";
    private String PATH = "请输入上传的txt(请注意路径用/或者\\\\)：";
    private String REUPLOAD = "继续上传请输入1\t返回请输入0：";
    private String FILEEXSITS = "文件已存在，上传终止！ ";
    private String ASTERISK = "**********************************************";
    private String FILENOTFOUND = "文件未找到，请确认后重试！";
    private String UPLOADFAIL = "文件上传失败！";
    @Override
    public Service<? extends Serializable> execute() throws Exception {
        DataTansfer<Novel> dt=new DataTansfer<>();
        dt.setKey(Constants.UPLOAD);
        Novel novel=new Novel();
        //告诉名字
        dt.setData(novel);
        //告诉服务器类名
        novel.setCls(this.getInputData());

        Scanner input =new Scanner(System.in);
        System.out.print(NAME);
        String name=input.next().trim();
        novel.setName(name);

        System.out.print(AUTHOR);
        String author=input.next().trim();
        novel.setAuthor(author);

        System.out.print(DESC);
        String desc=input.next().trim();
        novel.setDesc(desc);
        String paths;
        while (true){
            System.out.print(PATH);
             paths=input.next().trim();
            if (!new File(paths).exists()){
                System.out.println(FILENOTFOUND);
                continue;
            }
            break;
        }
        final String path=paths;

        final Communicator<Novel,?> comm=new Communicator<>();
        DataTansfer<?> response = null; 
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
        }
        if (response.getResult()==Novel_constants.SUCCESS){
            //成功
            new Thread(){
                @Override
                public void run() {
                    File file=new File(path);
                    byte[] buffer=new byte[2048];
                    int len =-1;
                    //客户端读取自己要上传的文件
                    InputStream fls=null;
                    try {
                        fls=new FileInputStream(file); 
                        while ((len=fls.read(buffer))!=-1){
                            //往服务器送
                            comm.getOO().write(buffer,0,len);
                        }
                        comm.getOO().flush();
                        //半关闭 
                        comm.getSocket().shutdownOutput();
                        
                    } catch (FileNotFoundException e) {
                        //文件未找到
                        System.err.println(FILENOTFOUND);
                        e.printStackTrace(); 
                    } catch (IOException e) {
                        System.out.println(UPLOADFAIL);
                        e.printStackTrace();
                    }finally {
                        try {
                            if (fls!=null){
                                comm.destory();
                                fls.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.err.println("上传成功！");
                }
            }.start();
        }else {
           //失败
            System.out.println(ASTERISK);
            System.out.println(FILEEXSITS);
            comm.destory();
        }
        //下一步提示操作
        System.out.println(ASTERISK);
        System.out.println(REUPLOAD);
        String choose=null;
        while (true) {
            choose = input.next().trim();
            switch (choose) {
                case "0":
                    Service<Classification> getNovels = ServerFactory.getServices(Constants.GETNOVELS);
                    getNovels.setInputData(this.getInputData());
                    return getNovels;
                case "1":
                    Service<Classification> upload = ServerFactory.getServices(Constants.UPLOAD);
                    upload.setInputData(this.getInputData());
                    return upload;
                default:
                    System.out.println(INVALIDINPUT);
                    break;
            }
        }
    }
}

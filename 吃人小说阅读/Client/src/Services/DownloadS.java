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

import java.io.*;
import java.net.UnknownHostException;
import java.util.Scanner;

//客户端下载小说功能
public class DownloadS extends BaseServices<Novel> {
    private String FILENOTFOUND = "文件未找到，可能已被删除！";
    private String FILECANNOTREAD = "文件无法读取，下载失败！";
    private String DOWNLOADSTART = "文件开始下载！";
    private String DOWNLOADFAIL = "文件下载失败！";
    private String DOWNLOADSUCCESS = "文件下载成功！";
    private String SELECTLIST = "按回车键返回：";
    @Override
    public Service<? extends Serializable> execute() throws Exception {
        DataTansfer<Novel> dt=new DataTansfer<>();
        dt.setKey(Constants.DOWNLOAD);
        dt.setData(this.getInputData());
        
        Communicator<Novel, ?> comm = new Communicator<>();
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
        //不能忙着关闭，否则无法下载
        
        System.out.println("下载****"+this.getInputData().getName()+"****小说");
        //检测能否下载
        boolean flag=true;//  充当检测的功能
        switch (dt.getResult()){
            case Novel_constants.FILE_NOTFIND:
                flag=false;
                System.out.println(FILENOTFOUND);
                break;
            case Novel_constants.FILE_CONTREAD:
                System.out.println(FILECANNOTREAD);
                flag=false;
                break;
                default:
                    System.out.println(DOWNLOADSTART);
                    break;
        }
        //若没有问题
        if (flag){
            //下载（独立的下载线程，不影响别的线程）
            new Thread(){
                @Override
                public void run() {
                    String path=Init.getProps("client.download.path");
                    if (path.endsWith(File.separator)){
                        //补"\\"
                        path+=File.separator;
                    }
                    String filename=getInputData().getName()+".txt";
                    File file=new File("downloads");
                    file.mkdirs();
                    file=new File(path+filename);
                    
                    byte[] buffer=new byte[2048];
                    int len=-1;
                    OutputStream out=null;
                    try {
                        out=new FileOutputStream(file);
                        //循环读，防止死循环
                        while ((len=comm.getOI().read(buffer))!=-1){
                            out.write(buffer,0,len);
                        }
                        out.flush();//缓存
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        //如果失败了，删除垃圾文件
                        file.delete();
                        System.out.println(DOWNLOADFAIL);
                        e.printStackTrace();
                    }finally {
                        //关闭
                        if (out!=null){
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            comm.destory();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.err.println(DOWNLOADSUCCESS);
                    System.err.println("下载后的文件路径是："+path+"\n下载后的文件名是："+getInputData().getName()+".txt");
                }
            }.start();
            
            
        }else {
            //不能上传就关闭资源
            comm.destory();
        }
        System.out.println(SELECTLIST);
        //遇到回车就执行
        new Scanner(System.in).nextLine();
        
        Service<Classification> next=ServerFactory.getServices(Constants.GETNOVELS);
        next.setInputData(this.getInputData().getCls());
        return next;
    }
}

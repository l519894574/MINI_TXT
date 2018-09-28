package Services;

import Common_constants.Novel_constants;
import ServerMain.BaseServices;
import Util.Novel_util;
import common_data.DataTansfer;
import common_data.Novel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Download extends BaseServices<Novel> {
    @Override
    protected void execute() throws IOException {
        DataTansfer<?> dt=new DataTansfer<>();
        //通过工具类获取要下载的文件File
        File file=Novel_util.getNoevlFile(getData());
        boolean flag=true;
        //若文件不存在
        if (!file.exists()){
            flag=false;
            dt.setResult(Novel_constants.FILE_NOTFIND);
        }else if (!file.canRead()){
            flag=false;
            dt.setResult(Novel_constants.FILE_NOTFIND);
        }
        getOO().writeObject(dt);
        
        //若可以继续
        if (flag){
            byte[] buffer=new byte[2048];
            int len=-1;
            InputStream fis=null;
            try {
                fis = new FileInputStream(file);
                while ((len = fis.read(buffer)) != -1) {
                    getOO().write(buffer, 0, len);
                }
                getOO().flush();
                getSocket().shutdownOutput();//只关闭了输出，否则客户在堵塞 
            }catch (Exception e){
                //释放资源
                e.printStackTrace();
            }finally {
                //释放资源
                if (fis!=null){
                    fis.close();
                }
                
            }
        }
    }
}

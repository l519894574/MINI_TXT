package Services;

import Common_constants.Novel_constants;
import ServerMain.BaseServices;
import Util.Novel_util;
import common_data.DataTansfer;
import common_data.Novel;

import java.io.FileNotFoundException;
import java.io.IOException;

//服务器小说预览
public class GetCon extends BaseServices<Novel> {
    //找到服务器下的小说文件，截取一部分给客户端
    @Override
    protected void execute() throws IOException{
        DataTansfer<String> dt=new DataTansfer<>();
        //调用预览功能
        try {
            dt.setData(Novel_util.getCon(getData()));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            dt.setResult(Novel_constants.FILE_NOTFIND);
        }catch (IOException e) {
            e.printStackTrace();
            dt.setResult(Novel_constants.FILE_CONTREAD);
        }
        getOO().writeObject(dt);
    }
}

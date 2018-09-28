package Services;

import Common_constants.Novel_constants;
import ServerMain.BaseServices;
import Util.Novel_util;
import common_data.DataTansfer;
import common_data.Novel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//服务器上传
public class Upload extends BaseServices<Novel> {
    @Override
    protected void execute() throws IOException {
        //1.
        //文件名==小说名+“.txt”
        //可能出现重名，所以进行验证
        int result=Novel_util.saveNovel(getData());
        DataTansfer<?> dt=new DataTansfer<>();
        dt.setResult(result);
        getOO().writeObject(dt);
        if (result==Novel_constants.SUCCESS){
            byte[] buffer=new byte[2048];
            int len =-1;
            OutputStream outputStream=null;
            //调用make方法
            File file=Novel_util.makeNovelFile(getData());
            try {
                outputStream = new FileOutputStream(file);
                while ((len = getOI().read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }finally {
                if (outputStream!=null){
                    //关闭
                    outputStream.close();
                }
            }
        }
        //2.
        //不管名字是什么，随机算法，生成不重复的文件名
        //不用验证，直接保存
    }
}

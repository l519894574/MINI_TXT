package Services;

import ServerMain.BaseServices;
import Util.User_util;
import common_data.DataTansfer;
import common_data.User;

//注册功能 
public class Register extends BaseServices<User> {
    @Override
    protected void execute() throws Exception{
        int result =User_util.register(getData());
        DataTansfer<?> dt=new DataTansfer<>();
        dt.setResult(result);
        
        //结果反馈给客户端
        getOO().writeObject(dt);
    }
}

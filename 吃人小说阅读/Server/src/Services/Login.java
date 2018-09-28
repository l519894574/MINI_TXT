package Services;

import ServerMain.BaseServices;
import Util.User_util;
import common_data.DataTansfer;
import common_data.User;

import java.io.IOException;
//登陆
public class Login extends BaseServices<User> {
    @Override
    public void execute() throws IOException {
        int result = User_util.Login(getData());
        // 登录成功
        // 1. 查询小说分类，作为返回数据发送回客户端
        // 2. 返回之后，客户端重新发送请求，获取小说分类
        DataTansfer<?> dto = new DataTansfer<>();
        dto.setResult(result);
        getOO().writeObject(dto);
    }
}


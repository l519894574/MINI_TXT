package Services;

import ServerMain.BaseServices;
import Util.Novel_util;
import common_data.Classification;
import common_data.DataTansfer;
import common_data.Novel;
//服务器小说列表
public class GetNovels extends BaseServices<Classification> {
    @Override
    protected void execute() throws Exception {
        DataTansfer<Novel[]> dt=new DataTansfer<>();
        //getData()调用自己
        dt.setData(Novel_util.getNovels(this.getData()));
        getOO().writeObject(dt);
    }
}

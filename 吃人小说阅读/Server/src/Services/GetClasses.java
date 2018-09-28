package Services;

import ServerMain.BaseServices;
import Util.Novel_util;
import common_data.Classification;
import common_data.DataTansfer;

import java.io.Serializable;

public class GetClasses extends BaseServices<Serializable> {
    @Override
    protected void execute() throws Exception {
        DataTansfer<Classification[]> dt=new DataTansfer<>();
        dt.setData(Novel_util.getClassname());
        
        getOO().writeObject(dt);
    }
}

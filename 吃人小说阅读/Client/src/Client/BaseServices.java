package Client;
import java.io.Serializable;

//相同的功能基础（对将来的需求没办法确定，所以抽象类）
public abstract class BaseServices<T extends Serializable> implements Service<T> {
    protected String SERVERERROR = "服务器故障，请重试！";
    protected String ERROR = "系统存在错误，服务终止！";
    protected String INVALIDINPUT = "你的输入有误，请重新输入：";
    private T inputData;

    @Override
    public void setInputData(T inputData) {
        this.inputData = inputData;
    }
    
    public T getInputData() {
        return inputData;
    }
}

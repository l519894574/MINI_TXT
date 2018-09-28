package ServerMain;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
//相同的功能基础（对将来的需求没办法确定，所以抽象类）
public abstract class BaseServices<T extends Serializable> implements ServerMain.Service<T> {
    private Socket socket;
    private ObjectOutputStream OO;
    private ObjectInputStream OI;
    private T data;
    @Override
    public void init(Socket socket, ObjectInputStream OI, ObjectOutputStream OO, T data) {
        this.socket=socket;
        this.OI=OI;
        this.OO=OO;
        this.data=data;
    }

    @Override
    public void desorty() throws Exception {
        OI.close();
        OO.close();
        socket.close();
    }
    //功能相关的个性代码
    abstract protected void execute() throws Exception;
    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                desorty();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOO() {
        return OO;
    }

    public void setOO(ObjectOutputStream OO) {
        this.OO = OO;
    }

    public ObjectInputStream getOI() {
        return OI;
    }

    public void setOI(ObjectInputStream OI) {
        this.OI = OI;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

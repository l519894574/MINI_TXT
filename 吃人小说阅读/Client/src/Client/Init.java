package Client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//配置类 与配置文件做交互
public class Init {
    //Map的分支<>
    private static Properties props=new Properties();
    //静态块值执行一次
    static {
        InputStream inputStream=null;
        try {
            inputStream=new FileInputStream("Client/config/client.properties");
            props.load(inputStream);//固有方法，与文件中的键值对匹配
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //返回key的值给服务器
    public static String getProps(String key){
        return props.getProperty(key);
    }
}

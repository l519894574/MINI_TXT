package ServerMain;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//服务器端器工厂类
public class ServerFactory {
    private static final String path = Init.getProps("server.config.service");
    private static Map<String, String> services = new HashMap<>();
    private Document document;
    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document document=null;
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(new FileInputStream(path));
            NodeList serviceNodes = document.getElementsByTagName("service");
            for (int i = 0; i < serviceNodes.getLength(); ++i) {
                Node node = serviceNodes.item(i);
                services.put(node.getAttributes().getNamedItem("key")
                        .getNodeValue().trim(), node.getFirstChild()
                        .getNodeValue().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> Service<T> getServices(String key) {
        String classname = services.get(key);
        if (classname == null)
            throw new RuntimeException("暂无此功能！");
        try {
            
            //反射机制  
            return (Service<T>) Class.forName(classname).newInstance();
        } catch (InstantiationException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

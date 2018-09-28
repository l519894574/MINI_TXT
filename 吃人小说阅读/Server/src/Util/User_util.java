package Util;

import Common_constants.User_constants;
import ServerMain.Init;
import common_data.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class User_util {
    private static final String path = Init.getProps("server.config.user");
    private static Map<String, User> users = new HashMap<>();
    private static Document doc = null;
    
    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new FileInputStream(path));
            NodeList usernames = doc.getElementsByTagName("username");
            NodeList passwords = doc.getElementsByTagName("password");
            User user = null;
            for (int i = 0; i < usernames.getLength(); ++i) {
                user = new User();
                user.setUsername(usernames.item(i).getFirstChild().getNodeValue().trim());
                user.setPassword(passwords.item(i).getFirstChild().getNodeValue().trim());
                users.put(user.getUsername(), user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static int Login(User user) {
        User check = null;
        if ((check = users.get(user.getUsername())) != null) {
            if (check.getPassword().equals(user.getPassword())) {
                return User_constants.SUCCESS;
            } else {
                return User_constants.PASSWOED_ERROE;
            }
        }else {
            return User_constants.USERNAME_NO;
        }
    }

    public static boolean exists(String username) {
        return (users.get(username) != null);
    }

    public static synchronized int register(User user) {
        if (exists(user.getUsername())) {
            return User_constants.ERROR;
        }

        Element newUser = doc.createElement("user");
        Element username = doc.createElement("username");
        newUser.appendChild(username);
        Element password = doc.createElement("password");
        newUser.appendChild(password);
        username.appendChild(doc.createTextNode(user.getUsername()));
        password.appendChild(doc.createTextNode(user.getPassword()));
        doc.getDocumentElement().appendChild(newUser);

        OutputStream fos = null;
        try {
            TransformerFactory tff = TransformerFactory.newInstance();
            tff.setAttribute("indent-number", 4);
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            fos = new FileOutputStream(path);
            tf.transform(new DOMSource(doc), new StreamResult(
                    new OutputStreamWriter(fos, "UTF-8")));

            users.put(user.getUsername(), user);
            return User_constants.SUCCESS;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doc.removeChild(newUser);
        users.remove(user.getUsername());
        return User_constants.ERROR;
    }
}

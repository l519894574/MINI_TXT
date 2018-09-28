package Util;

import Common_constants.Novel_constants;
import ServerMain.Init;
import common_data.Classification;
import common_data.Novel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

//小说功能
public class Novel_util {
    private static final String path=Init.getProps("server.config.class");
    //小说分类
    private static Map<String, Classification> classes = new HashMap<>();
    //小说内容（分类存储）Map的嵌套
    private static Map<String, Map<String , Novel>> novels=new HashMap<>();
   
    private static Document doc = null;
    private static String model;
    //加载小说分类信息
    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new FileInputStream(path));
            NodeList classnames = doc.getElementsByTagName("classname");
            NodeList catalogs = doc.getElementsByTagName("catalog");
            NodeList configs = doc.getElementsByTagName("config");
            Classification cls=null;
            //小说类名的集合（类别列表）
            for (int i=0;i<classnames.getLength();i++){
                cls=new Classification();
                cls.setClassname(classnames.item(i).getFirstChild().getNodeValue().trim());
                cls.setCatalog(catalogs.item(i).getFirstChild().getNodeValue().trim());
                cls.setConfig(configs.item(i).getFirstChild().getNodeValue().trim());
                classes.put(cls.getClassname(),cls);
            }
            
            //小说的集合（各个小说类中的小说列表）
            //将每次的分类信息classes中的值集合依次传入 类中，将类中config提取
            for (Classification clz:classes.values()){
                //拿出一种小说的分类（获取对应的信息和路径）把本类的小说组织到一个MAP（nls）中
                doc=db.parse(new FileInputStream(clz.getConfig()));
                NodeList names=doc.getElementsByTagName("name");
                NodeList anthors=doc.getElementsByTagName("author");
                NodeList descriptions=doc.getElementsByTagName("description");
                NodeList filenames=doc.getElementsByTagName("filename");
                Map<String,Novel> nls=new HashMap<>();
                Novel novel=null;
                for (int i=0;i<names.getLength();i++){
                    novel=new Novel();
                    novel.setName(names.item(i).getFirstChild().getNodeValue().trim());
                    novel.setAuthor(anthors.item(i).getFirstChild().getNodeValue().trim());
                    novel.setDesc(descriptions.item(i).getFirstChild().getNodeValue().trim());
                    novel.setFilename(filenames.item(i).getFirstChild().getNodeValue().trim());
                    novel.setCls(clz);
                    nls.put(novel.getName(),novel);
                }
                //完善novelsMap
                novels.put(clz.getClassname(),nls);
            }
            
            //加载模版
            File modelfile=new File(Init.getProps("server.novel.model"));
            BufferedReader bf=null;
            StringBuilder sb=new StringBuilder();
            try {
                bf = new BufferedReader(new FileReader(modelfile));
                String line = null;
                while ((line = bf.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                
            }finally {
                if (bf!=null){
                    bf.close();
                }
            }
            model = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("小说加载失败！",e);
        }
    }
    
    //获取小说分类
    public static Classification[] getClassname(){
        //将classes中的值的所有信息传入array将其返回
        return classes.values().toArray(new Classification[0]);
    }
    
    //根据客户的要求显示某个分类的小说列表（参数为分类名）
    public static Novel[] getNovels(Classification cls){
        return novels.get(cls.getClassname()).values().toArray(new Novel[0]);
    }
    
    //小说预览功能（参数为客户端发送过来的小说）
    public static String getCon(Novel novel) throws IOException {
        //提前检测本小说有获取过
        String con=novels.get(novel.getCls().getClassname()).get(novel.getName()).getCon();
        if (con!=null){
            return con;
        }
        //提取小说类的路径
        String clsLog=classes.get(novel.getCls().getClassname()).getCatalog();
        //提取小说的路径
        String fileLog=novels.get(novel.getCls().getClassname()).get(novel.getName()).getFilename();
        
        //判断下xml路径是否正确(是否以\\结尾)
        if (!clsLog.endsWith(File.separator)){
            clsLog+=File.separator;//加上
        }
        //拼接完整路径
        File file=new File(clsLog+fileLog);
        //读取
        BufferedReader bf= new BufferedReader(new FileReader(file));
        
        //预览10行
        String mess = null;
        for (int i=0;i<10;i++){
            mess+=(bf.readLine())+"\n";
        }
        
        //讲预览内容保存（缓存）
        novels.get(novel.getCls().getClassname()).get(novel.getName()).setCon(mess);
        return mess;
    }
    
    //小说下载功能
    public static File getNoevlFile(Novel novel){
        //提取小说类的路径
        String clsLog=classes.get(novel.getCls().getClassname()).getCatalog();
        //提取小说的路径
        String fileLog=novels.get(novel.getCls().getClassname()).get(novel.getName()).getFilename();

        //判断下xml路径是否正确(是否以\\结尾)
        if (!clsLog.endsWith(File.separator)){
            clsLog+=File.separator;//加上
        }
        //拼接完整路径
        File file=new File(clsLog+fileLog);
        return file;
    }
    
    //判断小说是否存在
    public static boolean exists(Novel novel){
        return novels.get(novel.getCls().getClassname()).containsKey(novel.getName());
    }
    
    //执行判断，写入xml，执行上传！
    public static synchronized int saveNovel(Novel novel){
        if (exists(novel)){
            //文件已存在
            return Novel_constants.FILE_YES;
        }
        novel.setFilename(novel.getName()+".txt");
        //类名xml中的cofing
        File config=new File(classes.get(novel.getCls().getClassname()).getConfig());
        //新的小说信息（利用模版）
        String newNovel=String.format(model,novel.getName(),novel.getAuthor(),novel.getDesc(),novel.getFilename());
        
        BufferedReader reader=null;
        StringBuilder builder=new StringBuilder();
        try {
            reader=new BufferedReader(new FileReader(config));
            String line=null;
            //将内容提取
            while ((line=reader.readLine())!=null){
                builder.append(line);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if (reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //找到最后一个</
        int lastindex=builder.lastIndexOf("</");
        //添加信息
        String content= builder.insert(lastindex,newNovel).toString();
        
        FileWriter writer=null;
        try {
            writer =new FileWriter(config);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        novels.get(novel.getCls().getClassname()).put(novel.getName(),novel);
        return Novel_constants.SUCCESS;
    }
    
    //即将要创建的小说名（File）
    public static File makeNovelFile(Novel novel){
        String path=classes.get(novel.getCls().getClassname()).getCatalog();
        if (!path.endsWith(File.separator)){
            path+=File.separator;
        }
        String filename=novel.getName()+".txt";
        return new File(path + filename);
    }
}

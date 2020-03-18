package api;

import dao.Image;
import dao.imageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashSet;

public class ImageShowServlet extends HttpServlet {
   /* static private HashSet<String> whiteList = new HashSet<>();
    static {
        whiteList.add("http://59.110.154.22:8080/java_image_server/index.html");
    }*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

     /*   String referer = req.getHeader("Referer");
        if (!whiteList.contains(referer)) {
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"未授权的访问\" }");
            return;
        }*/
        //1.解析出imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId不存在\"}");
            return;
        }
        //2.根据imageId查找数据库，得到对应的图片属性信息（前提是知道图片存储的路径）
        imageDao imagedao=new imageDao();
        Image image=imagedao.selectOne(Integer.parseInt(imageId));
        //3.根据路径打开文件，读取图片的内容并写入到响应对象中
        resp.setContentType(image.getContentType());
        File file=new File(image.getPath());
        //由于图片是二进制文件，应该使用字节流的方式读取文件
        OutputStream outputStream=resp.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] buffer=new byte[1024];
        while(true){
            int len=fileInputStream.read(buffer);
            if(len==-1){
                //文件读取结束
                break;
            }
            //此时已经读到了一部分的数据，放到buffer里，把buffer里中的内容写到响应对象中
            outputStream.write(buffer);
        }
        fileInputStream.close();
        outputStream.close();
    }
}

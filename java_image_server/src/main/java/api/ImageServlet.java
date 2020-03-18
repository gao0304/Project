package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.imageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet{
    @Override
    //查看图片属性：既能查看所有，也能查看指定的一个
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         这里我们要实现查看所有图片属性和查看指定图片属性，通过URL中是否带有imageId参数来进行区分
         如果存在imageId查看指定图片属性，否则就查看所有的图片属性
         例如：URL/imageId?imageId=100,imageId的值就是"100",
         如果URL中不存在imageId那么返回null
         */
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            //查看所有图片属性
            selectAll(req,resp);
        }else {
            //查看指定图片属性
            selectOne(imageId,resp);
        }
        /**
         * 当这里写完这个方法后，还无法直接运行，还需要修改项目路径下的webapp/WEB-INF/web.xml文件,
         * 这个文件是项目创建的时候自动生成的，然后把把新创建的Servlet注册进去
         * 一个Servlet注册一次就好了
         */
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建imageDao对象
        imageDao imagedao=new imageDao();
        Image image=imagedao.selectOne(Integer.parseInt(imageId));
        //2.使用gson把查到的数据转换成JSON格式，并写回给resp
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建一个imageDao对象，并查找数据库
        imageDao imagedao=new imageDao();
        List<Image> images=imagedao.selectAll();
        //2.把查找到的结果转换为JSON格式的字符串，并且写回resp对象
        Gson gson=new GsonBuilder().create();
        //jsonData就是一个json格式的字符串了，就接之前约定的格式是一样的。
        //这个方法的核心是gson棒我们自动完成了大量的格式转换
        //只要把之前的相关的字段都约定成统一的命名，下面的操作就可以一步到位的完成整个转换
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    @Override
    //上传图片
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取图片的属性信息，并且存入数据库
        //a)需要创建一个factory对象和upload对象，这是为了获取到图片属性做的准备工作
        //都是一些固定的逻辑
        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        //b)通过upload对象进一步解析请求(解析Http请求中的二进制内容)
        //FileItem就代表一个上传的文件对象，理论上来说，http支持一个请求中同时上传多个文件
        List<FileItem> items=null;
        try{
            items=upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
            //告诉客户端出现的具体的错误
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"请求解析失败\" }");
            return;
        }
        //c)把FileItem中的属性提取出来，转换成Image对象，才能存到数据库中
        //当前我们只考虑一张图片的情况
        FileItem fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        //手动获取一下当前的时间，并转换为格式化日期
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        //计算md5
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        //自己构造一个路径来保存，引入时间戳是为了让文件的路径能够唯一
        image.setPath("/root/tomcat/apache-tomcat-9.0.31/bin/image/"+System.currentTimeMillis()+"_"+image.getImageName());
        //存到数据库中
        Image existImage=imageDao.selectByMd5(image.getMd5());
        if(existImage==null){
            imageDao imagedao=new imageDao();
            imagedao.insert(image);
        }else {
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"该图片已经存在了\" }");
            return;
        }
        //2.获取图片的内容信息，并且写入磁盘文件
        File file = new File(image.getPath());
        try {
            fileItem.write(file);
        } catch (Exception e) {
            e.printStackTrace();

            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"写磁盘失败\" }");
            return;
        }
        //3.给客户端返回一个结果数据
        /*
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write("{\"ok\":true}");
        */
        //实现302重定向，当上传图片后回到上传页面并刷新
        resp.sendRedirect("index.html");
    }

    @Override
    //删除指定图片
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.先获取请求中的imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"解析请求失败\"}");
            return;
        }
        //2.创建imageDao对象，查看该图片对应的相关属性（这是为了知道这个图片对应的文件路径）
        imageDao imagedao=new imageDao();
        Image image=imagedao.selectOne(Integer.parseInt(imageId));
        if(image==null){
            //说明此时请求中传入的id在数据库中不存在
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId在数据库中不存在\"}");
            return;
        }
        //3.删除数据库中的记录
        imagedao.delete(Integer.parseInt(imageId));
        //4.删除本地对应的磁盘文件
        File file=new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\":true}");
    }
}

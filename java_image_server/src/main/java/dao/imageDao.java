package dao;

import common.JavaImageServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class imageDao {
    //把image对象插入到数据库中,在插入之前要先在代码中构建一个image对象这样才能顺利插入
    public void insert(Image image){
        // 1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.创建并拼装SQL语句
        PreparedStatement statement=null;
        String sql="insert into image_table values(null,?,?,?,?,?,?)";
        //因为第一个是ID（自增主键不需要设置），后边的六个属性先用?填充，通过后边代码进行替换属性
        try {
            statement=connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getPath());
            statement.setString(6,image.getMd5());
            //3.执行SQL语句
            int ret = statement.executeUpdate();//更新数据库，如果返回结果为1则表示执行成功
            if (ret != 1) {
                // 程序出现问题, 抛出一个异常
                throw new JavaImageServerException("插入数据库出错!");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        } finally {
            // 4. 关闭连接和statement对象
            DBUtil.close(connection, statement, null);
        }
    }

    /**查找数据库中的所有图片的信息
     * 注意：当数据库的数据内容过多时不适合一次展示出来，
     * 科学的做法是指定一些筛选条件然后进行分页展示
     * @return
     */
    public List<Image> selectAll(){
        List<Image> images=new ArrayList<>();
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.构造SQL语句
        String sql="select * from image_table";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            //3.执行SQL语句
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            //4.处理结果集
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                images.add(image);
            }
            return images;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;//如果中间出现了异常,返回null
    }
    //根据imageId查找指定的图片信息（因为这里imageId是唯一且自增的主键。所以返回的时候不存在返回多个h或者为null）
    public Image selectOne(int imageId){
        //1.获取数据库连接
        Connection connection =DBUtil.getConnection();
        //2.构造SQL语句
        String sql="select * from image_table where imageId=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try{
            //3.执行SQL语句
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);//将imageId的值替换到?上
            resultSet=statement.executeQuery();//执行SQL语句,执行的是查找操作
            //4.处理结果集
            //因为这里的id是自增主键且唯一，所以下边可以不用while用if也行
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;//找不到的情况下返回null
    }
    //根据imageId删除指定的图片
    public void delete(int imageId){
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.拼接SQL语句
        String sql="delete from image_table where imageId=?";
        PreparedStatement statement=null;
        //3.执行SQL语句（此处是删除操作，不需要处理结果集）
        try{
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            int ret=statement.executeUpdate();
            if(ret!=1){
                throw new JavaImageServerException("删除指定图片操作失败");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        }finally {
            //4.关闭连接
            DBUtil.close(connection,statement,null);
        }
    }
    public static Image selectByMd5(String md5) {
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql = "select * from image_table where md5 = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 3. 执行 SQL 语句
            statement = connection.prepareStatement(sql);
            statement.setString(1, md5);
            resultSet = statement.executeQuery();
            // 4. 处理结果集
            if (resultSet.next()) {
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 5. 关闭链接
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }
    /** 如果数据库是在阿里云服务器上, 不在本地. 这个程序在本地直接运行无法访问数据库
     此处我需要把这个程序部署到云服务器上执行才能看到效果
     */
    public static void main(String[] args) {
       /*  // 用于进行简单的测试
        // 1.测试插入数据
        Image image = new Image();
        image.setImageName("1.png");
        image.setSize(100);
        image.setUploadTime("20200216");
        image.setContentType("image/png");
        image.setPath("./data/1.png");
        image.setMd5("11223344");
        imageDao imagedao = new imageDao();
        imagedao.insert(image);

       //2.测试查找所有图片信息
        imageDao imagedao=new imageDao();
        List<Image> images = imagedao.selectAll();
        System.out.println(images);
       //3.测试查找指定图片信息
        imageDao imagedao=new imageDao();
        Image image=imagedao.selectOne(2);
        System.out.println(image);*/
        //4.测试删除图片
        imageDao imagedao=new imageDao();
        imagedao.delete(1);
    }
}

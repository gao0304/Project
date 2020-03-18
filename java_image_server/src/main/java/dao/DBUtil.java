


package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL="jdbc:mysql://127.0.0.1:3306/java_image_server?characterEncoding=utf8&useSSL=true";
    private static final String USERNAME="root";
    private static final String PASSWORD="1";
    //dataSource对象用volatile关键字修饰,保持其内存可见性
    private static volatile DataSource dataSource =null;//借助DataSource这个类我们可以获取与数据库的连接操作数据库
    /**这里只有DataSource对象，是线程不安全的，因为多线程是抢占式执行的
    解决办法：1.加锁  2.双重判定  3.volatile关键字
     */
    public static DataSource getDataSource(){
        //通过这个方法来创建DateSource的实例
        //以下代码是单例模式下的多线程
        if (dataSource == null) { //双重判定：只有在第一次创建对象的时候才加锁，提高了效率
            synchronized (DBUtil.class) {  //加锁
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    MysqlDataSource tmpDataSource = (MysqlDataSource) dataSource;//将其类型转换
                    //设定进去URL，用户名，密码等
                    tmpDataSource.setURL(URL);
                    tmpDataSource.setUser(USERNAME);
                    tmpDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }
    //获取连接
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //关闭连接
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
        /**
         * 这里关闭的时候要注意：先创建的对象后关闭，后创建的对象先关闭
         * 小技巧：Ctrl+alt+t可以用一些特定的格式来改变选中的代码
         */
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}

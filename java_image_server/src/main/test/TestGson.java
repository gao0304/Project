import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

class  Hero{
    public String name;
    public String skill1;
    public String skill2;
    public String skill3;
    public String skill4;
}
public class TestGson {
    public static void main(String[] args) {
        /*//第一种方法：通过map转成JSON结构的字符串
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name","曹操");
        hashMap.put("sikll1","剑气");
        hashMap.put("sikll2","三段跳");
        hashMap.put("sikll3","加攻速");
        hashMap.put("sikll4","加攻击并吸血");*/

        //第二种方法：直接通过创建类来实现
        Hero hero=new Hero();
        hero.name="曹操";
        hero.skill1="剑气";
        hero.skill2="三段跳";
        hero.skill3="加攻速";
        hero.skill1="加攻击并吸血";
        //1.创建一个gson对象
        Gson gson=new GsonBuilder().create();
        //2.使用toJson方法把键值对结构转成JSON字符串
        //第一种方法String str=gson.toJson(hashMap);
        String str=gson.toJson(hero);
        System.out.println(str);
        //在JSON里边对于字段之间的顺序没有明确的要求
    }
}

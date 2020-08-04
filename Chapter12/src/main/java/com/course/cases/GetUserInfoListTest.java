package com.course.cases;

import com.course.config.TestConfig;
import com.course.model.GetUserInfoCase;
import com.course.model.GetUserListCase;
import com.course.model.User;
import com.course.utils.DatabaseUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resources;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class GetUserInfoListTest {

    @Test(dependsOnGroups = "loginTrue",description = "获取性别为男的用户信息")
    public void getUserListInfo() throws IOException {
        SqlSession session = DatabaseUtil.getSqlSession();
        GetUserListCase getUserListCase = session.selectOne("getUserListCase",1);
        System.out.println(getUserListCase.toString());
        System.out.println(TestConfig.getUserListUrl);

        //发送请求获取结果
        JSONArray resultJson = getJsonResult(getUserListCase);

        //验证
        List<User> userList = session.selectList(getUserListCase.getExpected(),getUserListCase);
        for(User u: userList){
            System.out.println("list获取的user:" + u.toString());
        }
        JSONArray userListJson = new JSONArray(userList);

        Assert.assertEquals(userListJson.length(),resultJson.length());

        for(int i = 0 ; i<resultJson.length(); i++){
            JSONObject expect = (JSONObject) resultJson.get(i);
            JSONObject actual = (JSONObject) userListJson.get(i);
            Assert.assertEquals(expect,actual);
        }

    }

    private JSONArray getJsonResult(GetUserListCase getUserInfoListCase) throws IOException {
        HttpPost post = new HttpPost(TestConfig.getUserListUrl);
        JSONObject param = new JSONObject();
        param.put("userName" , getUserInfoListCase.getUserName());
        param.put("sex" , getUserInfoListCase.getSex());
        param.put("age" , getUserInfoListCase.getAge());

        //设置请求头信息 设置header
        post.setHeader("content-type","application/json");
        //将参数信息添加到方法中
        StringEntity entity = new StringEntity(param.toString(),"utf-8");
        post.setEntity(entity);

        //设置cookies
        TestConfig.defaultHttpClient.setCookieStore(TestConfig.store);
        //声明一个对象来进行响应结果的存储
        String result;
        //执行post方法
        HttpResponse response = TestConfig.defaultHttpClient.execute(post);

        //获取响应结果
        result  = EntityUtils.toString(response.getEntity(),"utf-8");
        JSONArray jsonArray = new JSONArray(result);

        return jsonArray;
    }
}

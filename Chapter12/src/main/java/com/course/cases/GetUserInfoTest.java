package com.course.cases;

import com.course.config.TestConfig;
import com.course.model.GetUserInfoCase;
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

import javax.management.relation.RelationNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetUserInfoTest {

    @Test(dependsOnGroups = "loginTrue",description = "获取用户信息")
    public void getUserInfo() throws IOException {
        SqlSession session = DatabaseUtil.getSqlSession();
        GetUserInfoCase getUserInfoCase = session.selectOne("getUserInfoCase",1);
        System.out.println(getUserInfoCase.toString());
        System.out.println(TestConfig.getUserInfoUrl);

        //获取结果
        JSONArray resultJson = getJsonResult(getUserInfoCase);

        //验证
        User user = session.selectOne(getUserInfoCase.getExpected(),getUserInfoCase);

        List userList = new ArrayList();
        userList.add(user);

        JSONArray jsonArray = new JSONArray(userList);

        Assert.assertEquals(jsonArray,resultJson);
    }

    private JSONArray getJsonResult(GetUserInfoCase getUserInfoCase) throws IOException {
        HttpPost post = new HttpPost(TestConfig.getUserInfoUrl);
        JSONObject param = new JSONObject();
        param.put("id",getUserInfoCase.getUserId());

        //设置头信息
        post.setHeader("content-type","application/json");

        //塞参数
        StringEntity entity = new StringEntity(param.toString(),"utf-8");
        post.setEntity(entity);

        //设置cookies
        TestConfig.defaultHttpClient.setCookieStore(TestConfig.store);

        //获取响应结果结果
        HttpResponse response = TestConfig.defaultHttpClient.execute(post);
        String result;

        result = EntityUtils.toString(response.getEntity(),"utf-8");

        //将响应结果用List接收后才能转换成JSONArray
        List resultList  = Arrays.asList(result);

        JSONArray jsonArray = new JSONArray(resultList);

        return jsonArray;
    }
}

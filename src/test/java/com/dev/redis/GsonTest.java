package com.dev.redis;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GsonTest {

    public static void main(String[] args) {
        Gson gson = new Gson();
        List<String> strList = new ArrayList<String>();
        strList.add("aaa");
        strList.add("bbb");
        String result = gson.toJson(strList);
        System.out.println(result);
    }
}

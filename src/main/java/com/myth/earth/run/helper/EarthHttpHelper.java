/*
 * Copyright (c) 2025 周潮. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.myth.earth.run.helper;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myth.earth.run.bean.ClassloaderInfo;
import com.myth.earth.run.bean.ObjectItem;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * agent 请求接口列表
 *
 * @author zhouchao
 * @date 2024-11-18 下午6:22
 */
public class EarthHttpHelper {


    /**
     * 获取AppClassLoader类加载器
     *
     * @param addressUrl 根路径
     * @return 类加载器地址
     */
    public static String getAppClassLoader(@NotNull String addressUrl) {
        return HttpUtil.get(addressUrl + "/api/console/appClassLoader");
    }

    /**
     * 执行代码
     *
     * @param addressUrl 根地址
     * @param loaderId   加载器ID
     * @param code       代码内容
     */
    public static void debug(@NotNull String addressUrl, @NotNull String loaderId, @NotNull String code) {
        String encode = URLEncoder.encode(code, StandardCharsets.UTF_8);
        HttpUtil.get(addressUrl + "/api/console/debug?loaderId=" + loaderId + "&code=" + encode);
    }

    /**
     * 获取所有类加载器
     *
     * @param addressUrl 根路径
     * @return 类加载器信息
     */
    public static List<ClassloaderInfo> getAllClassLoaders(@NotNull String addressUrl) {
        String result = HttpUtil.get(addressUrl + "/api/console/allClassLoader");
        return JSON.parseArray(result, ClassloaderInfo.class);
    }

    /**
     * 关闭session信息
     *
     * @param addressUrl 根路径
     * @param sessionId sessionId
     */
    public static void closeSession(@NotNull String addressUrl , @NotNull String sessionId) {
        HttpUtil.get(addressUrl + "/api/console/close?sessionId=" + sessionId);
    }

    /**
     * 获取sessionId
     *
     * @param addressUrl 根路径
     * @param classloaderId 类加载器ID
     * @return sessionId
     */
    public static String openSession(@NotNull String addressUrl,@NotNull String classloaderId) {
        String result = HttpUtil.get(addressUrl + "/api/console/open?loaderId=" + classloaderId);
        return JSONObject.parseObject(result).getString("sessionId");
    }

    /**
     * 执行代码
     *
     * @param addressUrl 根路径
     * @param sessionId 会话ID
     * @param groovyCode 执行代码
     * @return 执行结果
     */
    public static ObjectItem eval(@NotNull String addressUrl, @NotNull String sessionId, @NotNull String groovyCode) {
        String encode = URLEncoder.encode(groovyCode, StandardCharsets.UTF_8);
        String result = HttpUtil.get(addressUrl + "/api/console/eval?sessionId=" + sessionId + "&code=" + encode);
        return JSONObject.parseObject(result, ObjectItem.class);
    }

    public static List<ObjectItem> detail(String addressUrl,@NotNull String sessionId, ObjectItem objectItem) {
        String path = URLEncoder.encode(objectItem.getPath(), StandardCharsets.UTF_8);
        Integer size = Optional.ofNullable(objectItem.getChildSize()).filter(i -> i > 20).orElse(-1);
        String result = HttpUtil.get(addressUrl + "/api/console/detail?sessionId=" + sessionId + "&objectPath=" + path + "&begin=-1&size=" + size + "&level=0");
        return JSONArray.parseArray(result, ObjectItem.class);
    }
}

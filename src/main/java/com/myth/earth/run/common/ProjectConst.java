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

package com.myth.earth.run.common;

import com.google.common.collect.Sets;
import com.myth.earth.run.bean.GroovyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 项目中的固定常量
 *
 * @author zhouchao
 * @date 2024-11-20 下午9:34
 */
public class ProjectConst {

    /**
     * 项目输出目录名称
     */
    public static final String           PROJECT_OUTPUT_DIRECTORY = "earth-run-helper";
    /**
     * toolwindow控制台
     */
    public static final String           DEBUG_ULTRA              = "EarthRunHelper.Tool";
    /**
     * groovy code 头部
     */
    public static final String           GROOVY_HEAD              =
            "def getObject(str) {\n" + "    Object obj = get(str)\n" + "    if ((obj instanceof Object[]) && obj.length > 0) {\n" + "        return obj[0]\n"
                    + "    } else {\n" + "        return obj\n" + "    }\n" + "}\n";
    /**
     * 包装数据类型
     */
    public static final Set<String>      WRAPPER_DATA_TYPE;
    /**
     * 默认脚本
     */
    public static final List<GroovyCode> DEFAULT_SHELL;

    static {
        WRAPPER_DATA_TYPE = Sets.newHashSet("Byte", "Short", "Integer", "Long", "Character", "Float", "Double", "Boolean", "String");

        DEFAULT_SHELL = new ArrayList<>();
        DEFAULT_SHELL.add(new GroovyCode("获取系统属性", "System.getProperties()"));
        DEFAULT_SHELL.add(new GroovyCode("获取Spring上下文", "import org.springframework.context.ApplicationContext\nget ApplicationContext.class"));
        DEFAULT_SHELL.add(new GroovyCode("获取Spring中所有的Bean", "import org.springframework.context.ApplicationContext\n"
                + "ApplicationContext context=get(ApplicationContext.class)[0]\n" + "context.getBeanDefinitionNames().collect {it-> \n"
                + "  context.getBean(it)\n" + "}"));
        DEFAULT_SHELL.add(new GroovyCode("Druid数据源", "import com.alibaba.druid.pool.DruidDataSource\nget DruidDataSource.class"));
        DEFAULT_SHELL.add(new GroovyCode("时间戳转换",
                                         "import java.text.SimpleDateFormat\n" + "import java.util.Date\n" + "long timestamp = System.currentTimeMillis()\n"
                                                 + "String pattern = \"yyyy-MM-dd HH:mm:ss\"\n" + "SimpleDateFormat sdf = new SimpleDateFormat(pattern);\n"
                                                 + "sdf.format(new Date(timestamp))"));
    }
}

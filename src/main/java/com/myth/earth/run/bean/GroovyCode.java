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

package com.myth.earth.run.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * groovy shell 信息
 *
 * @author zhouchao
 * @date 2024-11-27 下午12:18
 */
@Data
public class GroovyCode implements Serializable {
    /**
     * 显示标题
     */
    private String  title;
    /**
     * 代码
     */
    private String  code;
    /**
     * 默认
     */
    private Boolean defaultFlag;

    public GroovyCode() {
    }

    public GroovyCode(String title, String code) {
        this.title = title;
        this.code = code;
        this.defaultFlag = true;
    }

    private static final long serialVersionUID = 1L;
}

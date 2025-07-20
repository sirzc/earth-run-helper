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
import java.util.List;

/**
 * object item数据
 *
 * @author zhouchao
 * @date 2024-11-25 下午10:34
 */
@Data
public class ObjectItem implements Serializable {
    private String           id;
    private String           objectId;
    /**
     * class 名称
     */
    private String           type;
    /**
     * 对象所属标志：根节点root、属性property、方法method、条目entry、索引index
     */
    private String           flag;
    private String           name;
    private String           value;
    private List<ObjectItem> children;
    /**
     * 访问路径
     */
    private String           path;
    /**
     * 当type为Collection\map\Array类型时才有值
     */
    private Integer          childSize;
    /**
     * 原始数据
     */
    private boolean          atomic = false;
    /**
     * 错误信息
     */
    private String           errorMessage;
    /**
     * 异常堆栈
     */
    private String           errorStack;
    /**
     * 异常类型
     */
    private String           errorType;

    private static final long serialVersionUID = 1L;
}

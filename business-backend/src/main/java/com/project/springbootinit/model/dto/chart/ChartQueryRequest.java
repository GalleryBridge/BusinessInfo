package com.project.springbootinit.model.dto.chart;

import com.project.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

     /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 表图类型
     */
    private String chartType;

    /**
     * 用户ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}

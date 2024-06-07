package com.project.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 表图数据
     */
    private String chartData;

    /**
     * 表图类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}

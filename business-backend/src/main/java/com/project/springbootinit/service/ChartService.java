package com.project.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.springbootinit.model.dto.chart.ChartQueryRequest;
import com.project.springbootinit.model.entity.Chart;

import javax.servlet.http.HttpServletRequest;

/**
* @author Laido
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-06-06 21:29:30
*/
public interface ChartService extends IService<Chart> {
    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Chart post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest postQueryRequest);


    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
    Chart getChartVO(Chart post, HttpServletRequest request);


}

package com.project.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.springbootinit.common.ErrorCode;
import com.project.springbootinit.exception.BusinessException;
import com.project.springbootinit.exception.ThrowUtils;
import com.project.springbootinit.mapper.ChartMapper;
import com.project.springbootinit.model.dto.chart.ChartQueryRequest;
import com.project.springbootinit.model.entity.Chart;
import com.project.springbootinit.model.entity.User;
import com.project.springbootinit.model.vo.ChartVO;
import com.project.springbootinit.model.vo.UserVO;
import com.project.springbootinit.service.ChartService;
import com.project.springbootinit.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Laido
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-06-06 21:29:30
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

    @Resource
    private UserService userService;

    @Override
    public void validPost(Chart post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String goal = post.getGoal();
        String chartData = post.getChartData();
        String chartType = post.getChartType();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(goal, chartData, chartType), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(goal) && goal.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(goal)) {
            queryWrapper.like(StringUtils.isNotBlank(goal), "goal", goal);
        }
        queryWrapper.like(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        return queryWrapper;
    }

    @Override
    public Chart getChartVO(Chart chart, HttpServletRequest request) {
        ChartVO postVO = ChartVO.objToVo(chart);
        long postId = chart.getId();
        // 1. 关联查询用户信息
        Long userId = chart.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postVO.setUser(userVO);
        return chart;
    }

}





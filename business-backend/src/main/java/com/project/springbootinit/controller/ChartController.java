package com.project.springbootinit.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.springbootinit.annotation.AuthCheck;
import com.project.springbootinit.bizmq.BiConsumer;
import com.project.springbootinit.bizmq.BiProducer;
import com.project.springbootinit.common.BaseResponse;
import com.project.springbootinit.common.DeleteRequest;
import com.project.springbootinit.common.ErrorCode;
import com.project.springbootinit.common.ResultUtils;
import com.project.springbootinit.constant.CommonConstant;
import com.project.springbootinit.constant.UserConstant;
import com.project.springbootinit.exception.BusinessException;
import com.project.springbootinit.exception.ThrowUtils;
import com.project.springbootinit.manager.AiManager;
import com.project.springbootinit.manager.RedisLimitManager;
import com.project.springbootinit.model.dto.chart.*;
import com.project.springbootinit.model.entity.Chart;
import com.project.springbootinit.model.entity.User;
import com.project.springbootinit.model.vo.BiResponse;
import com.project.springbootinit.service.ChartService;
import com.project.springbootinit.service.UserService;
import com.project.springbootinit.utils.ExcelUtils;
import com.project.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.threadpool.ThreadPool;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimitManager redisLimitManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BiProducer biProducer;

    /**
     * 增加
     *
     * @param ChartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest ChartAddRequest, HttpServletRequest request) {
        if (ChartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(ChartAddRequest, chart);
        chartService.validPost(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 修改
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, Chart);
        // 参数校验
        chartService.validPost(Chart, false);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(Chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = chartService.getById(id);
        if (Chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chartService.getChartVO(Chart, request));
    }

    /**
     * 分页获取列表 (管理员)
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        Page<Chart> ChartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(ChartPage);
    }

    /**
     * 修改
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart Chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, Chart);
        // 参数校验
        chartService.validPost(Chart, false);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(Chart);
        return ResultUtils.success(result);
    }

    /**
     * 智能生成
     *
     * @param multipartFile
     * @param genChartByAIRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAIRequest genChartByAIRequest, HttpServletRequest request) {
        String chartName = genChartByAIRequest.getChartName();
        String goal = genChartByAIRequest.getGoal();
        String chartType = genChartByAIRequest.getChartType();
        //  校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //  校验文件大小
        long size = multipartFile.getSize();
        String filename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件过大, 限制1M");

        //  校验文件后缀
        String suffix = FileUtil.getSuffix(filename);
        final List<String> validFileSuffixList = Arrays.asList("png", "jpg", "svg", "webp", "jpeg", "xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);

        //  限流判断
        redisLimitManager.doRateLimit("genChartByAi_" + loginUser.getId());
        //  使用现有的AI模型
        long biModelId = 1651472468042432513L;
        StringBuilder userInput = new StringBuilder();
        //  给AI模型一个预设 构造用户输入
        userInput.append("分析需求:").append("\n");

        String userGoal = goal;
        if (StringUtils.isNotBlank(userGoal))
            userGoal = ".请使用" + chartType;
        userInput.append(goal).append("\n");
        userInput.append("原始数据:").append("\n");
        String res = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(res).append("\n");

        //  插入到数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(res);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setStatus("wait");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        //  读取文件 进行处理 压缩信息
        CompletableFuture.runAsync(() -> {
            //  修改任务状态为执行中，任务完成后修改程已完成，执行失败修改程失败
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                handleChartUpdateError(chart.getId(), "更新图表运行状态失败");
                return;
            }
            //  压缩后的数据
            //  拼接用户输入 传入doChat
            String result = aiManager.doChat(userInput.toString(), 1709156902984093697L);
            String[] splits = result.split("【【【【【");
            System.out.println(splits.length);
            if (splits.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "BI生成错误");
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            //  TODO 建议定义状态为枚举
            updateChartResult.setStatus("succeed");
            boolean updateResult = chartService.updateById(updateChart);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        }, threadPoolExecutor);
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult)
            log.error("更新图表失败状态错误", chartId + "," + execMessage);

    }

    /**
     * 根据条件查询 可选
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest != null)
            return queryWrapper;
        Long id = chartQueryRequest.getId();
        String chartName = chartQueryRequest.getChartName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortField.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @PostMapping("/gen/mq")
    public BaseResponse<BiResponse> genChartByAiMq(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAIRequest genChartByAIRequest, HttpServletRequest request) {
        String chartName = genChartByAIRequest.getChartName();
        String goal = genChartByAIRequest.getGoal();
        String chartType = genChartByAIRequest.getChartType();
        //  校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //  校验文件大小
        long size = multipartFile.getSize();
        String filename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件过大, 限制1M");

        //  校验文件后缀
        String suffix = FileUtil.getSuffix(filename);
        final List<String> validFileSuffixList = Arrays.asList("png", "jpg", "svg", "webp", "jpeg", "xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);

        //  限流判断
        redisLimitManager.doRateLimit("genChartByAi_" + loginUser.getId());
        //  使用现有的AI模型
        long biModelId = 1651472468042432513L;
        StringBuilder userInput = new StringBuilder();
        //  给AI模型一个预设 构造用户输入
        userInput.append("分析需求:").append("\n");

        String userGoal = goal;
        if (StringUtils.isNotBlank(userGoal))
            userGoal = ".请使用" + chartType;
        userInput.append(goal).append("\n");
        userInput.append("原始数据:").append("\n");
        String res = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(res).append("\n");

        //  插入到数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(res);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setStatus("wait");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        //  读取文件 进行处理 压缩信息
        //  在这里改造成使用消息队列
        Long newChartId = chart.getId();
        biProducer.sendMessage(String.valueOf(newChartId));

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }

}

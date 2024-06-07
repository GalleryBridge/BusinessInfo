package com.project.springbootinit.model.vo;

import com.project.springbootinit.model.entity.Chart;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChartVO implements Serializable {

    /**
     * id
     */
    private Long id;

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

    /**
     * 生成的表图数据
     */
    private String genChart;

    /**
     * 生成的表图结论
     */
    private String genResult;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 包装类转对象
     *
     * @param chartVO
     * @return
     */
    public static Chart voToObj(ChartVO chartVO) {
        if (chartVO == null) {
            return null;
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartVO, chart);
        return chart;
    }

    /**
     * 对象转包装类
     *
     * @param chart
     * @return
     */
    public static ChartVO objToVo(Chart chart) {
        if (chart == null) {
            return null;
        }
        ChartVO postVO = new ChartVO();
        BeanUtils.copyProperties(chart, postVO);
        return postVO;
    }
}

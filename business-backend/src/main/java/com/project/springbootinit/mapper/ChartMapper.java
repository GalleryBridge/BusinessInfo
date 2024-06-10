package com.project.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.springbootinit.model.entity.Chart;

import java.util.List;
import java.util.Map;

/**
* @author Laido
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-06-06 21:29:30
* @Entity generator.domain.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    List<Map<String, Object>> queryChartData(String querySql);

}





package com.project.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.springbootinit.mapper.ChartMapper;
import com.project.springbootinit.model.entity.Chart;
import com.project.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

/**
* @author Laido
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-06-06 21:29:30
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

}





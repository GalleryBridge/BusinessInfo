package com.project.springbootinit.manager;

import com.project.springbootinit.common.ErrorCode;
import com.project.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AiManager {

    @Resource
    YuCongMingClient yuCongMingClient;

    public String doChat(String message, Long modelId){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI接口相应错误");
        return response.getData().getContent();
    }
}

package com.learninghero.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learninghero.config.WechatConfig;
import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class WechatUtil {

    private final WechatConfig wechatConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WechatUtil(WechatConfig wechatConfig) {
        this.wechatConfig = wechatConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getOpenid(String code) {
        String url = String.format("%s/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getApiUrl(),
                wechatConfig.getAppId(),
                wechatConfig.getAppSecret(),
                code);

        try {
            String response = Objects.requireNonNull(restTemplate.getForObject(Objects.requireNonNull(url), String.class));
            log.info("微信登录响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("微信登录失败: errcode={}, errmsg={}", jsonNode.get("errcode").asInt(), errmsg);
                throw new BusinessException(ErrorCode.WECHAT_API_ERROR, "微信登录失败: " + errmsg);
            }

            if (!jsonNode.has("openid")) {
                throw new BusinessException(ErrorCode.WECHAT_API_ERROR, "获取OpenID失败");
            }

            return jsonNode.get("openid").asText();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BusinessException(ErrorCode.WECHAT_API_ERROR, "微信接口调用失败");
        }
    }

    public Map<String, Object> getSessionInfo(String code) {
        String url = String.format("%s/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getApiUrl(),
                wechatConfig.getAppId(),
                wechatConfig.getAppSecret(),
                code);

        try {
            String response = Objects.requireNonNull(restTemplate.getForObject(Objects.requireNonNull(url), String.class));
            log.info("微信登录响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("微信登录失败: errcode={}, errmsg={}", jsonNode.get("errcode").asInt(), errmsg);
                throw new BusinessException(ErrorCode.WECHAT_API_ERROR, "微信登录失败: " + errmsg);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("openid", jsonNode.has("openid") ? jsonNode.get("openid").asText() : null);
            result.put("sessionKey", jsonNode.has("session_key") ? jsonNode.get("session_key").asText() : null);
            result.put("unionid", jsonNode.has("unionid") ? jsonNode.get("unionid").asText() : null);

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BusinessException(ErrorCode.WECHAT_API_ERROR, "微信接口调用失败");
        }
    }
}

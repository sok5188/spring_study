package com.example.guess_music.domain.userInfo;

import java.util.Map;

public class NaverUserInfo implements OAuthUserInfo{
    private Map<String, Object> attributes;
    private Map<String, Object> attributesResponse;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesResponse = (Map<String, Object>) attributes.get("response");
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributesResponse.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return attributesResponse.get("email").toString();
    }

    @Override
    public String getName() {
        return attributesResponse.get("name").toString();
    }
}

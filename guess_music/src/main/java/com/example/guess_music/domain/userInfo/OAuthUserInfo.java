package com.example.guess_music.domain.userInfo;

import java.util.Map;

public interface OAuthUserInfo {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}

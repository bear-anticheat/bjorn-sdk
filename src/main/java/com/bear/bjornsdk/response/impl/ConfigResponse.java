package com.bear.bjornsdk.response.impl;

import com.bear.bjornsdk.object.Configuration;
import com.bear.bjornsdk.response.BjornResponse;
import lombok.Data;

@Data
public class ConfigResponse implements BjornResponse {

    private final boolean success;

    private final Configuration configuration;
}

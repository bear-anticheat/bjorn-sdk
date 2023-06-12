package com.bear.bjornsdk.response.impl;

import com.bear.bjornsdk.response.BjornResponse;
import lombok.Data;

@Data
public class ServerSearchResponse implements BjornResponse {

    private final boolean success, result;
}

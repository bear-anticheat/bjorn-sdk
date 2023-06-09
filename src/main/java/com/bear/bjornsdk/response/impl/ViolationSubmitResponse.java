package com.bear.bjornsdk.response.impl;

import com.bear.bjornsdk.response.BjornResponse;
import lombok.Data;

@Data
public class ViolationSubmitResponse implements BjornResponse {

    private final String message;

    private final boolean success;
}

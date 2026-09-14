package com.bear.bjornsdk.response.impl;

import com.bear.bjornsdk.object.BjornServer;
import com.bear.bjornsdk.response.BjornResponse;
import lombok.Data;

@Data
public class BjornServerResponse implements BjornResponse {

    private final boolean success;

    private final BjornServer server;
}

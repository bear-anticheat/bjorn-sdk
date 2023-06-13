package com.bear.bjornsdk.object;

import lombok.Data;

@Data
public class Configuration {

    private final String alertFormat;
    private final String[] banAlertFormat;

    private final boolean proxyAlerts;
}

package com.bear.bjornsdk.object;

import lombok.Data;

@Data
public class Configuration {

    private final String alertFormat, banCommand;
    private final String[] banAlertFormat;

    private final boolean proxyAlerts, proxyBans;
}

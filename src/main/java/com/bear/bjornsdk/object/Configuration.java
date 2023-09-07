package com.bear.bjornsdk.object;

import com.bear.bjornsdk.fields.ReconnectionAction;
import lombok.Data;

@Data
public class Configuration {

    private final String alertFormat, banCommand;
    private final String[] banAlertFormat;

    private final ReconnectionAction reconnectionAction;

    private final boolean proxyAlerts, proxyBans;
}

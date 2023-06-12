package com.bear.bjornsdk.object;

import lombok.Data;

import java.util.UUID;

@Data
public class Violation {

    private final String debug;
    private final String checkParent, checkType;
    private final String serverLicense, serverName;

    private final UUID uuid;

    private final int vl;
}

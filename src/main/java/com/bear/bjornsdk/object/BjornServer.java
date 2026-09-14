package com.bear.bjornsdk.object;

import lombok.Data;

import java.util.UUID;

@Data
public class BjornServer {

    private final String name, license;
    private final UUID id, owner;
}

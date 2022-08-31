package com.zakux.live.token;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}

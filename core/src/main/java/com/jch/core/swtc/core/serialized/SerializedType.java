package com.jch.core.swtc.core.serialized;

import com.jch.core.swtc.core.fields.Type;

public interface SerializedType {
    Object toJSON();
    byte[] toBytes();
    String toHex();
    void toBytesSink(BytesSink to);
    Type type();
}

package com.usrun.core.config.cache;

import org.redisson.codec.KryoCodec;

import java.util.Collections;
import java.util.List;

/**
 * @author phuctt4
 */

public class KryoCodecWithDefaultSerializer extends KryoCodec {

    public KryoCodecWithDefaultSerializer() {
        this(Collections.<Class<?>>emptyList());
    }

    public KryoCodecWithDefaultSerializer(List<Class<?>> classes) {
        super(new KryoPoolImplWithDefaultSerializer(classes));
    }

    public KryoCodecWithDefaultSerializer(KryoPool kryoPool) {
        super(kryoPool);
    }
}
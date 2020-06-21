package com.usrun.core.config.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import java.util.List;
import org.redisson.codec.KryoCodec;

/**
 * @author phuctt4
 */
public class KryoPoolImplWithDefaultSerializer extends KryoCodec.KryoPoolImpl {

  public KryoPoolImplWithDefaultSerializer(List<Class<?>> classes) {
    super(classes, null);
  }

  @Override
  protected Kryo createInstance() {
    Kryo kryo = super.createInstance();
    kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
    return kryo;
  }

}
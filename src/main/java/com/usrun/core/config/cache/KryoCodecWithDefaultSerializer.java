//package com.usrun.core.config.cache;
//
//import java.util.Collections;
//import java.util.List;
//import org.redisson.codec.KryoCodec;
//
///**
// * @author phuctt4
// */
//
//public class KryoCodecWithDefaultSerializer extends KryoCodec {
//
//  public KryoCodecWithDefaultSerializer() {
//    this(Collections.<Class<?>>emptyList());
//  }
//
//  public KryoCodecWithDefaultSerializer(List<Class<?>> classes) {
//    super(new KryoPoolImplWithDefaultSerializer(classes));
//  }
//
//  public KryoCodecWithDefaultSerializer(KryoPool kryoPool) {
//    super(kryoPool);
//  }
//}
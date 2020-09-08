package com.usrun.core.payload;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class UploadImageResponse {
  private boolean success;
  private Data data;

  @lombok.Data
  public static class Data {
    private String link;
  }
}

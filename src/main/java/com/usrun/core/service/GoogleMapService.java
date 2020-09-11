/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.service;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.usrun.core.config.AppProperties;
import com.usrun.core.model.track.Location;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author huyna3
 */
@Component
public class GoogleMapService {

  private static final Logger logger = LoggerFactory.getLogger(GoogleMapService.class);
  private static final String url = "https://maps.googleapis.com/maps/api/staticmap?";

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private ImageClient imageClient;

  @Autowired
  private ActivityService activityService;

  public String getRouteImage(List<List<Location>> dataLocation) throws IOException {
    List<Location> locations = dataLocation.stream().flatMap(Collection::stream)
        .collect(Collectors.toList());
    List<LatLng> latLngs = locations.stream()
        .map(location -> new LatLng(location.getLatitude(), location.getLongitude())).collect(
            Collectors.toList());
    EncodedPolyline encodedPolyline = new EncodedPolyline(latLngs);
    String path = "color:0xf0602dFF|weight:2|enc:" + encodedPolyline.getEncodedPath();
    String key = appProperties.getGoogleMapKey();
    String markerStart = "";
    String markerFinish = "";
    if (!locations.isEmpty()) {
      markerStart = "color:blue|label:S|" + locations.get(0).getLatitude() + "," + locations.get(0)
          .getLongitude();
      markerFinish = "color:blue|label:F|" + locations.get(locations.size() - 1).getLatitude() + ","
          + locations.get(locations.size() - 1).getLongitude();
    }
    String query = "size=720x720&path=" + path + "&markers=" + URLEncoder
        .encode(markerStart, StandardCharsets.UTF_8.name()) + "&markers=" +
        URLEncoder.encode(markerFinish, StandardCharsets.UTF_8.name()) + "&key=" + key;
    BufferedImage resultImage = ImageIO.read(new URL(url + query));
    String fileUrl = imageClient.uploadFile(resultImage, "png");

    return fileUrl == null && fileUrl.isEmpty() ? activityService.IMAGE_DEFAULT : fileUrl;
  }

  public static double latRad(double lat) {
    double sin = Math.sin(lat * Math.PI / 180);
    double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
    return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
  }

  public static double zoom(int mapPx, int worldPx, double fraction) {
    return Math.floor(Math.log(mapPx / worldPx / fraction) / 0.6931471805599453);
  }

  public static Location calcCenterPosition(List<Location> data) {
    Double north = Double.MIN_VALUE;
    Double south = Double.MAX_VALUE;
    Double west = Double.MIN_VALUE;
    Double east = Double.MAX_VALUE;

    for (Location item : data) {
      north = item.getLatitude() > north ? item.getLatitude() : north;
      south = item.getLatitude() < south ? item.getLatitude() : south;
      west = item.getLongitude() > west ? item.getLongitude() : west;
      east = item.getLongitude() < east ? item.getLongitude() : east;
    }
    double latFraction = (latRad(north) - latRad(south)) / Math.PI;
    double lngDiff = west - east;
    double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff / 360);
    double latZoom = zoom(400, 256, latFraction);
    double lngZoom = zoom(400, 256, lngFraction);
    return new Location((north + south) / 2, (west + east) / 2,
        Math.round(Math.min(latZoom, lngZoom)));
  }

}

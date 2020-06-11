package com.usrun.core.service;

import com.usrun.core.model.track.Point;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ActivityCalcService {

    public static final long unitDistance = 1000l;
    private static final double R = 6371e3; //theo met
    private ActivityCalcService() {
    }


    private double distanceBetween2Points(double la1, double lo1,
                                                double la2, double lo2) {
        double dLat = (la2 - la1) * (Math.PI / 180);
        double dLon = (lo2 - lo1) * (Math.PI / 180);
        double la1ToRad = la1 * (Math.PI / 180);
        double la2ToRad = la2 * (Math.PI / 180);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public double calcDistancePoint(Point point1, Point point2) {
        if(point1 == null || point2 == null) return 0;
        return distanceBetween2Points(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }

    public double calcTotalDistance(List<Point> dataRun) {
        if (dataRun == null) {
            return 0;
        }
        long totalDistance = 0;
        for (int i = 0; i < dataRun.size() - 1; i++) {
            totalDistance += calcDistancePoint(dataRun.get(i), dataRun.get(i + 1));
        }
        return totalDistance;
    }

//    public long calcTotalTime(List<Point> dataRun) {
//        if (dataRun == null || dataRun.size() == 1) {
//            return 0;
//        }
//        dataRun.sort(Comparator.comparing(Point::getTime));
//        return dataRun.get(dataRun.size() - 1).getTime().getTime() - dataRun.get(0).getTime().getTime();
//    }

    public float getAvgPace(long totalDistance, long totalTime) {
        if (totalDistance == 0) return 0;
        return (float) totalTime / totalDistance;
    }

//    public Map<Float, Double> getPacePerDistance(List<Point> dataRun) {
//        if (dataRun == null || dataRun.size() < 2) {
//            return new HashMap<Float, Double>() {
//                {
//                    put(0f, 0d);
//                }
//            };
//        }
//        double currentDistance = 0l;
//        long currentTime = 0l;
//        float numberDistance = 0;
//        Map<Float, Double> result = new HashMap<>();
//        for (int i = 0; i < dataRun.size() - 1; i++) {
//            currentDistance += calcDistancePoint(dataRun.get(i), dataRun.get(i + 1));
//            currentTime = dataRun.get(i + 1).getTime().getTime() - dataRun.get(i).getTime().getTime();
//            if (currentDistance >= unitDistance) {
//                numberDistance++;
//                result.put(numberDistance, currentTime / currentDistance);
//                currentTime = 0l;
//                currentDistance = 0d;
//            }
//        }
//        if (currentDistance > 0) {
//            result.put((float) currentDistance,currentTime / currentDistance);
//        }
//        return result;
//    }


}

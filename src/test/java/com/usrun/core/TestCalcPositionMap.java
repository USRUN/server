/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usrun.core.model.track.Location;
import com.usrun.core.utility.ObjectUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Arrays;

/**
 *
 * @author huyna3
 */
public class TestCalcPositionMap {

    public static class LocationObject {

        public LocationObject() {
            super();
        }

        public LocationObject(double latitude, double longitude, long time) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.time = time;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double latitude;
        public double longitude;
        public long time;
    }

    public static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    public static double zoom(int mapPx, int worldPx, double fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / 0.6931471805599453);
    }

    public static LocationObject calcCenterPosition(List<LocationObject> data) {
        Double north = Double.MIN_VALUE;
        Double south = Double.MAX_VALUE;
        Double west = Double.MIN_VALUE;
        Double east = Double.MAX_VALUE;

        for (LocationObject item : data) {
            north = item.latitude > north ? item.latitude : north;
            south = item.latitude < south ? item.latitude : south;
            west = item.longitude > west ? item.longitude : west;
            east = item.longitude < east ? item.longitude : east;
        }
        double latFraction = (latRad(north) - latRad(south)) / Math.PI;
        double lngDiff = west - east;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff / 360);
        double latZoom = zoom(400, 256, latFraction);
        double lngZoom = zoom(400, 256, lngFraction);
        return new LocationObject((north + south) / 2, (west + east) / 2, Math.round(Math.min(latZoom, lngZoom)));
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        long start = System.currentTimeMillis();

        String data = "[{\"latitude\":10.7579155,\"longitude\":106.745285,\"time\":1595935246169},{\"latitude\":10.757391,\"longitude\":106.74572,\"time\":1595935287383},{\"latitude\":10.757397,\"longitude\":106.745804,\"time\":1595935299953},{\"latitude\":10.755672,\"longitude\":106.74439,\"time\":1595935332000},{\"latitude\":10.7558155,\"longitude\":106.74421,\"time\":1595935335000},{\"latitude\":10.756297,\"longitude\":106.74351,\"time\":1595935349000},{\"latitude\":10.756288,\"longitude\":106.74344,\"time\":1595935352248},{\"latitude\":10.755603,\"longitude\":106.74275,\"time\":1595935363000},{\"latitude\":10.755282,\"longitude\":106.7425,\"time\":1595935367000},{\"latitude\":10.754401,\"longitude\":106.741844,\"time\":1595935379000},{\"latitude\":10.753504,\"longitude\":106.74102,\"time\":1595935391000},{\"latitude\":10.753348,\"longitude\":106.740746,\"time\":1595935394000},{\"latitude\":10.753205,\"longitude\":106.74018,\"time\":1595935400000},{\"latitude\":10.753137,\"longitude\":106.73884,\"time\":1595935414000},{\"latitude\":10.753133,\"longitude\":106.738396,\"time\":1595935418000},{\"latitude\":10.753092,\"longitude\":106.737,\"time\":1595935430000},{\"latitude\":10.753038,\"longitude\":106.73601,\"time\":1595935439000},{\"latitude\":10.753031,\"longitude\":106.735664,\"time\":1595935442000},{\"latitude\":10.752978,\"longitude\":106.734146,\"time\":1595935457000},{\"latitude\":10.752959,\"longitude\":106.73389,\"time\":1595935460000},{\"latitude\":10.752904,\"longitude\":106.73278,\"time\":1595935472000},{\"latitude\":10.752903,\"longitude\":106.732506,\"time\":1595935475000},{\"latitude\":10.752897,\"longitude\":106.73178,\"time\":1595935487000},{\"latitude\":10.752839,\"longitude\":106.7306,\"time\":1595935502000},{\"latitude\":10.752828,\"longitude\":106.73035,\"time\":1595935505000},{\"latitude\":10.752813,\"longitude\":106.72964,\"time\":1595935514000},{\"latitude\":10.752876,\"longitude\":106.728775,\"time\":1595935526000},{\"latitude\":10.752995,\"longitude\":106.7286,\"time\":1595935529000},{\"latitude\":10.753398,\"longitude\":106.72846,\"time\":1595935535000},{\"latitude\":10.754238,\"longitude\":106.728065,\"time\":1595935549000},{\"latitude\":10.754368,\"longitude\":106.72788,\"time\":1595935553000},{\"latitude\":10.754642,\"longitude\":106.727066,\"time\":1595935567000},{\"latitude\":10.754696,\"longitude\":106.726845,\"time\":1595935571000},{\"latitude\":10.754826,\"longitude\":106.726135,\"time\":1595935586000},{\"latitude\":10.7548485,\"longitude\":106.726036,\"time\":1595935588000},{\"latitude\":10.754964,\"longitude\":106.72559,\"time\":1595935598000},{\"latitude\":10.755074,\"longitude\":106.72508,\"time\":1595935610000},{\"latitude\":10.755099,\"longitude\":106.72495,\"time\":1595935613000},{\"latitude\":10.755292,\"longitude\":106.724174,\"time\":1595935628000},{\"latitude\":10.755319,\"longitude\":106.724075,\"time\":1595935631000},{\"latitude\":10.755326,\"longitude\":106.72399,\"time\":1595935633000},{\"latitude\":10.755333,\"longitude\":106.72396,\"time\":1595935634000},{\"latitude\":10.755407,\"longitude\":106.72347,\"time\":1595935647000},{\"latitude\":10.755599,\"longitude\":106.72258,\"time\":1595935661000},{\"latitude\":10.755651,\"longitude\":106.72228,\"time\":1595935665000},{\"latitude\":10.755765,\"longitude\":106.72175,\"time\":1595935674000},{\"latitude\":10.755804,\"longitude\":106.72161,\"time\":1595935677000},{\"latitude\":10.755812,\"longitude\":106.72158,\"time\":1595935678000},{\"latitude\":10.755823,\"longitude\":106.72153,\"time\":1595935680000},{\"latitude\":10.755802,\"longitude\":106.72126,\"time\":1595935694156},{\"latitude\":10.755748,\"longitude\":106.721214,\"time\":1595935696000},{\"latitude\":10.755213,\"longitude\":106.72101,\"time\":1595935710000},{\"latitude\":10.755094,\"longitude\":106.720955,\"time\":1595935714000},{\"latitude\":10.754725,\"longitude\":106.72077,\"time\":1595935723000},{\"latitude\":10.754635,\"longitude\":106.72071,\"time\":1595935727139},{\"latitude\":10.754155,\"longitude\":106.72038,\"time\":1595935738000},{\"latitude\":10.754016,\"longitude\":106.72026,\"time\":1595935741000},{\"latitude\":10.753968,\"longitude\":106.720215,\"time\":1595935742000},{\"latitude\":10.753231,\"longitude\":106.71965,\"time\":1595935756000},{\"latitude\":10.752647,\"longitude\":106.71884,\"time\":1595935771000},{\"latitude\":10.752051,\"longitude\":106.71787,\"time\":1595935786000},{\"latitude\":10.751908,\"longitude\":106.71741,\"time\":1595935792000},{\"latitude\":10.751843,\"longitude\":106.71664,\"time\":1595935804000},{\"latitude\":10.75184,\"longitude\":106.716515,\"time\":1595935807000},{\"latitude\":10.751844,\"longitude\":106.71569,\"time\":1595935822000},{\"latitude\":10.75184,\"longitude\":106.71551,\"time\":1595935825000},{\"latitude\":10.751841,\"longitude\":106.71462,\"time\":1595935840000},{\"latitude\":10.751844,\"longitude\":106.71404,\"time\":1595935851000},{\"latitude\":10.751841,\"longitude\":106.71399,\"time\":1595935852000},{\"latitude\":10.75183,\"longitude\":106.71387,\"time\":1595935855000},{\"latitude\":10.751801,\"longitude\":106.712944,\"time\":1595935870000},{\"latitude\":10.751804,\"longitude\":106.71263,\"time\":1595935876000},{\"latitude\":10.751802,\"longitude\":106.712585,\"time\":1595935878000},{\"latitude\":10.751788,\"longitude\":106.71204,\"time\":1595935891000},{\"latitude\":10.75179,\"longitude\":106.71117,\"time\":1595935906000},{\"latitude\":10.751797,\"longitude\":106.710266,\"time\":1595935921000},{\"latitude\":10.751781,\"longitude\":106.70936,\"time\":1595935935000},{\"latitude\":10.751777,\"longitude\":106.709076,\"time\":1595935939000},{\"latitude\":10.7517605,\"longitude\":106.70864,\"time\":1595935950495},{\"latitude\":10.751753,\"longitude\":106.70858,\"time\":1595935951000},{\"latitude\":10.7517395,\"longitude\":106.70799,\"time\":1595935962000},{\"latitude\":10.751737,\"longitude\":106.70794,\"time\":1595935963000},{\"latitude\":10.751722,\"longitude\":106.707535,\"time\":1595935973000},{\"latitude\":10.751732,\"longitude\":106.707466,\"time\":1595935974000},{\"latitude\":10.751746,\"longitude\":106.70658,\"time\":1595935988000},{\"latitude\":10.7517185,\"longitude\":106.70572,\"time\":1595936002000},{\"latitude\":10.751727,\"longitude\":106.70548,\"time\":1595936006000},{\"latitude\":10.751772,\"longitude\":106.70485,\"time\":1595936018000},{\"latitude\":10.751764,\"longitude\":106.70478,\"time\":1595936019000},{\"latitude\":10.751729,\"longitude\":106.703804,\"time\":1595936033000},{\"latitude\":10.7517185,\"longitude\":106.70358,\"time\":1595936036000},{\"latitude\":10.751718,\"longitude\":106.703354,\"time\":1595936039000},{\"latitude\":10.751709,\"longitude\":106.7031,\"time\":1595936044000},{\"latitude\":10.751728,\"longitude\":106.70224,\"time\":1595936058000},{\"latitude\":10.751717,\"longitude\":106.70197,\"time\":1595936062000},{\"latitude\":10.751692,\"longitude\":106.70129,\"time\":1595936071000},{\"latitude\":10.751682,\"longitude\":106.70104,\"time\":1595936074000},{\"latitude\":10.75171,\"longitude\":106.70018,\"time\":1595936086000},{\"latitude\":10.7516985,\"longitude\":106.699715,\"time\":1595936092000},{\"latitude\":10.751672,\"longitude\":106.698875,\"time\":1595936104000},{\"latitude\":10.751664,\"longitude\":106.698685,\"time\":1595936107000},{\"latitude\":10.751669,\"longitude\":106.69831,\"time\":1595936113000},{\"latitude\":10.751665,\"longitude\":106.69825,\"time\":1595936114000},{\"latitude\":10.751688,\"longitude\":106.69721,\"time\":1595936128000},{\"latitude\":10.751654,\"longitude\":106.69611,\"time\":1595936143000},{\"latitude\":10.751655,\"longitude\":106.69591,\"time\":1595936146000},{\"latitude\":10.751646,\"longitude\":106.69567,\"time\":1595936149000},{\"latitude\":10.751659,\"longitude\":106.69458,\"time\":1595936164000},{\"latitude\":10.751642,\"longitude\":106.69434,\"time\":1595936167000},{\"latitude\":10.751448,\"longitude\":106.69321,\"time\":1595936182000},{\"latitude\":10.751362,\"longitude\":106.6928,\"time\":1595936188000},{\"latitude\":10.751213,\"longitude\":106.69188,\"time\":1595936200000},{\"latitude\":10.751201,\"longitude\":106.6918,\"time\":1595936201000},{\"latitude\":10.751015,\"longitude\":106.6907,\"time\":1595936215000},{\"latitude\":10.750901,\"longitude\":106.69,\"time\":1595936227000},{\"latitude\":10.750718,\"longitude\":106.68911,\"time\":1595936239000},{\"latitude\":10.750707,\"longitude\":106.68903,\"time\":1595936240000},{\"latitude\":10.750567,\"longitude\":106.68836,\"time\":1595936251000},{\"latitude\":10.750779,\"longitude\":106.68771,\"time\":1595936266000},{\"latitude\":10.750596,\"longitude\":106.68685,\"time\":1595936280000},{\"latitude\":10.750526,\"longitude\":106.68659,\"time\":1595936284000},{\"latitude\":10.750319,\"longitude\":106.686035,\"time\":1595936296000},{\"latitude\":10.750306,\"longitude\":106.68601,\"time\":1595936297000},{\"latitude\":10.75029,\"longitude\":106.68597,\"time\":1595936299000},{\"latitude\":10.750239,\"longitude\":106.68581,\"time\":1595936313000},{\"latitude\":10.750235,\"longitude\":106.68577,\"time\":1595936317000},{\"latitude\":10.750227,\"longitude\":106.68571,\"time\":1595936319000},{\"latitude\":10.750093,\"longitude\":106.68542,\"time\":1595936332000},{\"latitude\":10.750074,\"longitude\":106.685326,\"time\":1595936335000},{\"latitude\":10.750066,\"longitude\":106.6853,\"time\":1595936336000},{\"latitude\":10.750056,\"longitude\":106.68529,\"time\":1595936338000},{\"latitude\":10.7498665,\"longitude\":106.684906,\"time\":1595936348000},{\"latitude\":10.749798,\"longitude\":106.68478,\"time\":1595936350000},{\"latitude\":10.749732,\"longitude\":106.68461,\"time\":1595936353000},{\"latitude\":10.749713,\"longitude\":106.68456,\"time\":1595936354000},{\"latitude\":10.749362,\"longitude\":106.68387,\"time\":1595936368000},{\"latitude\":10.749003,\"longitude\":106.68321,\"time\":1595936383000},{\"latitude\":10.748775,\"longitude\":106.68274,\"time\":1595936398000},{\"latitude\":10.7487755,\"longitude\":106.68272,\"time\":1595936400657},{\"latitude\":10.748394,\"longitude\":106.68208,\"time\":1595936413000},{\"latitude\":10.748317,\"longitude\":106.68195,\"time\":1595936416000},{\"latitude\":10.747976,\"longitude\":106.6813,\"time\":1595936431000},{\"latitude\":10.747949,\"longitude\":106.68124,\"time\":1595936433000},{\"latitude\":10.74783,\"longitude\":106.681015,\"time\":1595936443715},{\"latitude\":10.747819,\"longitude\":106.680984,\"time\":1595936445072},{\"latitude\":10.747813,\"longitude\":106.680954,\"time\":1595936446000},{\"latitude\":10.747818,\"longitude\":106.680916,\"time\":1595936452000},{\"latitude\":10.747403,\"longitude\":106.68019,\"time\":1595936466000},{\"latitude\":10.747277,\"longitude\":106.679955,\"time\":1595936470000},{\"latitude\":10.746918,\"longitude\":106.6793,\"time\":1595936482000},{\"latitude\":10.74653,\"longitude\":106.678566,\"time\":1595936494000},{\"latitude\":10.746079,\"longitude\":106.67775,\"time\":1595936509000},{\"latitude\":10.74603,\"longitude\":106.67765,\"time\":1595936511000},{\"latitude\":10.74587,\"longitude\":106.677376,\"time\":1595936521000},{\"latitude\":10.74587,\"longitude\":106.677345,\"time\":1595936524000},{\"latitude\":10.745844,\"longitude\":106.67729,\"time\":1595936530000},{\"latitude\":10.7457485,\"longitude\":106.67714,\"time\":1595936539000},{\"latitude\":10.745727,\"longitude\":106.67709,\"time\":1595936541000},{\"latitude\":10.745687,\"longitude\":106.677025,\"time\":1595936544000},{\"latitude\":10.745668,\"longitude\":106.677,\"time\":1595936545000},{\"latitude\":10.745278,\"longitude\":106.67634,\"time\":1595936559000},{\"latitude\":10.745124,\"longitude\":106.67599,\"time\":1595936568000},{\"latitude\":10.7448225,\"longitude\":106.67538,\"time\":1595936583000},{\"latitude\":10.744544,\"longitude\":106.6748,\"time\":1595936595000},{\"latitude\":10.744524,\"longitude\":106.67476,\"time\":1595936596000},{\"latitude\":10.744329,\"longitude\":106.67463,\"time\":1595936610000},{\"latitude\":10.744251,\"longitude\":106.67466,\"time\":1595936612000},{\"latitude\":10.744125,\"longitude\":106.67471,\"time\":1595936617000},{\"latitude\":10.744103,\"longitude\":106.67472,\"time\":1595936619000},{\"latitude\":10.7441025,\"longitude\":106.67475,\"time\":1595936622000},{\"latitude\":10.744146,\"longitude\":106.674995,\"time\":1595936635000},{\"latitude\":10.744149,\"longitude\":106.67502,\"time\":1595936641000},{\"latitude\":10.744134,\"longitude\":106.67499,\"time\":1595936649000},{\"latitude\":10.744006,\"longitude\":106.67505,\"time\":1595936664000},{\"latitude\":10.743945,\"longitude\":106.67505,\"time\":1595936667000},{\"latitude\":10.743851,\"longitude\":106.674995,\"time\":1595936679000},{\"latitude\":10.7438965,\"longitude\":106.67505,\"time\":1595936682000},{\"latitude\":10.743887,\"longitude\":106.675064,\"time\":1595936685000},{\"latitude\":10.744055,\"longitude\":106.674995,\"time\":1595936729041},{\"latitude\":10.744095,\"longitude\":106.675,\"time\":1595936732000},{\"latitude\":10.744099,\"longitude\":106.67497,\"time\":1595936771000},{\"latitude\":10.744088,\"longitude\":106.67496,\"time\":1595936791000},{\"latitude\":10.744104,\"longitude\":106.67497,\"time\":1595936804000},{\"latitude\":10.744117,\"longitude\":106.67497,\"time\":1595936808000},{\"latitude\":10.744093,\"longitude\":106.67497,\"time\":1595936825000},{\"latitude\":10.744152,\"longitude\":106.67498,\"time\":1595936837000},{\"latitude\":10.744671,\"longitude\":106.67506,\"time\":1595936849000},{\"latitude\":10.744723,\"longitude\":106.675064,\"time\":1595936850000},{\"latitude\":10.744052,\"longitude\":106.674995,\"time\":1595936884318}]";
        List<LocationObject> participantJsonList = ObjectUtils.fromJsonString(data, new TypeReference<List<LocationObject>>() {
        });
        List<LocationObject> l1 = participantJsonList.subList(0, 10);
        List<LocationObject> l2 = participantJsonList.subList(10, participantJsonList.size());
        List<List<LocationObject>> data1 = new ArrayList<>();
        data1.add(l1);
        data1.add(l2);
//        LocationObject result = calcCenterPosition(participantJsonList);
//        long end = System.currentTimeMillis();
//        System.out.println("result:" + result.latitude + "|" + result.longitude + "| zoom:" + result.time + "|" + (end - start));
        String d = getRouteImage(data1);
        System.out.println("d:"+ d);
        System.exit(0);

    }
    public static String getRouteImage(List<List<LocationObject>> dataLocation) throws IOException {
        List<LocationObject> mergeData = new ArrayList<>();
        dataLocation.stream().forEach(item -> mergeData.addAll(item));
        StringBuilder params = new StringBuilder("color:0xf0602dFF|weight:5");
        mergeData.stream().forEach(item->params.append("|").append(item.getLatitude()).append(",").append(item.getLongitude()));
        String query =URLEncoder.encode(params.toString(), StandardCharsets.UTF_8.name());
        query = "size=400x400&path="+ query +"&key=AIzaSyCxxnmYcixQiUd_8VONtIIFyGwEppgOan0";
        HttpGet httpPost = new HttpGet("https://maps.googleapis.com/maps/api/staticmap?"+query);
        String result ;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            result = EntityUtils.toString(response.getEntity());
        }
        return result;
    }
}

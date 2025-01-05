package com.security.learn;

import java.util.*;

public class MainApp {
    static class GPSObject {
        private String officerId;
        private Integer lastLocation;
        private Integer longLocation;
        private Integer time;

        public GPSObject() {

        }

        public GPSObject(String officerId, Integer lastLocation, Integer longLocation, Integer time) {
            this.officerId = officerId;
            this.lastLocation = lastLocation;
            this.longLocation = longLocation;
            this.time = time;
        }

        public Integer getLastLocation() {
            return lastLocation;
        }

        public void setLastLocation(Integer lastLocation) {
            this.lastLocation = lastLocation;
        }

        public Integer getLongLocation() {
            return longLocation;
        }

        public void setLongLocation(Integer longLocation) {
            this.longLocation = longLocation;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public String getOfficerId() {
            return officerId;
        }

        public void setOfficerId(String officerId) {
            this.officerId = officerId;
        }

        @Override
        public String toString() {
            return "GPSObject{" +
                    "officerId=" + officerId +
                    ", lastLocation='" + lastLocation + '\'' +
                    ", longLocation='" + longLocation + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    public static void main(String[] args) {
        // Data example
        Map<String, List<GPSObject>> data = new HashMap<>();
        List<GPSObject> gpsOfficer1 = new ArrayList<>();
        gpsOfficer1.add(new GPSObject("officer1", 1, 4, 1));
        gpsOfficer1.add(new GPSObject("officer1", 1, 5, 5));
        gpsOfficer1.add(new GPSObject("officer1", 1, 6, 10));
        data.put("officer1", gpsOfficer1);

        List<GPSObject> gpsOfficer2 = new ArrayList<>();
        gpsOfficer2.add(new GPSObject("officer2", 2, 3, 8));
        data.put("officer2", gpsOfficer2);


        String inputOfficerId = "officer1";
        Integer inputTime = 9;

        System.out.println(getGPSNear(data, inputOfficerId, inputTime));
    }

    public static GPSObject getGPSNear(Map<String, List<GPSObject>> data, String officerId, Integer time) {
        if (data.get(officerId) == null || data.get(officerId) == null || data.get(officerId).isEmpty()) {
            return null;
        }

        // Sorted listGps
        List<GPSObject> listGps = data.get(officerId);
        GPSObject gpsNear = new GPSObject();
        int timeAbs = Integer.MAX_VALUE;

//        for (GPSObject gpsObject : listGps) {
//            if (Math.abs(time - gpsObject.getTime()) <= timeAbs && gpsObject.getTime() - time > 0) {
//                gpsNear = gpsObject;
//                timeAbs = Math.abs(time - gpsObject.getTime());
//            }
//        }

//        int mid = listGps.size() / 2;
//        gpsNear = listGps.get(mid);
//        if (listGps.get(mid).getTime() < time) {
//            GPSObject gpsObjectMid = listGps.get(mid);
//            GPSObject gpsObjectLeft = listGps.get(mid - 1);
//            if (Math.abs(gpsObjectMid.getTime() - time) < timeAbs) {
//                gpsNear =
//            }
//            return Math.abs(gpsObjectMid.getTime() - time) <
//        }
//        if (listGps.get(mid).getTime() > time) {
//
//        }

        return gpsNear;
    }

}

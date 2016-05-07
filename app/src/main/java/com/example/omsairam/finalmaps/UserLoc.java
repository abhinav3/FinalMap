package com.example.omsairam.finalmaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by omsairam on 4/12/2016.
 */
public class UserLoc {
    public LatLng getSelfLoc() {
        return selfLoc;
    }

    public void setSelfLoc(LatLng selfLoc) {
        this.selfLoc = selfLoc;
    }

    private   LatLng selfLoc= new LatLng(26.190459,91.699407);
}

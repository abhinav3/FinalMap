package com.example.omsairam.finalmaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by omsairam on 4/12/2016.
 */
public class FrndLoc {
    public LatLng getFrndLoc() {
        return frndLoc;
    }

    public void setFrndLoc(LatLng frndLoc) {
        this.frndLoc = frndLoc;
    }

    private LatLng frndLoc=new LatLng(26.188671,91.696207) ;

}

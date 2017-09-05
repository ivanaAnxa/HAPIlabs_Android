package com.anxa.hapilabs.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aprilanxa on 25/07/2016.
 */
public class Water {

    public String number_glasses;
    public String getNumber_glasses() {
        return number_glasses;
    }

    public void setNumber_glasses(String number_glasses) {
        this.number_glasses = number_glasses;
    }
    public String getWater_id() {
        return water_id;
    }

    public void setWater_id(String water_id) {
        this.water_id = water_id;
    }
    public Date getWater_datetime() {
        return water_datetime;
    }

    public void setWater_datetime(Date water_datetime) {
        this.water_datetime = water_datetime;
    }
    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public String water_id;
    public Date water_datetime;
    public boolean isChecked;

    public List<Comment> comments;
    ;
    public List<HAPI4U> hapi4Us;
}

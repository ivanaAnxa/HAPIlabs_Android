package com.anxa.hapilabs.models;

import java.util.Date;

/**
 * Created by aprilanxa on 11/10/2016.
 */

public class Weight {
    public String activity_id;
    public String device_name;
    public String graph_values_type;
    public Date start_datetime;
    public Date end_datetime;
    public Boolean isDeleted;
    public double currentWeightGrams;
    public double BMI;
}

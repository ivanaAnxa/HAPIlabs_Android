package com.anxa.hapilabs.models;

import java.util.Date;
import java.util.List;

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
    public double BodyWaterRatio;
    public double BodyFatRatio;
    public double LeanMassRatio;
    public double BoneWeightGrams;


    public List<Comment> comments;
    public List<HAPI4U> hapi4Us;
}

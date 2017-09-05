package com.anxa.hapilabs.models;

import java.util.Date;
import java.util.List;

/**
 * Created by angelaanxa on 8/5/2016.
 */
public class HapiMoment {
    public static final byte HAPIMOMENTSTATE_ADD = 40;
    public static final byte HAPIMOMENTSTATE_EDIT = 41;
    public static final byte HAPIMOMENTSTATE_DELETE = 42;
    public static final byte HAPIMOMENTSTATE_VIEW = 43;
    public int moodValue;
    public int hungerValue;
    public String description;
    public Date mood_datetime;
    public int mood_id;
    public boolean isChecked;
    public String command;
    public String status;
    public String location;

    public List<Photo> photos;

    public List<Comment> comments;
    public List<HAPI4U> hapi4Us;
}

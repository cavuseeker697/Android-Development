package com.example.finalproject;

import java.io.Serializable;

// implements Class Section Model, sections are a ListArray within the Class Course Model
public class ClassSectionModel
    implements Serializable {
    private String sectionCode;
    private String itemNbr;
    private String days;
    private String timeLocation;
    private String instructor;
    private String labFee;



public ClassSectionModel (String sectionCode,
                          String itemNbr,
                          String days,
                          String timeLocation,
                          String instructor,
                          String labFee) {
    this.sectionCode = sectionCode;
    this.itemNbr = itemNbr;
    this.days = days;
    this.timeLocation = timeLocation;
    this.instructor = instructor;
    this.labFee = labFee;
}
public ClassSectionModel (ClassSectionModel obj) {
    this.sectionCode = obj.sectionCode;
    this.itemNbr = obj.itemNbr;
    this.days = obj.days;
    this.timeLocation = obj.timeLocation;
    this.instructor = obj.instructor;
    this.labFee = obj.labFee;
}
public String getSectionCode () {return this.sectionCode;}
public String getItemNbr () {return this.itemNbr;}
public String getDays () {return this.days;}
public String getTimeLocation () {return this.timeLocation;}
public String getInstructor () {return this.instructor;};
public String getLabFee () {return this.labFee;}
}
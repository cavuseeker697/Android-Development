package com.example.finalproject;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
// Implements Class ClassEntryModel. One object per class, with an array of ClassSectionModel
public class ClassEntryModel
    implements Serializable {

    public String classDept;
    public String classCourseNbr;
    public String classTitle;
    public String classDesc;
    public String classCredits;
    public String preCoRequisites;
    public ArrayList<ClassSectionModel> classSections;


    public ClassEntryModel (String classDept,
                            String classCourseNbr,
                            String classTitle,
                            String classDesc,
                            String classCredits,
                            String preCoRequisites) {
        this.classDept = classDept;
        this.classCourseNbr = classCourseNbr;
        this.classTitle = classTitle;
        this.classDesc = classDesc;
        this.classCredits = classCredits;
        this.preCoRequisites = preCoRequisites;
        this.classSections = new ArrayList<>();
    }
    public ClassEntryModel(ClassEntryModel obj) {
        this.classDept = obj.classDept;
        this.classCourseNbr = obj.classCourseNbr;
        this.classTitle = obj.classTitle;
        this.classDesc = obj.classDesc;
        this.classCredits = obj.classCredits;
        this.preCoRequisites = obj.preCoRequisites;
        this.classSections = new ArrayList<>();
        for (int i = 0; i< obj.classSections.size(); i++) {
            this.classSections.add(new ClassSectionModel (obj.classSections.get(i)));
        }
    }
    public String getClassDept() {return this.classDept;}
    public String getClassCourseNbr() {return this.classCourseNbr;}
    public String getClassTitle() {return this.classTitle;}
    public String getClassDesc() {return this.classDesc;}
    public String getClassCredits() {return this.classCredits;}
    public String getPreCoRequisites () {return this.preCoRequisites;}
    public ClassSectionModel getSectionByIndex (int idx) {
        if (idx > this.classSections.size()) {
            throw new IllegalArgumentException("Illegal Argument: index is > # of section objects");
        }

        return classSections.get(idx);
    }
    public void addSection (ClassSectionModel sectionEntry) {
        this.classSections.add(sectionEntry);
    }
    public void clearSections () {
        this.classSections.clear();
    }
    public int getNbrSections () {return this.classSections.size();}
}

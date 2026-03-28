package com.cvoptimizer.cv_backend.interview.dto;

public class TagWeaknessDto {

    private String tag;
    private int mistakes;

    public TagWeaknessDto() {}

    public TagWeaknessDto(String tag, int mistakes) {
        this.tag = tag;
        this.mistakes = mistakes;
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public int getMistakes() { return mistakes; }
    public void setMistakes(int mistakes) { this.mistakes = mistakes; }
}

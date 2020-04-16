package com.xuwanjin.inchoate.model;

public enum ArticleCategorySection {
    THE_WORLD_THIS_WEEK("The world this week"),
    LEADERS("Leaders"),
    LETTERS("Letters"),
    BRIEFING("Briefing"),
    UNITED_STATES("United States"),
    THE_AMERICAS("The Americas"),
    ASIA("Asia"),
    CHINA("China"),
    MIDDLE_EAST_AND_AFRICA("Middle east and africa"),
    EUROPE("Europe"),
    BRITAIN("Britain"),
    INTERNATIONAL("International"),
    BUSINESS("Business"),
    FINANCE_AND_ECONOMICS("Finance and economics"),
    SCIENCE_AND_TECHNOLOGY("Science and technology"),
    BOOKS_AND_ARTS("Books and arts"),
    ECONOMICS_AND_FINANCIAL_INDICATORS("Economics and financial indicators"),
    GRAPHIC_DETAIL("Graphic detail"),
    OBITUNARY("Obitunary");
    private String sectionName;
    private ArticleCategorySection(String sectionName){
        this.sectionName = sectionName;
    }
    public String getName(){
        return sectionName;
    }
}

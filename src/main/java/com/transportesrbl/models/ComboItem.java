package com.transportesrbl.models;

public class ComboItem {
    private final Integer id;
    private final String label;

    public ComboItem(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label != null ? label : "";
    }
}

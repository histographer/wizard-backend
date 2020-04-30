package no.digipat.wizard.models.results;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AnalysisValue {
    private String name;
    private Float val;

    public String getName() {
        return name;
    }

    public AnalysisValue setName(String name) {
        this.name = name;
        return this;
    }

    public Float getVal() {
        return val;
    }

    public AnalysisValue setVal(Float val) {
        this.val = val;
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisValue{"
                + "name='" + name + "'"
                + ", val=" + val
                + "}";
    }
}

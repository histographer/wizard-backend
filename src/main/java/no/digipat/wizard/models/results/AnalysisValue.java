package no.digipat.wizard.models.results;

import lombok.EqualsAndHashCode;

/**
 * A value in an analysis component.
 * 
 * @author Kent Are Torvik
 * 
 * @see AnalysisComponent
 * 
 */
@EqualsAndHashCode
public class AnalysisValue {
    private String name;
    private Float val; // TODO val is a keyword in a later Java version

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this value, e.g. "mean" or "std".
     * 
     * @param name the name
     * 
     * @return this
     */
    public AnalysisValue setName(String name) {
        this.name = name;
        return this;
    }

    public Float getVal() {
        return val;
    }

    /**
     * Sets the value.
     * 
     * @param val the value
     * 
     * @return this
     */
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

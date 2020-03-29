package no.digipat.wizard.models;

import java.util.Map;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
public class Result {
    public String getType() {
        return type;
    }

    public Result setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public Result setValues(Map<String, Integer> values) {
        this.values = values;
        return this;
    }

    private String type;
    private Map<String, Integer> values;

}

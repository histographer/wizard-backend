package no.digipat.wizard.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * A representation of a single result. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
public class Result {
    @NotBlank(message = "type can not be null or empty")
    private String type;
    @NotEmpty(message = "values can not be null or empty")
    private Map<String, Integer> values;

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


}

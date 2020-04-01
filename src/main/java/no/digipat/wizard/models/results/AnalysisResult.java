package no.digipat.wizard.models.results;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * A representation of a single result. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
@EqualsAndHashCode
public class AnalysisResult {
    @NotBlank(message = "Result: Type can not be null or empty")
    private String type;
    @NotEmpty(message = "Result: Values can not be null or empty")
    private Map<String, Integer> values;

    public String getType() {
        return type;
    }

    public AnalysisResult setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public AnalysisResult setValues(Map<String, Integer> values) {
        this.values = values;
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisResult{" +
                "type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}

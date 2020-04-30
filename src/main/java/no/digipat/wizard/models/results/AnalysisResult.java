package no.digipat.wizard.models.results;

import javax.validation.constraints.NotEmpty;

import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * A representation of a single result. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
@EqualsAndHashCode
public class AnalysisResult {
    @NotEmpty(message = "Result: Values can not be null or empty")
    private List<AnalysisComponent> components;
    private String name;

    public List<AnalysisComponent> getComponents() {
        return components;
    }

    public AnalysisResult setComponents(List<AnalysisComponent> components) {
        this.components = components;
        return this;
    }

    public String getName() {
        return name;
    }

    public AnalysisResult setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisResult{"
                + "components=" + components
                + ", name='" + name + "'"
                + "}";
    }
}

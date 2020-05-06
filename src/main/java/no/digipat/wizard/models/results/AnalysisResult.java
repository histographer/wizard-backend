package no.digipat.wizard.models.results;

import javax.validation.constraints.NotEmpty;

import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * A representation of a single result, which can have several components.
 * All the setters of this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 * 
 * @see Results
 *
 */
@EqualsAndHashCode
public class AnalysisResult {
    @NotEmpty(message = "Result: Values can not be null or empty")
    private List<AnalysisComponent> components;
    private String name;

    /**
     * Gets the analysis components for this analysis result.
     * 
     * @return the components
     */
    public List<AnalysisComponent> getComponents() {
        return components;
    }

    public AnalysisResult setComponents(List<AnalysisComponent> components) {
        this.components = components;
        return this;
    }

    /**
     * Gets the name of this analysis result, e.g. "he" or "hsv".
     * 
     * @return the name
     */
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

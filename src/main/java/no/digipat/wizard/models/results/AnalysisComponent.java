package no.digipat.wizard.models.results;

import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * A component of an individual analysis result.
 * 
 * @author Kent Are Torvik
 * 
 * @see AnalysisResult
 *
 */
@EqualsAndHashCode
public class AnalysisComponent {
    private String name;

    private List<AnalysisValue> components;

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this analysis component, e.g. "H" or "E".
     * 
     * @param name the name
     * 
     * @return this
     */
    public AnalysisComponent setName(String name) {
        this.name = name;
        return this;
    }

    public List<AnalysisValue> getComponents() {
        return components;
    }

    /**
     * Sets the analysis values comprising this analysis component.
     * 
     * @param components the analysis values
     * 
     * @return this
     */
    public AnalysisComponent setComponents(List<AnalysisValue> components) {
        // Calling it "components" is rather confusing since they're analysis values
        // and not instances of AnalysisComponent, but renaming the field
        // could cause backwards incompatibility because of how the JSON is generated
        this.components = components;
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisComponent{"
                + "name='" + name + "'"
                + ", components=" + components
                + "}";
    }
}

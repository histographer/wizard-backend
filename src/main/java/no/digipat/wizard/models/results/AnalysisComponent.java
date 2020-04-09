package no.digipat.wizard.models.results;

import java.util.List;

public class AnalysisComponent {
    private String name;

    private List<AnalysisValue> components;

    public String getName() {
        return name;
    }

    public AnalysisComponent setName(String name) {
        this.name = name;
        return this;
    }

    public List<AnalysisValue> getComponents() {
        return components;
    }

    public AnalysisComponent setComponents(List<AnalysisValue> components) {
        this.components = components;
        return this;
    }
}

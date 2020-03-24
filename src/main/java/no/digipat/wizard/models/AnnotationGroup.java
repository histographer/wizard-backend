package no.digipat.wizard.models;

import java.util.List;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class AnnotationGroup {
    
    private String groupId;
    private List<Long> annotationIds;
    
    public String getGroupId() {
        return groupId;
    }
    
    public AnnotationGroup setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }
    
    public List<Long> getAnnotationIds() {
        return annotationIds;
    }
    
    public AnnotationGroup setAnnotationIds(List<Long> annotationIds) {
        this.annotationIds = annotationIds;
        return this;
    }
    
}

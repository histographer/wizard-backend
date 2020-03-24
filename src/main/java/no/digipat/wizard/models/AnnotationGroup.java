package no.digipat.wizard.models;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class AnnotationGroup {
    
    private String groupId;
    private long[] annotationIds;
    
    public String getGroupId() {
        return groupId;
    }
    
    public AnnotationGroup setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }
    
    public long[] getAnnotationIds() {
        return annotationIds;
    }
    
    public AnnotationGroup setAnnotationIds(long[] annotationIds) {
        this.annotationIds = annotationIds;
        return this;
    }
    
}

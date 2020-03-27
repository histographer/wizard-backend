package no.digipat.wizard.models;

import java.util.Date;
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
    private Date creationDate;
    private String name;
    private Long projectId;
    
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
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public AnnotationGroup setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public AnnotationGroup setName(String name) {
        this.name = name;
        return this;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public AnnotationGroup setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }
    
}

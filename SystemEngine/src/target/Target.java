package target;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Target implements Serializable {
    static public enum TargetProperty { LEAF, MIDDLE, ROOT, INDEPENDENT }
    static public enum Connection { REQUIRED_FOR, DEPENDS_ON }

    private Set<Target> dependsOnTargets;
    private Set<Target> requireForTargets;
    private TargetProperty targetProperty;
    private String targetName;
    private String extraInformation;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    public Target() {
        this.dependsOnTargets = new HashSet<>();
        this.requireForTargets = new HashSet<>();
        this.targetProperty = TargetProperty.INDEPENDENT;
    }

    public Set<Target> getDependsOnTargets() {
        return dependsOnTargets;
    }

    public void setDependsOnTargets(Set<Target> dependsOnTargets) {
        this.dependsOnTargets = dependsOnTargets;
    }

    public Set<Target> getRequireForTargets() {
        return requireForTargets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Target target = (Target) o;
        return Objects.equals(targetName, target.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName);
    }

    public void setRequireForTargets(Set<Target> requireForTargets) {
        this.requireForTargets = requireForTargets;
    }

    public TargetProperty getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(TargetProperty targetProperty) {
        this.targetProperty = targetProperty;
    }

    public void addToDependsOn(Target dependsOn) { dependsOnTargets.add(dependsOn); }

    public void addToRequiredFor(Target requiredFor) { requireForTargets.add(requiredFor); }
}
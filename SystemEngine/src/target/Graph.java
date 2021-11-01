package target;

import java.util.HashMap;
import java.util.Map;

public class Graph extends Target{
    private Map<String, Target> graphTargets;

    public Map<String, Target> getGraphTargets() {
        return graphTargets;
    }

    public Graph() {
        this.graphTargets = new HashMap<>();
    }
}

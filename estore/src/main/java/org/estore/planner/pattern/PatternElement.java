package org.estore.planner.pattern;

import java.util.ArrayList;
import java.util.List;

public class PatternElement extends Pattern {
    private List<Pattern> elements;

    public PatternElement(int id) {
        this.id = id;
        elements = new ArrayList<Pattern>();
    }

    public void addElement(Pattern element) throws IllegalArgumentException {
        if ((element instanceof NodePattern && elements.size() % 2 != 0)
                || (element instanceof RelationPattern && elements.size() % 2 == 0)) {
            throw new IllegalArgumentException();
        }
        elements.add(element);
    }

    public List<Pattern> getElements() {
        return elements;
    }

    public List<NodePattern> getNodePatterns() {
        List<NodePattern> nodePatterns = new ArrayList<NodePattern>();

        for (int j = 0; j < elements.size(); j += 2) {
            nodePatterns.add((NodePattern) elements.get(j));
        }
        return nodePatterns;
    }

    public List<RelationPattern> getRelationPatterns() {
        List<RelationPattern> relationPatterns = new ArrayList<RelationPattern>();

        for (int j = 1; j < elements.size(); j += 2) {
            relationPatterns.add((RelationPattern) elements.get(j));
        }
        return relationPatterns;
    }

    public boolean isNodeOnlyPattern() {
        return elements.size() == 1;
    }

    public boolean isMultiLengthRelationPattern() {
        return elements.size() > 3;
    }

    public NodePattern getNodePattern(int idx) {
        if (idx < 0 || (idx * 2) >= elements.size()) {
            throw new IllegalArgumentException();
        }
        return (NodePattern) elements.get(idx * 2);
    }

    public RelationPattern getRelationPattern(int idx) {
        if (idx < 0 || ((idx * 2) + 1) >= elements.size()) {
            throw new IllegalArgumentException();
        }
        return (RelationPattern) elements.get((idx * 2) + 1);
    }
}

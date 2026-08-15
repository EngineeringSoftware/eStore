package org.estore.planner.util;

import com.jakewharton.fliptables.FlipTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Table extends HashMap<String, ArrayList<Object>> {
    public Table(List<String> variables) {
        for (String variable : variables) {
            super.put(variable, new ArrayList<Object>());
        }
    }

    public Table() {}

    public Table(Table other) {
        super(other);
    }

    public void putEntry(HashMap<String, Object> mapEntry) {
        for (String variable : keySet()) {
            get(variable).add(mapEntry.get(variable));
        }
    }

    public int getSize() {
        for (String key : this.keySet()) {
            return this.get(key).size();
        }
        return 0;
    }

    public void changeColumnName(String oldName, String newName) {
        this.put(newName, this.remove(oldName));
    }

    public Table selectColumns(List<String> variables) {
        Table result = new Table(this);
        List<String> variableRemove = new ArrayList<String>();

        for (String variable : result.keySet()) {
            if (!variables.contains(variable)) {
                variableRemove.add(variable);
            }
        }

        for (String variable : variableRemove) {
            result.remove(variable);
        }

        return result;
    }

    public HashMap<String, Object> getAtIndex(int j) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        for (String key : this.keySet()) {
            map.put(key, this.get(key).get(j));
        }
        return map;
    }

    public Table getAtIndexTable(int j) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Table result = new Table(new ArrayList<String>(this.keySet()));

        for (String key : this.keySet()) {
            map.put(key, this.get(key).get(j));
        }
        result.putEntry(map);
        return result;
    }

    public List<Object> getDistinctVariable(String variable) {
        HashSet<Object> distinctSet = new HashSet<Object>();

        for (Object obj : this.get(variable)) {
            distinctSet.add(obj);
        }

        return new ArrayList<Object>(distinctSet);
    }

    public Table join(Table other, String joinVariable) {
        if (!(this.containsKey(joinVariable) && other.containsKey(joinVariable))) {
            return null;
        }

        HashSet<String> joinedKeys = new HashSet<String>(this.keySet());
        joinedKeys.addAll(other.keySet());

        Table joinedTable = new Table(new ArrayList<String>(joinedKeys));

        // join variable -> corresponding row
        HashMap<Object, List<HashMap<String, Object>>> thisMap = new HashMap<>();
        for (int k = 0; k < this.getSize(); k++) {
            Object joinValue = this.get(joinVariable).get(k);
            if (joinValue != null) {
                if (!thisMap.containsKey(joinValue)) {
                    thisMap.put(joinValue, new ArrayList<>());
                }
                thisMap.get(joinValue).add(this.getAtIndex(k));
            }
        }

        try {
            for (int i = 0; i < other.getSize(); i++) {
                Object joinValue = other.get(joinVariable).get(i);
                if (joinValue != null && thisMap.containsKey(joinValue)) {
                    List<HashMap<String, Object>> matchingRows = thisMap.get(joinValue);
                    for (HashMap<String, Object> row : matchingRows) {
                        HashMap<String, Object> joinedMapEntry = new HashMap<>(row);
                        joinedMapEntry.putAll(other.getAtIndex(i));
                        joinedTable.putEntry(joinedMapEntry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return joinedTable;
    }

    public Table cartesianJoin(Table other) {
        HashSet<String> joinedKeys = new HashSet<String>(this.keySet());
        joinedKeys.addAll(other.keySet());

        Table joinedTable = new Table(new ArrayList<String>(joinedKeys));

        for (int k = 0; k < this.getSize(); k++) {
            for (int j = 0; j < other.getSize(); j++) {
                HashMap<String, Object> joinedMapEntry = this.getAtIndex(k);
                joinedMapEntry.putAll(other.getAtIndex(j));
                joinedTable.putEntry(joinedMapEntry);
            }
        }

        return joinedTable;
    }

    public Table union(Table other, List<String> unionVariables) {

        Table unionTable = new Table(this);

        for (String variable : unionTable.keySet()) {
            if (!unionVariables.contains(variable)) {
                unionTable.remove(variable);
            }
        }

        OUTER:
        for (int j = 0; j < other.getSize(); j++) {
            HashMap<String, Object> otherValue = other.getAtIndex(j);
            for (int k = 0; k < unionTable.getSize(); k++) {
                HashMap<String, Object> thisValue = unionTable.getAtIndex(k);
                boolean isNotDistinct = true;

                for (String variable : unionVariables) {
                    isNotDistinct &= thisValue.get(variable).equals(otherValue.get(variable));
                }
                if (isNotDistinct) continue OUTER;
            }
            unionTable.putEntry(otherValue);
        }

        return unionTable;
    }

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        String[][] data = new String[this.getSize()][this.size()];
        String[] headers = this.keySet().toArray(new String[0]);

        for (int j = 0; j < this.getSize(); j++) {
            HashMap<String, Object> item = this.getAtIndex(j);
            for (int k = 0; k < headers.length; k++) {
                data[j][k] = item.get(headers[k]) + "";
            }
        }
        return FlipTable.of(headers, data);
    }
}

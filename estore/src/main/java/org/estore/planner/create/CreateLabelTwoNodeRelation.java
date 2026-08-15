package org.estore.planner.create;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.ClassHelper;
import org.estore.planner.util.Table;
import org.estore.planner.util.enums.RelationDirection;

public class CreateLabelTwoNodeRelation extends LogicalPlan {

    private Estore estore;
    private String referrerVariable;
    private String referrerLabel;
    private String refereeVariable;
    private String refereeLabel;
    private String relationVariable;
    private String relationLabel;

    public Estore getEstore() {
        return estore;
    }

    public CreateLabelTwoNodeRelation(
            int id, Estore estore, NodePattern left, RelationPattern relation, NodePattern right) {
        this.name = "CreateLabelTwoNodeRelation";
        this.id = id;
        this.estore = estore;
        this.referrerVariable =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        this.referrerLabel =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel();
        this.refereeVariable =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getVariable()
                        : right.getVariable();
        this.refereeLabel =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getLabel()
                        : right.getLabel();
        this.relationLabel = relation.getEdgeNames().get(0);
        this.relationVariable = relation.getVariable();
    }

    public Estore getDataSource() {
        return estore;
    }

    public String getReferrerVariable() {
        return referrerVariable;
    }

    public String getRefereeVariable() {
        return refereeVariable;
    }

    public String getRelationVariable() {
        return relationVariable;
    }

    public String getReferrerLabel() {
        return referrerLabel;
    }

    public String getRefereeLabel() {
        return refereeLabel;
    }

    public String getRelationLabel() {
        return relationLabel;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        ArrayList<Object> refObjects = new ArrayList<Object>();
        Table result =
                new Table(
                        Arrays.asList(
                                new String[] {
                                    referrerVariable, refereeVariable, relationVariable
                                }));
        Object referrerObject = null;
        Object refereeObject = null;
        Class<?> refereeClass = null;
        try {
            refereeClass = Class.forName(refereeLabel);
            refereeObject = estore.insert(refereeClass);
        } catch (Exception e) {
            String fullyQualifiedName = refereeLabel;
            fullyQualifiedName = fullyQualifiedName.replace('.', '/');
            int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');
            String className = fullyQualifiedName;
            String packageName = null;
            if (lastIndexSlash != -1) {
                className =
                        fullyQualifiedName.substring(
                                lastIndexSlash + 1, fullyQualifiedName.length());
                packageName = fullyQualifiedName.substring(0, lastIndexSlash);
            }
            refereeClass =
                    ClassHelper.createClass(
                            className, packageName, new String[0], new Class[0], new Object[0]);
            try {
                refereeObject = estore.insert(refereeClass);

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            Class<?> referrerClass = Class.forName(referrerLabel);
            referrerObject = estore.insert(referrerClass);
        } catch (Exception e) {
            String fullyQualifiedName = referrerLabel;
            fullyQualifiedName = fullyQualifiedName.replace('.', '/');
            int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');
            String className = fullyQualifiedName;
            String packageName = null;
            if (lastIndexSlash != -1) {
                className =
                        fullyQualifiedName.substring(
                                lastIndexSlash + 1, fullyQualifiedName.length());
                packageName = fullyQualifiedName.substring(0, lastIndexSlash);
            }

            Class<?> klass =
                    ClassHelper.createClass(
                            className,
                            packageName,
                            new String[] {relationLabel},
                            new Class[] {refereeClass},
                            new Object[] {refereeObject});

            try {
                referrerObject = estore.insert(klass);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        ArrayList<Object> referrerObjects = new ArrayList<Object>();
        ArrayList<Object> refereeObjects = new ArrayList<Object>();
        referrerObjects.add(referrerObject);
        refereeObjects.add(refereeObject);
        result.put(referrerVariable, referrerObjects);
        result.put(refereeVariable, refereeObjects);
        return result;
    }
}

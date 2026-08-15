package org.estore.planner.scan;

import java.util.List;
import org.estore.Estore;
import org.estore.planner.util.NodeProperty;

public interface NodeScan {
    public String getVariable();

    public String getLabel();

    public List<NodeProperty> getProperties();

    public Estore getDataSource();
}

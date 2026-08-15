package org.estore.planner.scan;

import java.util.List;
import org.estore.Estore;
import org.estore.planner.util.PathRange;

public interface RelationScan {

    public Estore getDataSource();

    public List<String> getEdgeNames();

    public PathRange getPathRange();

    public String getVariable();

    public String getReferrerVariable();

    public String getRefereeVariable();

    public String getReferrerLabel();

    public String getRefereeLabel();
}

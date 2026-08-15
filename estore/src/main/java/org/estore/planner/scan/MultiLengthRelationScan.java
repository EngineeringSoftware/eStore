package org.estore.planner.scan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.estore.planner.LogicalPlan;
import org.estore.planner.util.Table;

public class MultiLengthRelationScan extends LogicalPlan {

    public MultiLengthRelationScan(int id) {
        this.id = id;
        this.children = new ArrayList<LogicalPlan>();
        this.name = "MultiLengthRelationScan";
    }

    @Override
    public List<LogicalPlan> children() {
        return children;
    }

    @Override
    public Table execute(Table input) {

        Table result = null;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        try {
            List<Future<Table>> futures = new ArrayList<>();

            for (LogicalPlan plan : children) {
                Future<Table> future = executorService.submit(() -> plan.execute(null));
                futures.add(future);
            }

            result = futures.get(0).get();
            for (int j = 1; j < futures.size(); j++) {
                result =
                        result.join(
                                futures.get(j).get(),
                                ((RelationScan) children.get(j)).getReferrerVariable());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return result;
    }
}

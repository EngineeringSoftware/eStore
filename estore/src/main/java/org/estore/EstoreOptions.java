package org.estore;

public class EstoreOptions {
    private boolean useUnsafe;
    private boolean useDfs;
    private boolean useRecursion;
    private boolean profileParseTreeGenTime;
    private boolean profileParseQueryPlanASTBuildTime;
    private boolean profileQueryExecutionTime;
    private boolean profileTotalQueryTime;

    public EstoreOptions() {
        useUnsafe = true;
        useDfs = false;
        useRecursion = false;
        profileParseTreeGenTime = false;
        profileParseQueryPlanASTBuildTime = false;
        profileQueryExecutionTime = false;
        profileTotalQueryTime = false;
    }

    public static EstoreOptions getDefaultOptions() {
        return new EstoreOptions();
    }

    public EstoreOptions useUnsafe(boolean flag) {
        useUnsafe = flag;
        return this;
    }

    public EstoreOptions useDfs(boolean flag) {
        useDfs = flag;
        return this;
    }

    public EstoreOptions useRecursion(boolean flag) {
        useRecursion = flag;
        return this;
    }

    public EstoreOptions profile(boolean flag) {
        profileParseTreeGenTime = flag;
        profileParseQueryPlanASTBuildTime = flag;
        profileQueryExecutionTime = flag;
        profileTotalQueryTime = flag;
        return this;
    }

    public EstoreOptions profileParseTreeGenTime(boolean flag) {
        profileParseTreeGenTime = flag;
        return this;
    }

    public EstoreOptions profileParseQueryPlanASTBuildTime(boolean flag) {
        profileParseQueryPlanASTBuildTime = flag;
        return this;
    }

    public EstoreOptions profileQueryExecutionTime(boolean flag) {
        profileQueryExecutionTime = flag;
        return this;
    }

    public EstoreOptions profileTotalQueryTime(boolean flag) {
        profileTotalQueryTime = flag;
        return this;
    }

    public boolean getUseUnsafe() {
        return useUnsafe;
    }

    public boolean getUseDfs() {
        return useDfs;
    }

    public boolean getUseRecursion() {
        return useRecursion;
    }

    public boolean getProfileParseTreeGenTime() {
        return profileParseTreeGenTime;
    }

    public boolean getProfileParseQueryPlanASTBuildTime() {
        return profileParseQueryPlanASTBuildTime;
    }

    public boolean getProfileQueryExecutionTime() {
        return profileQueryExecutionTime;
    }

    public boolean getProfileTotalQueryTime() {
        return profileTotalQueryTime;
    }
}

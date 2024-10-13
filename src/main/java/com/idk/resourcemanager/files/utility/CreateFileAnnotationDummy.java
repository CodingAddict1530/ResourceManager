package com.idk.resourcemanager.files.utility;

public class CreateFileAnnotationDummy {

    private Condition condition;
    private final String method;
    private final long delay;
    private final boolean overwrite;
    private final int retryAttempts;
    private final long retryInterval;

    public CreateFileAnnotationDummy() {

        this.condition = Condition.IMMEDIATELY;
        this.method = "";
        this.delay = 0;
        this.overwrite = true;
        this.retryAttempts = 3;
        this.retryInterval = 100;

    }

    public CreateFileAnnotationDummy(Condition condition, String method, long delay, boolean overwrite, int retryAttempts, long retryInterval) {

        this.condition = condition;
        this.method = method;
        this.delay = delay;
        this.overwrite = overwrite;
        this.retryAttempts = retryAttempts;
        this.retryInterval = retryInterval;

    }

    public Condition getCondition() {

        return this.condition;
    }

    public String getMethod() {

        return this.method;
    }

    public long getDelay() {

        return this.delay;
    }

    public boolean shouldOverwrite() {

        return this.overwrite;
    }

    public int getRetryAttempts() {

        return this.retryAttempts;
    }

    public long getRetryInterval() {

        return this.retryInterval;
    }

    public void setCondition(Condition condition) {

        this.condition = condition;
    }

}

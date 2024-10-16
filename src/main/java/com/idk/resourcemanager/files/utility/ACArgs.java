package com.idk.resourcemanager.files.utility;

public class ACArgs {

    private Condition condition;
    private String method;
    private long delay;
    private boolean overwrite;
    private int retryAttempts;
    private long retryInterval;

    public ACArgs(Condition condition, String method, long delay, boolean overwrite, int retryAttempts, long retryInterval) {

        this.condition = condition;
        this.method = method;
        this.delay = delay;
        this.overwrite = overwrite;
        this.retryAttempts = retryAttempts;
        this.retryInterval = retryInterval;

    }

    public ACArgs() {

        this(Condition.IMMEDIATELY, "", 0, true, 3, 100);
    }

    public ACArgs setCondition(Condition condition) {

        this.condition = condition;
        return this;
    }

    public ACArgs setMethod(String method) {

        this.method = method;
        return this;
    }

    public ACArgs setDelay(long delay) {

        this.delay = delay;
        return this;
    }

    public ACArgs setOverwrite(boolean overwrite) {

        this.overwrite = overwrite;
        return this;
    }

    public ACArgs setRetryAttempts(int retryAttempts) {

        this.retryAttempts = retryAttempts;
        return this;
    }

    public ACArgs setRetryInterval(long retryInterval) {

        this.retryInterval = retryInterval;
        return this;
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

    public boolean isOverwrite() {

        return this.overwrite;
    }

    public int getRetryAttempts() {

        return this.retryAttempts;
    }

    public long getRetryInterval() {

        return this.retryInterval;
    }

}

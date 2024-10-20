package com.idk.resourcemanager.files.utility;

public class CreateFileAnnotationDummy {

    private Condition condition;
    private final String method;
    private final Class<?>[] parameterTypes;
    private final long delay;
    private final boolean overwrite;
    private final int retryAttempts;
    private final long retryInterval;

    public CreateFileAnnotationDummy(ACArgs args) {

        this.condition = args.getCondition();
        this.method = args.getMethod();
        this.parameterTypes = args.getParameterTypes();
        this.delay = args.getDelay();
        this.overwrite = args.isOverwrite();
        this.retryAttempts = args.getRetryAttempts();
        this.retryInterval = args.getRetryInterval();

    }

    public Condition getCondition() {

        return this.condition;
    }

    public String getMethod() {

        return this.method;
    }

    public Class<?>[] getParameterTypes() {

        return this.parameterTypes;
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

    public void setCondition(Condition condition, Monitor.AccessKey key) {

        this.condition = condition;
    }

}

package com.idk.resourcemanager.application;

import com.idk.resourcemanager.inspection.CodeInspector;

import java.util.ArrayList;
import java.util.List;

public abstract class Application {

    private static final ArrayList<String> excludedPackages = new ArrayList<String>();
    static {
        excludedPackages.addAll(List.of("java.*", "javax.*", "jdk.*", "sun.*", "com.sun.*"));
    }

    public final void launch() {

        CodeInspector.inspect();
        run();

    }

    public abstract void run();

    public static ArrayList<String> getExcludedPackages() {

        return excludedPackages;
    }

    public static void addExcludedPackage(String p) {

        excludedPackages.add(p);
    }

    public static void removeExcludedPackage(String p) {

        excludedPackages.remove(p);
    }

}

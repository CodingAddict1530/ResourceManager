package com.idk.resourcemanager.application;

import com.idk.resourcemanager.inspection.CodeInspector;

import java.util.ArrayList;
import java.util.List;

public abstract class Application {

    private static final ArrayList<String> includedPackages = new ArrayList<String>();

    public static void launch(Application application) {

        String[] parts = application.getClass().getPackage().getName().split("\\.");

        if (parts.length > 1) {
            includedPackages.add(parts[0] + "." + parts[1]);
        } else {
            if (!application.getClass().getPackage().getName().isEmpty()) {
                includedPackages.add(application.getClass().getPackage().getName());
            } else {
                throw new RuntimeException("Application class must have a package name.");
            }
        }

        CodeInspector.inspect();
        application.run();

    }

    public abstract void run();

    public static ArrayList<String> getIncludedPackages() {

        return includedPackages;
    }

    public static void addIncludedPackage(String p) {

        includedPackages.add(p);
    }

    public static void removeIncludedPackage(String p) {

        includedPackages.remove(p);
    }

}

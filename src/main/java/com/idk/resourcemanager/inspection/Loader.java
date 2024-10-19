package com.idk.resourcemanager.inspection;

public class Loader {

    public static boolean load(Class<?> clazz) {

        try {
            switch (clazz.getName()) {
                case "com.idk.resourcemanager.files.annotations.CreateFile",
                     "com.idk.resourcemanager.files.classes.ACFile":
                    Class.forName("com.idk.resourcemanager.files.aspects.FileCreationAspect");
                    return true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;

    }

}

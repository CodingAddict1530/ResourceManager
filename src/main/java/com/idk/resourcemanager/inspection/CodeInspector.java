package com.idk.resourcemanager.inspection;

import com.idk.resourcemanager.application.Application;
import com.idk.resourcemanager.files.annotations.CreateFile;
import com.idk.resourcemanager.files.classes.ACFile;
import com.sun.jdi.ClassNotLoadedException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class CodeInspector {

    private static final List<Class<? extends Annotation>> annotations = new ArrayList<>() {};
    static {
        annotations.addAll(List.of(CreateFile.class));
    }

    private static final List<Class<?>> classes = new ArrayList<>() {};
    static {
        classes.addAll(List.of(ACFile.class));
    }

    public static void inspect() {

        try (ScanResult scanResult = new ClassGraph().enableAllInfo()
                .acceptPackages(Application.getIncludedPackages().toArray(new String[0]))
                .scan()) {
            scanResult.getAllClasses().forEach(classInfo -> {

                try {

                    Class<?> clazz = Class.forName(classInfo.getName());

                    for (Field field : clazz.getDeclaredFields()) {

                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);

                        try {

                            for (Class<? extends Annotation> annotation : annotations) {
                                if (field.isAnnotationPresent(annotation)) {
                                    if (!Loader.load(annotation)) {
                                        throw new ClassNotLoadedException(annotation.getName());
                                    }
                                }
                            }

                        } finally {
                            field.setAccessible(accessible);
                        }

                    }

                    for (Method method : clazz.getDeclaredMethods()) {

                        for (Parameter parameter : method.getParameters()) {

                            boolean accessible = method.isAccessible();
                            method.setAccessible(true);

                            try {

                                for (Class<? extends Annotation> annotation : annotations) {
                                    if (parameter.isAnnotationPresent(annotation)) {
                                        if (!Loader.load(annotation)) {
                                            throw new ClassNotLoadedException(annotation.getName());
                                        }
                                    }
                                }

                            } finally {
                                method.setAccessible(accessible);
                            }

                        }

                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotLoadedException e) {
                    throw new RuntimeException(e);
                }

            });

        }

    }

}

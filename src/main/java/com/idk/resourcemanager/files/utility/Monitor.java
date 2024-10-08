package com.idk.resourcemanager.files.utility;

import com.idk.resourcemanager.files.annotations.CreateFile;
import com.idk.resourcemanager.files.annotations.Track;
import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

@Aspect
public class Monitor {

    private static ArrayList<TrackedMethod> trackedMethods;

    @Pointcut("annotation(Track)")
    public static void trackMethods() {

    }

    @Around("trackedMethods()")
    public static void monitor(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        for (TrackedMethod trackedMethod : trackedMethods) {
            if (trackedMethod.methodName.equals(methodName)) {
                if (trackedMethod.annotation instanceof CreateFile annotation) {
                    FieldSignature signature = (FieldSignature) joinPoint.getSignature();
                    Track track = signature.getField().getAnnotation(Track.class);
                    if (trackedMethod.times >= track.times()) {
                        return;
                    }
                    FileCreationAspect.createFile(trackedMethod.file, annotation, true);
                    if (track.times() != -1) {
                        trackedMethod.times++;
                    }
                }
            }
        }

    }

    public static void track(String methodName, Annotation annotation, File file) {

        trackedMethods.add(new TrackedMethod(methodName, annotation, file));
    }

    private static class TrackedMethod {

        private final String methodName;
        private final Annotation annotation;
        private final File file;
        private int times;

        public TrackedMethod(String methodName, Annotation annotation, File file) {

            this.methodName = methodName;
            this.annotation = annotation;
            this.file = file;
            this.times = 0;

        }

    }

}

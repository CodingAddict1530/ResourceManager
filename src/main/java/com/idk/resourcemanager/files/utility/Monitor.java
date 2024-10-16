package com.idk.resourcemanager.files.utility;

import com.idk.resourcemanager.files.annotations.Track;
import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import java.io.File;
import java.util.ArrayList;

@Aspect
public class Monitor {

    private static final ArrayList<TrackedMethod> trackedMethods = new ArrayList<>();

    @Pointcut("@annotation(com.idk.resourcemanager.files.annotations.Track)")
    public static void trackMethods() {}

    @Around("trackMethods()")
    public static Object performAction(ProceedingJoinPoint joinPoint) throws Throwable {

        Throwable throwable = null;
        Object result = null;

        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        for (TrackedMethod trackedMethod : trackedMethods) {
            if (trackedMethod.methodName.equals(methodName)) {
                if (trackedMethod.dummy instanceof CreateFileAnnotationDummy dummy) {
                    if (dummy.getCondition().equals(Condition.AFTER_METHOD)) {
                        try {
                            joinPoint.proceed();
                        } catch (Throwable e) {
                            throwable = e;
                        }
                    }

                    Track track = ((FieldSignature) joinPoint.getSignature()).getField().getAnnotation(Track.class);
                    dummy.setCondition(Condition.IMMEDIATELY, new AccessKey());
                    FileCreationAspect.createFile(trackedMethod.file, dummy);

                    if (dummy.getCondition().equals(Condition.BEFORE_METHOD)) {
                        try {
                            result = joinPoint.proceed();
                        } catch (Throwable e) {
                            throwable = e;
                        }
                    }
                    if (track.times() != -1) {
                        trackedMethod.times++;
                    }
                    if (trackedMethod.times >= track.times() && track.times() != -1) {
                        trackedMethods.remove(trackedMethod);
                    }

                    if (throwable != null) {
                        throw throwable;
                    }

                    return result;
                }
            }
        }

        return joinPoint.proceed();

    }

    public static void track(String methodName, CreateFileAnnotationDummy dummy, File file) {

        trackedMethods.add(new TrackedMethod(methodName, dummy, file));
    }

    private static class TrackedMethod {

        private final String methodName;
        private final CreateFileAnnotationDummy dummy;
        private final File file;
        private int times;

        public TrackedMethod(String methodName, CreateFileAnnotationDummy dummy, File file) {

            this.methodName = methodName;
            this.dummy = dummy;
            this.file = file;
            this.times = 0;

        }

    }

    public static class AccessKey {

        private AccessKey() {}

    }

}

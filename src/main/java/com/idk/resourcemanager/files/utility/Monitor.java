package com.idk.resourcemanager.files.utility;

import com.idk.resourcemanager.files.annotations.Track;
import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Aspect
public class Monitor {

    static {
        ByteBuddyAgent.install();
    }

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
        redefine(methodName, dummy.getParameterTypes());
    }

    private static boolean redefine(String m, Class<?>[] parameterTypes) {

        try {

            int lastDot = m.lastIndexOf('.');
            String className = m.substring(0, lastDot);
            String methodName = m.substring(lastDot + 1);

            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);

            new ByteBuddy()
                    .redefine(clazz)
                    .method(ElementMatchers.named(methodName))
                    .intercept(MethodDelegation.to(method))
                    .annotateMethod(new Track() {

                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return Track.class;
                        }

                    })
                    .make()
                    .load(clazz.getClassLoader());

            return true;

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    private static class TrackedMethod {

        private final String methodName;
        private final CreateFileAnnotationDummy dummy;
        private final File file;

        public TrackedMethod(String methodName, CreateFileAnnotationDummy dummy, File file) {

            this.methodName = methodName;
            this.dummy = dummy;
            this.file = file;

        }

    }

    public static class AccessKey {

        private AccessKey() {}

    }

}

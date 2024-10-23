package com.idk.resourcemanager.files.utility;

import com.idk.resourcemanager.files.annotations.Track;
import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

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

    @Pointcut("@annotation(com.idk.resourcemanager.files.annotations.Track) && execution(* *(..))")
    public static void trackMethods() {}

    @Before("trackMethods()")
    public void performActionBefore(JoinPoint joinPoint) {

        performAction(joinPoint, true);
    }

    @After("trackMethods()")
    public void performActionAfter(JoinPoint joinPoint) {

        performAction(joinPoint, false);
    }

    public static void track(String methodName, CreateFileAnnotationDummy dummy, File file) {

        trackedMethods.add(new TrackedMethod(methodName, dummy, file));
        //redefine(methodName, dummy.getParameterTypes());

    }

    private static void performAction(JoinPoint joinPoint, boolean before) {

        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        for (TrackedMethod trackedMethod : trackedMethods) {
            if (trackedMethod.methodName.equals(methodName)) {
                if (trackedMethod.dummy instanceof CreateFileAnnotationDummy dummy) {
                    if (dummy.getCondition().equals(Condition.AFTER_METHOD) && before ||
                            dummy.getCondition().equals(Condition.BEFORE_METHOD) && !before) {
                        return;
                    }
                    dummy.setCondition(Condition.IMMEDIATELY, new AccessKey());
                    FileCreationAspect.createFile(trackedMethod.file, dummy);
                }
            }
        }

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
                    .visit(new MemberAttributeExtension.ForMethod()
                            .annotateMethod(new Track() {

                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return Track.class;
                                }

                            }).on(ElementMatchers.is(method))
                    ).make()
                    .load(clazz.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

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

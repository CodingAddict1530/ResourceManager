package com.idk.resourcemanager.files.aspects;

import com.idk.resourcemanager.files.annotations.CreateFile;
import com.idk.resourcemanager.files.utility.ACArgs;
import com.idk.resourcemanager.files.utility.CreateFileAnnotationDummy;
import com.idk.resourcemanager.files.utility.Monitor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.FieldSignature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Aspect
public class FileCreationAspect {

    private static final Map<File, FileCreationResult> fileCreationResults = new HashMap<>();

    @Pointcut("set(@com.idk.resourcemanager.files.annotations.CreateFile java.io.File * *)")
    public static void fileCreation() {}

    @Before("fileCreation()")
    public static void handleFileCreation(final JoinPoint joinPoint) {

        FieldSignature signature = (FieldSignature) joinPoint.getSignature();
        Field field = signature.getField();
        Object target = joinPoint.getTarget();

        field.setAccessible(true);
        try {
            createFile((File) field.get(target), new CreateFileAnnotationDummy(
                    new ACArgs(
                            field.getAnnotation(CreateFile.class).condition(),
                            field.getAnnotation(CreateFile.class).method(),
                            field.getAnnotation(CreateFile.class).delay(),
                            field.getAnnotation(CreateFile.class).overwrite(),
                            field.getAnnotation(CreateFile.class).retryAttempts(),
                            field.getAnnotation(CreateFile.class).retryInterval(),
                            field.getAnnotation(CreateFile.class).parameterTypes()
                    )
            ));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

    public static CompletableFuture<Boolean> getFileCreationResult(File file) {

        return fileCreationResults.get(file).created;
    }

    public static Exception getFileCreationException(File file) {

        return fileCreationResults.get(file).exception;
    }

    public static void removeFileCreationResult(File file) {

        fileCreationResults.remove(file);
    }

    public static void removeAllFileCreationResults() {

        fileCreationResults.clear();
    }

    public static void createFile(File file, CreateFileAnnotationDummy dummy) {

        fileCreationResults.put(file, new FileCreationResult(null));

        fileCreationResults.get(file).created = CompletableFuture.supplyAsync(() -> {

            int attempts = 0;
            boolean success = false;
            Exception exception = null;

            while (attempts < dummy.getRetryAttempts() && !success) {

                try {

                    switch (dummy.getCondition()) {
                        case DELAYED:
                            Thread.sleep(dummy.getDelay());
                            break;

                        case BEFORE_METHOD, AFTER_METHOD:
                            Monitor.track(dummy.getMethod(), dummy, file);
                            break;
                    }

                    if (file.exists()) {
                        if (!dummy.shouldOverwrite()) {
                            exception = new FileAlreadyExistsException(file.getAbsolutePath());
                            return false;
                        }
                        if (file.isDirectory()) {
                            recursiveDelete(file);
                        } else {
                            file.delete();
                        }

                    }
                    success = file.createNewFile();
                } catch (IOException | SecurityException | InterruptedException e) {
                    exception = e;
                    attempts++;
                    try {
                        Thread.sleep(dummy.getRetryInterval());
                    } catch (InterruptedException e1) {
                        exception = e1;
                    }
                }

            }

            if (exception != null) {
                fileCreationResults.get(file).exception = exception;
            }

            return success;

        });

    }

    private static void recursiveDelete(File dir) throws IOException {

        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;

            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;

            }

        });

    }

    private static class FileCreationResult {

        private CompletableFuture<Boolean> created;
        private Exception exception;

        public FileCreationResult(final CompletableFuture<Boolean> created) {

            this.created = created;
        }

    }

}

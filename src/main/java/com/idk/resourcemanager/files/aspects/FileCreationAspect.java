package com.idk.resourcemanager.files.aspects;

import com.idk.resourcemanager.files.annotations.CreateFile;
import com.idk.resourcemanager.files.utility.Condition;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.FieldSignature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Aspect
public class FileCreationAspect {

    private static final Map<File, FileCreationResult> fileCreationResults = new HashMap<>();

    @Pointcut("set(@CreateFile * *)")
    public static void fileCreation() {}

    @After("annotatedFileInitialization()")
    public static void handleFileCreation(final JoinPoint joinPoint) {

        FieldSignature signature = (FieldSignature) joinPoint.getSignature();
        Field field = signature.getField();
        Object target = joinPoint.getTarget();

        if (field.isAnnotationPresent(CreateFile.class) && File.class.isAssignableFrom(field.getType())) {

            field.setAccessible(true);
            try {
                createFile((File) field.get(target), field.getAnnotation(CreateFile.class), false);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }

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

    public static void createFile(File file, CreateFile annotation, boolean ignoreCondition) {

        fileCreationResults.put(file, new FileCreationResult(null));

        fileCreationResults.get(file).created = CompletableFuture.supplyAsync(() -> {

            int attempts = 0;
            boolean success = false;
            Exception exception = null;

            while (attempts < annotation.retryAttempts() && !success) {

                try {

                    if (!ignoreCondition) {
                        switch (annotation.condition()) {
                            case DELAYED:
                                Thread.sleep(annotation.delay());
                                break;

                            case BEFORE_METHOD:
                                return false;
                        }
                    }

                    if (file.exists()) {
                        if (!annotation.overwrite()) {
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
                        Thread.sleep(annotation.retryInterval());
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

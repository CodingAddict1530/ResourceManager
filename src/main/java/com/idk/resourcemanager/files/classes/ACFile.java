package com.idk.resourcemanager.files.classes;

import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import com.idk.resourcemanager.files.utility.Condition;
import com.idk.resourcemanager.files.utility.CreateFileAnnotationDummy;

import java.io.File;
import java.net.URI;

public class ACFile extends File {

    private final CreateFileAnnotationDummy dummy = new CreateFileAnnotationDummy();

    public ACFile(String pathname) {

        super(pathname);
        FileCreationAspect.createFile(this, dummy);
    }

    public CreateFileAnnotationDummy setCondition(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public CreateFileAnnotationDummy setMethod(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public CreateFileAnnotationDummy setDelay(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public CreateFileAnnotationDummy setOverwrite(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public CreateFileAnnotationDummy setRetryAttempts(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public CreateFileAnnotationDummy setRetryIntervals(Condition condition) {

        dummy.setCondition(condition);
        return dummy;
    }

    public ACFile(String parent, String child, String fieldName) {

        super(parent, child);
        FileCreationAspect.createFile(this, dummy);
    }

    public ACFile(File parent, String child) {

        super(parent, child);
        FileCreationAspect.createFile(this, dummy);
    }

    public ACFile(URI uri) {

        super(uri);
        FileCreationAspect.createFile(this, dummy);
    }

    public ACFile(File file) {

        super(file.getAbsolutePath());
        FileCreationAspect.createFile(this, dummy);
    }

}

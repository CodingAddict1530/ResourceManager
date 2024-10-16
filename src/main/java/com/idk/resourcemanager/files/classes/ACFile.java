package com.idk.resourcemanager.files.classes;

import com.idk.resourcemanager.files.aspects.FileCreationAspect;
import com.idk.resourcemanager.files.utility.ACArgs;
import com.idk.resourcemanager.files.utility.CreateFileAnnotationDummy;

import java.io.File;
import java.net.URI;

public class ACFile extends File {

    public ACFile(String pathname, ACArgs args) {

        super(pathname);
        FileCreationAspect.createFile(this, new CreateFileAnnotationDummy(args));
    }

    public ACFile(String parent, String child, String fieldName, ACArgs args) {

        super(parent, child);
        FileCreationAspect.createFile(this, new CreateFileAnnotationDummy(args));
    }

    public ACFile(File parent, String child, ACArgs args) {

        super(parent, child);
        FileCreationAspect.createFile(this, new CreateFileAnnotationDummy(args));
    }

    public ACFile(URI uri, ACArgs args) {

        super(uri);
        FileCreationAspect.createFile(this, new CreateFileAnnotationDummy(args));
    }

    public ACFile(File file, ACArgs args) {

        super(file.getAbsolutePath());
        FileCreationAspect.createFile(this, new CreateFileAnnotationDummy(args));
    }

}

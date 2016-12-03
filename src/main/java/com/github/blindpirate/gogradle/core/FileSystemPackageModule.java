package com.github.blindpirate.gogradle.core;

import com.github.blindpirate.gogradle.core.dependency.DefaultDependencyResolutionStrategy;
import com.github.blindpirate.gogradle.core.dependency.DependencyResolutionStrategy;
import com.github.blindpirate.gogradle.core.pack.AbstractPakcageModule;
import com.github.blindpirate.gogradle.core.dependency.DependencyHelper;
import com.github.blindpirate.gogradle.core.dependency.GolangDependencySet;
import com.github.blindpirate.gogradle.core.dependency.GolangPackageDependency;
import com.github.blindpirate.gogradle.core.pack.LocalFileSystemPackageModule;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * A {@link FileSystemPackageModule} is a directory in file system,
 * regardless of stable or transient
 */
public abstract class FileSystemPackageModule extends AbstractPakcageModule implements GolangPackageDependency {

    private Path rootDir;

    protected Date updateTime;

    private GolangDependencySet dependencies;

    private DependencyResolutionStrategy dependencyResolutionStrategy
            = new DefaultDependencyResolutionStrategy();

    public abstract FileSystemPackageModule vendor(Path relativePathToVendor);

    public FileSystemPackageModule(String name, Path rootDir) {
        super(name);
        this.rootDir = rootDir;
    }

    protected Path addVendorPrefix(Path relativePathToVendor) {
        String relativeToParentModule = "vendor" + File.separator + relativePathToVendor;
        return Paths.get(relativeToParentModule);
    }

    public GolangDependencySet getDependencies() {
        if (dependencies == null) {
            dependencies =
                    DependencyHelper.resolveDirectDependencies(dependencyResolutionStrategy, this);
        }
        return dependencies;
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public GolangPackageModule getPackage() {
        return this;
    }

}
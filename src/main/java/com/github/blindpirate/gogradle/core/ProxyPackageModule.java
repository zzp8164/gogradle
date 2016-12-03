package com.github.blindpirate.gogradle.core;

import com.github.blindpirate.gogradle.core.dependency.GolangDependencySet;
import com.github.blindpirate.gogradle.core.pack.AbstractPakcageModule;
import org.gradle.api.artifacts.DependencySet;

import java.nio.file.Path;
import java.util.Date;

/**
 * A {@link ProxyPackageModule} is a proxy that represent a package that may be resolved later.
 * <p>
 * When resolved, it will record all information of underlying package module.
 */
public class ProxyPackageModule extends AbstractPakcageModule {

    private GolangPackageModule delegate;

    private GolangDependencySet dependencies;
    private Date updateTime;

    public ProxyPackageModule(String name, GolangPackageModule delegate) {
        super(name);
        this.delegate = delegate;
    }

    public GolangDependencySet getDependencies() {
        if (dependencies == null) {
            dependencies = delegate.getDependencies();
        }
        return dependencies;
    }

    @Override
    public Path getRootDir() {
        return delegate.getRootDir();
    }

    @Override
    public Date getUpdateTime() {
        if (updateTime == null) {
            updateTime = delegate.getUpdateTime();
        }
        return updateTime;
    }
}
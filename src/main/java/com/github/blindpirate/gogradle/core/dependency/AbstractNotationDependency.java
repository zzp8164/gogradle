package com.github.blindpirate.gogradle.core.dependency;

import com.github.blindpirate.gogradle.core.GolangPackageModule;
import com.github.blindpirate.gogradle.core.pack.DependencyResolver;
import com.github.blindpirate.gogradle.vcs.git.GitDependencyResolver;
import com.github.blindpirate.gogradle.vcs.VcsType;
import org.omg.CORBA.Object;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNotationDependency extends AbstractGolangDependency {

    private VcsType vcsType;

    private boolean transitive = true;

    private Map<String, Object> excludes = new HashMap<>();

    @Override
    public GolangPackageModule getPackage() {
        DependencyResolver resolver = DependencyHelper.injector.getInstance(this.resolverClass());
        return resolver.resolve(this);
    }

    protected abstract Class<? extends DependencyResolver> resolverClass();

    public VcsType getVcsType() {
        return vcsType;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public Map<String, Object> getExcludes() {
        return excludes;
    }

    public void exclude(Map<String, Object> map) {
        this.excludes.putAll(map);
    }

    public AbstractNotationDependency setVcsType(VcsType vcsType) {
        this.vcsType = vcsType;
        return this;
    }

    public AbstractNotationDependency setTransitive(boolean transitive) {
        this.transitive = transitive;
        return this;
    }

}
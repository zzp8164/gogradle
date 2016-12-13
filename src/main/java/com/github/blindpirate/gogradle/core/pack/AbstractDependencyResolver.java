package com.github.blindpirate.gogradle.core.pack;

import com.github.blindpirate.gogradle.core.GolangPackageModule;
import com.github.blindpirate.gogradle.core.TempFileModule;
import com.github.blindpirate.gogradle.core.cache.CacheManager;
import com.github.blindpirate.gogradle.core.dependency.GolangDependency;
import com.github.blindpirate.gogradle.util.FileUtils;
import com.google.common.base.Optional;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public abstract class AbstractDependencyResolver<REPOSITORY, VERSION> implements DependencyResolver {

    @Inject
    private CacheManager cacheManager;

    @Override
    public GolangPackageModule resolve(final GolangDependency dependency) {
        return cacheManager.runWithGlobalCacheLock(dependency, new Callable<GolangPackageModule>() {
            @Override
            public GolangPackageModule call() {
                Path path = cacheManager.getGlobalCachePath(dependency.getName());
                return doResolve(dependency, path);
            }
        });
    }

    private GolangPackageModule doResolve(GolangDependency dependency, Path path) {
        REPOSITORY repository = resolveToGlobalCache(dependency, path);
        VERSION version = determineVersion(repository, dependency);
        resetToSpecifiedVersion(repository, version);
        return new TempFileModule(dependency.getName(), path);
    }

    protected abstract void resetToSpecifiedVersion(REPOSITORY repository, VERSION version);

    protected abstract VERSION determineVersion(REPOSITORY repository, GolangDependency dependency);

    private REPOSITORY resolveToGlobalCache(GolangDependency dependency, Path path) {
        Optional<REPOSITORY> repositoryInGlobalCache = ensureGlobalCacheEmptyOrMatch(dependency, path);
        if (!repositoryInGlobalCache.isPresent()) {
            return initRepository(dependency, path);
        } else {
            return updateRepository(repositoryInGlobalCache.get(), path);
        }
    }

    protected abstract REPOSITORY updateRepository(REPOSITORY repository, Path path);

    protected abstract REPOSITORY initRepository(GolangDependency dependency, Path path);


    private Optional<REPOSITORY> ensureGlobalCacheEmptyOrMatch(GolangDependency dependency, Path path) {
        if (FileUtils.dirIsEmpty(path)) {
            return Optional.absent();
        } else {
            Optional<REPOSITORY> ret = repositoryMatch(path, dependency);
            if (ret.isPresent()) {
                return ret;
            } else {
                throw new IllegalStateException("Existing cache directory "
                        + path.toAbsolutePath().toString()
                        + " does not match the dependency "
                        + dependency.getName());
            }
        }
    }

    /**
     * Checks if a non-empty directory matches the dependency.
     *
     * @param repoPath   the directory
     * @param dependency the dependency
     * @return
     */
    protected abstract Optional<REPOSITORY> repositoryMatch(Path repoPath, GolangDependency dependency);
}
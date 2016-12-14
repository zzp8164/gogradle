package com.github.blindpirate.gogradle.core.dependency.produce

import com.github.blindpirate.gogradle.GogradleRunner
import com.github.blindpirate.gogradle.GolangPluginSetting
import com.github.blindpirate.gogradle.core.GolangPackageModule
import com.github.blindpirate.gogradle.core.dependency.GolangConfiguration
import com.github.blindpirate.gogradle.core.dependency.GolangDependency
import com.github.blindpirate.gogradle.core.dependency.GolangDependencySet
import com.github.blindpirate.gogradle.core.dependency.LockedDependencyManager
import com.github.blindpirate.gogradle.core.dependency.resolve.ModuleDependencyVistor
import com.google.common.base.Optional
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencySet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock

import static com.github.blindpirate.gogradle.core.mode.BuildMode.Develop
import static com.github.blindpirate.gogradle.core.mode.BuildMode.Reproducible
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@RunWith(GogradleRunner)
class GogradleRootProduceStrategyTest {
    @InjectMocks
    GogradleRootProduceStrategy strategy
    @Mock
    GolangPluginSetting golangPluginSetting
    @Mock
    LockedDependencyManager lockedDependencyManager
    @Mock
    ConfigurationContainer configurationContainer
    @Mock
    GolangConfiguration configuration

    @Mock
    GolangPackageModule module
    @Mock
    ModuleDependencyVistor visitor
    @Mock
    GolangDependency a1
    @Mock
    GolangDependency b1
    @Mock
    GolangDependency c1
    @Mock
    GolangDependency a2
    @Mock
    GolangDependency b2
    @Mock
    GolangDependency c2

    GolangDependencySet asGolangDependencySet(GolangDependency... dependencies) {
        return dependencies.inject(new GolangDependencySet(), { ret, dependency ->
            ret.add(dependency)
            return ret
        })
    }

    Optional<GolangDependencySet> asOptional(GolangDependency... dependencies) {
        if (dependencies.size() == 0) {
            return Optional.absent()
        }
        return Optional.of(asGolangDependencySet(dependencies))
    }

    DependencySet asDependencySet(GolangDependency... dependencies) {
        return asGolangDependencySet(dependencies).toDependencySet()
    }

    @Before
    void setUp() {
        when(a1.getName()).thenReturn('a')
        when(b1.getName()).thenReturn('b')
        when(c1.getName()).thenReturn('c')
        when(a2.getName()).thenReturn('a')
        when(b2.getName()).thenReturn('b')
        when(c2.getName()).thenReturn('c')

        when(configurationContainer.getByName(anyString()))
                .thenReturn(configuration)
    }

    void dependenciesInBuildDotGradle(GolangDependency... dependencies) {
        GolangDependencySet result = asGolangDependencySet(dependencies)
        when(configuration.getDependencies()).thenReturn(result.toDependencySet())
    }

    void lockedDependencies(GolangDependency... dependencies) {
        Optional<GolangDependencySet> result = asOptional(dependencies)
        when(lockedDependencyManager.getLockedDependencies()).thenReturn(result)
    }

    void vendorDependencies(GolangDependency... dependencies) {
        Optional<GolangDependencySet> result = asOptional(dependencies)
        when(visitor.visitVendorDependencies(module)).thenReturn(result)
    }

    @Test
    void 'dependencies in build.gradle should have top priority when in Develop mode'() {
        // given
        when(golangPluginSetting.getBuildMode()).thenReturn(Develop)
        dependenciesInBuildDotGradle(a1, b1)
        lockedDependencies(a2)
        vendorDependencies(b2)

        // when
        def resultDependencies = strategy.produce(module, visitor)

        // then
        assert resultDependencies.any { it == a1 }
        assert resultDependencies.any { it == b1 }
        assert !resultDependencies.any { it == a2 }
        assert !resultDependencies.any { it == b2 }

    }

    @Test
    void 'dependencis in vendor should have top priority when in Reproducible mode'() {
        // given
        when(golangPluginSetting.getBuildMode()).thenReturn(Reproducible)
        dependenciesInBuildDotGradle(a1)
        lockedDependencies(a2, b2)
        vendorDependencies()

        // when
        def result = strategy.produce(module, visitor)

        // then
        assert result.any { it == a2 }
        assert result.any { it == b2 }
        assert !result.any { it == a1 }
        assert !result.any { it == b1 }
    }

    @Test
    void 'locked dependencies should have priority than build.gradle in Reproducible mode'() {
        // given
        when(golangPluginSetting.getBuildMode()).thenReturn(Reproducible)
        dependenciesInBuildDotGradle(a1, b1)
        lockedDependencies(a2)
        vendorDependencies()

        // when
        def result = strategy.produce(module, visitor)

        // then
        assert result.any { it == a2 }
        assert result.any { it == b1 }
        assert !result.any { it == a1 }
    }

    @Test
    void 'source code should be scanned when no dependencies exist'() {
        // given
        when(golangPluginSetting.getBuildMode()).thenReturn(Develop)
        dependenciesInBuildDotGradle()
        lockedDependencies()
        vendorDependencies()

        // when
        strategy.produce(module, visitor)

        // then
        verify(visitor).visitSourceCodeDependencies(module)

    }


}
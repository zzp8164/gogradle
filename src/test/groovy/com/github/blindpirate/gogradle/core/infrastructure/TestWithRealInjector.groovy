package com.github.blindpirate.gogradle.core.infrastructure

import com.github.blindpirate.gogradle.GogradleModule
import com.github.blindpirate.gogradle.core.dependency.DependencyHelper
import com.google.inject.Guice
import com.google.inject.Injector
import org.gradle.internal.reflect.Instantiator
import org.junit.Before
import org.mockito.Mock

abstract class TestWithRealInjector {
    @Mock
    Instantiator instantiator

    Injector injector

    @Before
    void initInjector() {
        injector = Guice.createInjector(new GogradleModule(instantiator))
        DependencyHelper.INJECTOR_INSTANCE = injector
    }
}
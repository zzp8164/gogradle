package com.github.blindpirate.gogradle.core.dependency.produce

import com.github.blindpirate.gogradle.GogradleRunner
import org.junit.Test
import org.junit.runner.RunWith

import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

@RunWith(GogradleRunner)
class DefaultDependencyProduceStrategyTest extends DependencyProduceStrategyTest {
    DefaultDependencyProduceStrategy strategy = new DefaultDependencyProduceStrategy();

    @Test
    void 'source code will be scanned when external and vendor dependencies are all empty'() {
        // given
        externalDependencies()
        vendorDependencies()

        // when
        strategy.produce(module, visitor)

        //then
        verify(visitor).visitSourceCodeDependencies(module)
    }

    @Test
    void 'vendor dependencies should have priority over external dependencies'() {
        // given
        vendorDependencies(a1, b1)
        externalDependencies(a2, c2)

        // when
        def result = strategy.produce(module, visitor)
        // then
        assert result.any { it.is(a1) }
        assert result.any { it.is(b1) }
        assert result.any { it.is(c2) }
        assert !result.any { it.is(a2) }
        verify(visitor, times(0)).visitSourceCodeDependencies(module)
    }

    @Test
    void 'vendor dependencies should be used when external dependencies are empty'() {
        // given
        vendorDependencies(a1)
        externalDependencies()
        // when
        def result = strategy.produce(module, visitor)
        // then
        assert result.size() == 1
        assert result.any { it.is(a1) }
        verify(visitor, times(0)).visitSourceCodeDependencies(module)
    }

    @Test
    void 'external dependencies should be used when vendor are empty'() {
        // given
        vendorDependencies()
        externalDependencies(a2)
        // when
        def result = strategy.produce(module, visitor)
        // then
        assert result.size() == 1
        assert result.any { it.is(a2) }
        verify(visitor, times(0)).visitSourceCodeDependencies(module)
    }

}
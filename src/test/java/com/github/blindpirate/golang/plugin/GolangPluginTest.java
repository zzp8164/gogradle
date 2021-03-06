package com.github.blindpirate.golang.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.internal.ProjectBuilderImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class GolangPluginTest {

    @Test
    public void smoke_test() {
        Project project = new ProjectBuilderImpl().createProject("test", null, null);
        new GolangPlugin().apply(project);
    }

}
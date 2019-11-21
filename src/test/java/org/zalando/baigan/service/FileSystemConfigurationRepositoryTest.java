package org.zalando.baigan.service;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class FileSystemConfigurationRepositoryTest {

    @Test
    public void testReadConfigurationsFromFile() {
        final ConfigurationRepository repo = new FileSystemConfigurationRepository("src/test/resources/example.json", 3);
        assertThat(repo.get("express.feature.toggle").get().getDefaultValue(), Matchers.equalTo(false));
    }
}

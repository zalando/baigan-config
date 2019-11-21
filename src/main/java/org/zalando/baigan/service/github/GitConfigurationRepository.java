package org.zalando.baigan.service.github;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.baigan.model.Configuration;
import org.zalando.baigan.service.AbstractConfigurationRepository;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {link ConfigurationRepository} supporting Github as
 * the persistence storage for the baigan configuration.
 *
 * Deprecated since 0.16.0, use some more reliable repository.
 *
 * @author mchand
 */
@Deprecated
public class GitConfigurationRepository extends AbstractConfigurationRepository {
    private static final Logger LOG = LoggerFactory.getLogger(GitConfigurationRepository.class);

    private final LoadingCache<String, Map<String, Configuration>> cachedConfigurations;
    private final GitConfig gitConfig;

    public GitConfigurationRepository(long refreshIntervalInMinutes, GitConfig gitConfig) {
        this.gitConfig = gitConfig;
        cachedConfigurations = CacheBuilder.newBuilder()
                .refreshAfterWrite(refreshIntervalInMinutes, TimeUnit.MINUTES)
                .build(new GitCacheLoader(gitConfig));
    }

    @Nonnull
    @Override
    public Optional<Configuration> get(@Nonnull String key) {
        try {
            return Optional.ofNullable(cachedConfigurations.get(gitConfig.getSourceFile()).get(key));
        } catch (ExecutionException e) {
            LOG.warn("Exception while trying to get configuration for key {}", key, e);
        }
        return Optional.empty();
    }

    @Override
    public void put(@Nonnull String key, @Nonnull String value) {
        throw new UnsupportedOperationException();
    }

}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.demo.action;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.demo.aggregations.metrics.InternalPrimeCount;
import org.opensearch.demo.aggregations.metrics.PrimeNumberCountAggregationBuilder;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.SearchPlugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;

import lombok.extern.log4j.Log4j2;

/**
 * Plugin class registering various items which will be provided by this plugin.
 */
@Log4j2
public class DemoPlugin extends Plugin implements ActionPlugin, SearchPlugin {
    /**
     * @return A {@link List} of {@link RestHandler}s which will be registered by this plugin.
     */
    @Override
    public List<RestHandler> getRestHandlers(final Settings settings, final RestController restController,
            final ClusterSettings clusterSettings, final IndexScopedSettings indexScopedSettings,
            final SettingsFilter settingsFilter, final IndexNameExpressionResolver indexNameExpressionResolver,
            final Supplier nodesInCluster) {
        return Collections.singletonList(new RestDemoAction());
    }

    /**
     * Register the aggregation here so that in SearchModule this function can be called to register the aggregation
     *
     * @return a List of {@link AggregationSpec}
     */
    public List<AggregationSpec> getAggregations() {
        log.info("Registering the aggregations [{}] from HelloWorldPlugin.", PrimeNumberCountAggregationBuilder.NAME);
        final AggregationSpec spec =
                new AggregationSpec(PrimeNumberCountAggregationBuilder.NAME, PrimeNumberCountAggregationBuilder::new,
                        PrimeNumberCountAggregationBuilder.PARSER).addResultReader(InternalPrimeCount::new).setAggregatorRegistrar(PrimeNumberCountAggregationBuilder::registerAggregators);
        return Collections.singletonList(spec);
    }

}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.demo.aggregations.metrics;

import java.io.IOException;
import java.util.Map;

import org.opensearch.common.collect.List;
import org.opensearch.index.query.QueryShardContext;
import org.opensearch.search.aggregations.Aggregator;
import org.opensearch.search.aggregations.AggregatorFactories;
import org.opensearch.search.aggregations.AggregatorFactory;
import org.opensearch.search.aggregations.CardinalityUpperBound;
import org.opensearch.search.aggregations.support.CoreValuesSourceType;
import org.opensearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.aggregations.support.ValuesSourceRegistry;
import org.opensearch.search.aggregations.support.ValuesSourceType;
import org.opensearch.search.internal.SearchContext;

/**
 * A factory class that helps in building the Aggregator for the specific aggregation.
 */
public class PrimeNumberCountAggregatorFactory extends ValuesSourceAggregatorFactory {

    public PrimeNumberCountAggregatorFactory(final String name, final ValuesSourceConfig config,
            final QueryShardContext queryShardContext, final AggregatorFactory parent,
            final AggregatorFactories.Builder subFactoriesBuilder, final Map<String, Object> metadata)
            throws IOException {
        super(name, config, queryShardContext, parent, subFactoriesBuilder, metadata);
    }

    static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        builder.register(PrimeNumberCountAggregationBuilder.REGISTRY_KEY,
                List.of(new ValuesSourceType[] { CoreValuesSourceType.NUMERIC }), PrimeNumberCountAggregator::new,
                true);
    }

    @Override
    protected Aggregator createUnmapped(final SearchContext searchContext, final Aggregator aggregator,
            final Map<String, Object> map) throws IOException {
        return new PrimeNumberCountAggregator(this.name, this.config, searchContext, aggregator, metadata);
    }

    @Override
    protected Aggregator doCreateInternal(final SearchContext searchContext, final Aggregator aggregator,
            final CardinalityUpperBound cardinalityUpperBound, final Map<String, Object> map) throws IOException {
        return this.queryShardContext.getValuesSourceRegistry()
                .getAggregator(PrimeNumberCountAggregationBuilder.REGISTRY_KEY, this.config)
                .build(this.name, this.config, searchContext, aggregator, metadata);
    }
}

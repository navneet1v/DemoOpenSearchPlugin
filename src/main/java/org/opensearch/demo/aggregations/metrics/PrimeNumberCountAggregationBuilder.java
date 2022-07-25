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

import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.xcontent.ObjectParser;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.index.query.QueryShardContext;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregatorFactories;
import org.opensearch.search.aggregations.AggregatorFactory;
import org.opensearch.search.aggregations.metrics.MetricAggregatorSupplier;
import org.opensearch.search.aggregations.metrics.SumAggregationBuilder;
import org.opensearch.search.aggregations.support.CoreValuesSourceType;
import org.opensearch.search.aggregations.support.ValuesSource;
import org.opensearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.aggregations.support.ValuesSourceRegistry;
import org.opensearch.search.aggregations.support.ValuesSourceType;

/**
 * This is the builder class that will help us in creating the right builder and parsers to parse the input from the
 * users.
 */
public class PrimeNumberCountAggregationBuilder
        extends ValuesSourceAggregationBuilder.LeafOnly<ValuesSource.Numeric, SumAggregationBuilder> {

    public static final String NAME = "primeCount";
    public static final ValuesSourceRegistry.RegistryKey<MetricAggregatorSupplier> REGISTRY_KEY =
            new ValuesSourceRegistry.RegistryKey(NAME, MetricAggregatorSupplier.class);
    public static final ObjectParser<PrimeNumberCountAggregationBuilder, String> PARSER =
            ObjectParser.fromBuilder(NAME, PrimeNumberCountAggregationBuilder::new);

    public PrimeNumberCountAggregationBuilder(final String name) {
        super(name);
    }

    public PrimeNumberCountAggregationBuilder(final StreamInput in) throws IOException {
        super(in);
    }

    public PrimeNumberCountAggregationBuilder(final PrimeNumberCountAggregationBuilder clone,
            final AggregatorFactories.Builder factoriesBuilder, final Map<String, Object> metadata) {
        super(clone, factoriesBuilder, metadata);
    }

    @Override
    protected void innerWriteTo(final StreamOutput streamOutput) throws IOException {

    }

    /**
     * Called via registering the aggregator in plugin
     * @param builder
     */
    public static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        PrimeNumberCountAggregatorFactory.registerAggregators(builder);
    }

    @Override
    protected ValuesSourceRegistry.RegistryKey<?> getRegistryKey() {
        return REGISTRY_KEY;
    }

    /**
     * This tells on what type of fields that aggregation will work on in default. This values gets changed if from
     * the field mapper we found different source type
     *
     * @return ValuesSourceType
     */
    @Override
    protected ValuesSourceType defaultValueSourceType() {
        return CoreValuesSourceType.NUMERIC;
    }

    @Override
    protected PrimeNumberCountAggregatorFactory innerBuild(final QueryShardContext queryShardContext,
            final ValuesSourceConfig valuesSourceConfig, final AggregatorFactory aggregatorFactory,
            final AggregatorFactories.Builder subFactories) throws IOException {

        return new PrimeNumberCountAggregatorFactory(this.name, valuesSourceConfig, queryShardContext,
                aggregatorFactory, subFactories, this.metadata);
    }

    @Override
    protected XContentBuilder doXContentBody(final XContentBuilder xContentBuilder, final Params params)
            throws IOException {
        return xContentBuilder;
    }

    @Override
    protected AggregationBuilder shallowCopy(final AggregatorFactories.Builder builder, final Map<String, Object> map) {
        return new PrimeNumberCountAggregationBuilder(this, factoriesBuilder, metadata);
    }

    @Override
    public String getType() {
        return NAME;
    }

    static {
        ValuesSourceAggregationBuilder.declareFields(PARSER, true, true, false);
    }
}

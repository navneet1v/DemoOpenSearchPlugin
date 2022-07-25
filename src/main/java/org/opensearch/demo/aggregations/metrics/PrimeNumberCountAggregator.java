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

import org.apache.lucene.index.LeafReaderContext;
import org.opensearch.common.util.BigArrays;
import org.opensearch.common.util.IntArray;
import org.opensearch.index.fielddata.SortedNumericDoubleValues;
import org.opensearch.search.DocValueFormat;
import org.opensearch.search.aggregations.Aggregator;
import org.opensearch.search.aggregations.InternalAggregation;
import org.opensearch.search.aggregations.LeafBucketCollector;
import org.opensearch.search.aggregations.bucket.terms.LongKeyedBucketOrds;
import org.opensearch.search.aggregations.metrics.MetricsAggregator;
import org.opensearch.search.aggregations.support.ValuesSource;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.internal.SearchContext;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PrimeNumberCountAggregator extends MetricsAggregator {

    // This is the type of data we will be getting
    private final ValuesSource.Numeric valuesSource;
    private final DocValueFormat format;

    // Some variables that can be used to store the information computed in the aggregation.
    // on the same node across documents.
    private IntArray primeCounts;

    public PrimeNumberCountAggregator(String name, ValuesSourceConfig valuesSourceConfig, SearchContext context,
            Aggregator parent,
            Map<String, Object> metadata) throws IOException {
        super(name, context, parent, metadata);
        this.format = valuesSourceConfig.format();
        this.valuesSource = valuesSourceConfig.hasValues()? (ValuesSource.Numeric)valuesSourceConfig.getValuesSource() : null;
        if(valuesSource != null) {
            primeCounts = context.bigArrays().newIntArray(1L, true);
        }
    }

    @Override
    public InternalAggregation buildAggregation(long bucket) throws IOException {
        if (valuesSource != null && bucket < primeCounts.size()) {
            log.info("In the buildAggregation function {} to build internal aggregation", this.primeCounts.get(bucket));
            return new InternalPrimeCount(name, primeCounts.get(bucket), format, metadata());
        } else {
            return buildEmptyAggregation();
        }
    }

    @Override
    protected LeafBucketCollector getLeafCollector(LeafReaderContext leafReaderContext, LeafBucketCollector leafBucketCollector) throws IOException {
        log.info("Entering the LeafCollector function for the PrimeNumberCountAggregation");
        if(valuesSource == null) {
            return LeafBucketCollector.NO_OP_COLLECTOR;
        }
        // this is just for doing operations on bigArray
        final BigArrays bigArrays = this.context.bigArrays();
        // This is place where we are reading the information from Lucene.
        // The SortedNumericDoubleValues is abstraction created to fetch the information
        final SortedNumericDoubleValues values = this.valuesSource.doubleValues(leafReaderContext);

        // This is interface for collecting the information
        return new LeafBucketCollector() {
            /**
             * Collect the given {@code doc} in the bucket owned by
             * {@code owningBucketOrd}.
             * <p>
             * The implementation of this method metric aggregations is generally
             * something along the lines of
             * <pre>{@code
             * array[owningBucketOrd] += loadValueFromDoc(doc)
             * }</pre>
             * <p>Bucket aggregations have more trouble because their job is to
             * <strong>make</strong> new ordinals. So their implementation generally
             * looks kind of like
             * <pre>{@code
             * long myBucketOrd = mapOwningBucketAndValueToMyOrd(owningBucketOrd, loadValueFromDoc(doc));
             * collectBucket(doc, myBucketOrd);
             * }</pre>
             * <p>
             * Some bucket aggregations "know" how many ordinals each owning ordinal
             * needs so they can map "densely". The {@code range} aggregation, for
             * example, can perform this mapping with something like:
             * <pre>{@code
             * return rangeCount * owningBucketOrd + matchingRange(value);
             * }</pre>
             * Other aggregations don't know how many buckets will fall into any
             * particular owning bucket. The {@code terms} aggregation, for example,
             * uses {@link LongKeyedBucketOrds} which amounts to a hash lookup.
             */
            @Override
            public void collect(final int doc, final long owningBucketOrder) throws IOException {
                // Need to check in detail what we are doing with the owningBucketOrder, this is coming as 0, so why
                // we are growing the size to a min size. This is very strange
                primeCounts = bigArrays.grow(primeCounts, owningBucketOrder + 1);
                if(values.advanceExact(doc)) {
                    final int valuesCount = (int)values.nextValue();
                    // now implement the prime number logic here.
                    if(isPrime(valuesCount)) {
                        // set count to 1 if number is prime
                        primeCounts.set(owningBucketOrder, primeCounts.get(owningBucketOrder) + 1);
                    }
                }
            }
        };
    }

    /**
     * Simple logic to check a number is prime or not.
     * @param number int
     * @return boolean
     */
    private boolean isPrime(int number) {
        if(number == 1 || number == 0 ) {
            return false;
        }
        int modifiedNumber = number < 0 ? number * -1 : number;
        int root = (int)Math.sqrt(modifiedNumber);
        for(int i = 2 ; i <= root; i++) {
            if(modifiedNumber % i == 0) {
                return false;
            }
        }
        log.info("The number {} is prime.", number);
        return true;
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new InternalPrimeCount(this.name, 0, this.format, this.metadata());
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.demo.aggregations.metrics;

import org.opensearch.search.aggregations.metrics.NumericMetricsAggregation;

/**
 * An aggregation that computes the total number of prime numbers in the whole index for a specific field
 */
public interface PrimeCount extends NumericMetricsAggregation.SingleValue {

    /**
     * The count
     * @return int
     */
    int getValue();
}

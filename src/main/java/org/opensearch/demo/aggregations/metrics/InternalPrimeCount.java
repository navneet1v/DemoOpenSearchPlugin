/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.demo.aggregations.metrics;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.search.DocValueFormat;
import org.opensearch.search.aggregations.InternalAggregation;
import org.opensearch.search.aggregations.metrics.InternalNumericMetricsAggregation;

import lombok.extern.log4j.Log4j2;

/**
 * Internal PrimeCount representation used when passing the data between data nodes and coordinating node
 */
@Log4j2
public class InternalPrimeCount extends InternalNumericMetricsAggregation.SingleValue implements PrimeCount {

    private final int count;

    public InternalPrimeCount(final String name, final int count, DocValueFormat formatter,
            final Map<String, Object> metadata) {
        super(name, metadata);
        this.count = count;
        this.format = formatter;
    }

    public InternalPrimeCount(final StreamInput in) throws IOException {
        super(in);
        this.format = in.readNamedWriteable(DocValueFormat.class);
        this.count = in.readInt();
    }

    /**
     * The count of primes in all the documents.
     *
     * @return int
     */
    @Override
    public int getValue() {
        return this.count;
    }

    /**
     * Writing data on the stream which can be read by the coordinator node
     *
     * @param streamOutput {@link StreamOutput}
     * @throws IOException
     */
    @Override
    protected void doWriteTo(final StreamOutput streamOutput) throws IOException {
        streamOutput.writeNamedWriteable(this.format);
        streamOutput.writeDouble(this.count);
    }

    /**
     * This is the function which gets called on the coordinator node to reduce/combine the results from different
     * data nodes.
     *
     * @param list
     * @param reduceContext
     * @return
     */
    @Override
    public InternalPrimeCount reduce(final List<InternalAggregation> list, final ReduceContext reduceContext) {
        log.info("Doing the reduce on the Primes Count");
        int primesCount = list.stream().map(agg -> ((InternalPrimeCount) agg).count).reduce(0, Integer::sum);
        return new InternalPrimeCount(this.name, primesCount, this.format, this.getMetadata());

    }

    /**
     * Serialize the data in the json.
     * @param xContentBuilder XContentBuilder
     * @param params Params
     * @return XContentBuilder
     * @throws IOException
     */
    @Override
    public XContentBuilder doXContentBody(final XContentBuilder xContentBuilder, final Params params)
            throws IOException {
        xContentBuilder.field(CommonFields.VALUE.getPreferredName(), this.count);
        if(this.format != DocValueFormat.RAW) {
            xContentBuilder.field(CommonFields.VALUE_AS_STRING.getPreferredName(),
                    this.format.format(this.count).toString());
        }
        return xContentBuilder;
    }

    @Override
    public String getWriteableName() {
        return "primeCount";
    }

    @Override
    public double value() {
        return this.count;
    }
}

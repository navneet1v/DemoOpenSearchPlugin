/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.demo.aggregations.metrics;

import java.io.IOException;

import org.opensearch.common.xcontent.ObjectParser;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.XContentParser;
import org.opensearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@NoArgsConstructor
@Log4j2
public class ParsedPrimeCount extends ParsedSingleValueNumericMetricsAggregation implements PrimeCount {
    private static final ObjectParser<ParsedPrimeCount, Void> PARSER =
            new ObjectParser(ParsedPrimeCount.class.getSimpleName(), true, ParsedPrimeCount::new);

    /**
     * The count
     *
     * @return int
     */
    @Override
    public int getValue() {
        return (int)this.value;
    }

    @Override
    protected XContentBuilder doXContentBody(XContentBuilder xContentBuilder, Params params) throws IOException {
        log.info("Creating XContent Body for Parsed Prime");
        xContentBuilder.field(CommonFields.VALUE.getPreferredName(), this.value);
        if (this.valueAsString != null) {
            xContentBuilder.field(CommonFields.VALUE_AS_STRING.getPreferredName(), this.valueAsString);
        }
        return xContentBuilder;
    }

    /**
     * Converts the json object to the ParsedPrime class.
     *
     * @param parser {@link XContentParser}
     * @param name
     * @return ParsedPrime
     */
    public static ParsedPrimeCount fromXContent(XContentParser parser, String name) {
        log.info("In the parsed Prime fromXContent {}, {}", parser, name);
        final ParsedPrimeCount parsedPrimeCount = PARSER.apply(parser, null);
        parsedPrimeCount.setName(name);
        return parsedPrimeCount;
    }

    @Override
    public String getType() {
        return "primeCount";
    }

    // Setting up the default value
    static {
        declareSingleValueFields(PARSER, Double.NEGATIVE_INFINITY);
    }
}

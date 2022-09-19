/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.demo.processor;

import java.util.Map;
import java.util.function.BiConsumer;

import org.opensearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.opensearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.opensearch.client.Client;
import org.opensearch.ingest.AbstractProcessor;
import org.opensearch.ingest.IngestDocument;
import org.opensearch.ingest.Processor;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NLPProcessor extends AbstractProcessor {

    private final Client client;

    public static final String TYPE = "nlpProcessor";

    protected NLPProcessor(final String tag, final String description, final Client client) {
        super(tag, description);
        this.client = client;
    }

    @Override
    public void execute(final IngestDocument ingestDocument, final BiConsumer<IngestDocument, Exception> handler) {
        super.execute(ingestDocument, handler);
    }

    @Override
    public IngestDocument execute(final IngestDocument ingestDocument) throws Exception {
        log.info("I am in the execute");
        ingestDocument.setFieldValue("navneet", "MyNLPProcessor");
        ClusterStatsResponse clusterStatsResponse =
                client.admin().cluster().clusterStats(new ClusterStatsRequest()).actionGet();
        log.info("Response: " + clusterStatsResponse.toString());
        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        private final Client client;

        public Factory(Client client) {
            this.client = client;
        }

        @Override
        public Processor create(final Map<String, Processor.Factory> map, final String processorTag,
                final String description, final Map<String, Object> config) throws Exception {
            return new NLPProcessor(processorTag, description, client);
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.demo.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

public class RestDemoAction extends BaseRestHandler {
    @Override
    public String getName() {
        return "rest_handler_of_hello_world";
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        String name = request.hasContent() ? request.contentParser().mapStrings().get("name") : "";

        return channel -> {
            try {
                channel.sendResponse(DemoService.buildResponse(name));
            } catch (final Exception e) {
                channel.sendResponse(new BytesRestResponse(channel, e));
            }
        };
    }

    @Override
    public List<Route> routes() {
        List<Route> routeList = new ArrayList<>();
        routeList.add(new Route(RestRequest.Method.GET, "/_plugins/hello_world"));
        routeList.add(new Route(RestRequest.Method.POST, "/_plugins/hello_world"));
        return Collections.unmodifiableList(routeList);
    }
}

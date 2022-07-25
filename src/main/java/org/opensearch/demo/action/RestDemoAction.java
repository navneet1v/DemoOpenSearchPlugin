/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.demo.action;

import java.io.IOException;
import java.util.List;

import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

/**
 * This is RestAction which handles the incoming request for the URLs registered via routes() . There can be many
 * rest action, which a plugin can register. In OpenSearch we call Handlers as Action. But this is just a name which
 * has stick around.
 */
public class RestDemoAction extends BaseRestHandler {
    private static final String HELLO_WORLD_ROUTE_PATH = "/_plugins/hello_world";
    private static final String HANDLER_OR_ACTION_NAME = "rest_handler_of_hello_world";

    @Override
    public String getName() {
        return HANDLER_OR_ACTION_NAME;
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

    /**
     * List of Routes to be handled by this Hanlder/Action
     *
     * @return A {@link List} of {@link Route}s
     */
    @Override
    public List<Route> routes() {
        return List.of(new Route(RestRequest.Method.GET, HELLO_WORLD_ROUTE_PATH),
                new Route(RestRequest.Method.POST, HELLO_WORLD_ROUTE_PATH));
    }
}

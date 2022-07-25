/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.demo.action;

import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.RestStatus;

/**
 * A test service
 */
public class DemoService {

    public static RestResponse buildResponse(String name) {
        final String space = name.isEmpty() ? "" : " ";
        final StringBuilder sb = new StringBuilder().append("Hi").append(space).append(name)
                .append("! Your plugin is" + " installed and working:)");
        return new BytesRestResponse(RestStatus.OK, sb.toString());
    }
}

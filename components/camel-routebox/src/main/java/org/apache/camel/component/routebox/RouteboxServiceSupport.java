/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.routebox;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.ServiceSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class RouteboxServiceSupport extends ServiceSupport {
    private static final transient Log LOG = LogFactory.getLog(RouteboxServiceSupport.class);
    private RouteboxEndpoint endpoint;
    private ExecutorService executor;
    private int pendingExchanges;
    private boolean startedInnerContext;
    
    public RouteboxServiceSupport(RouteboxEndpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    protected void doStopInnerContext() throws Exception {
        CamelContext context = endpoint.getConfig().getInnerContext();
        context.stop();
        setStartedInnerContext(false);
    }

    protected void doStartInnerContext() throws Exception {
        // Add Route Builders and definitions to the inner camel context and start the context
        CamelContext context = endpoint.getConfig().getInnerContext();
        List<RouteBuilder> routeBuildersList = endpoint.getConfig().getRouteBuilders();
        if (!(routeBuildersList.isEmpty())) {
            for (RouteBuilder routeBuilder : routeBuildersList) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding routebuilder " + routeBuilder + " to " + context.getName());
                }
                context.addRoutes(routeBuilder);
            }
        }       
        
        context.start();
        setStartedInnerContext(true);
    }

    public void setPendingExchanges(int pendingExchanges) {
        this.pendingExchanges = pendingExchanges;
    }

    public int getPendingExchanges() {
        return pendingExchanges;
    }

    public RouteboxEndpoint getRouteboxEndpoint() {
        return endpoint;
    }

    public void setRouteboxEndpoint(RouteboxEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }


    public void setStartedInnerContext(boolean startedInnerContext) {
        this.startedInnerContext = startedInnerContext;
    }


    public boolean isStartedInnerContext() {
        return startedInnerContext;
    }

}

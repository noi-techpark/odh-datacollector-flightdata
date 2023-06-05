// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.datacollector.flightdata;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;

import javax.enterprise.context.ApplicationScoped;

/**
 * This class sets configuration values for Websockets.
 */
@ApplicationScoped
public class WebsocketConfig extends RouteBuilder {

    @Override
    public void configure() {
        ((WebsocketComponent) getContext().getComponent("websocket")).setMaxThreads(5);
    }

}

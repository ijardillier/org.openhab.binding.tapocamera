/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tapocamera.internal;

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.THING_TYPE_CAMERA;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApi;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApiFactory;

/**
 * The {@link TapoCameraHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.tapocamera", service = ThingHandlerFactory.class)
public class TapoCameraHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_CAMERA);

    private final TapoCameraApiFactory apiFactory;

    @Activate
    public TapoCameraHandlerFactory(@Reference TapoCameraApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_CAMERA.equals(thingTypeUID)) {
            try {
                TapoCameraApi api = apiFactory.getApi();
                TapoCameraHandler handler = new TapoCameraHandler(thing, api);
                api.setDevice(handler);
                return handler;
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}

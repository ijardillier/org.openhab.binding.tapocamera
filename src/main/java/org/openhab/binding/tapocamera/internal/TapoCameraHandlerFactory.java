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

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.THING_TYPE_BRIDGE;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.THING_TYPE_CAMERA;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApi;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApiFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link TapoCameraHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.tapocamera", service = ThingHandlerFactory.class)
public class TapoCameraHandlerFactory extends BaseThingHandlerFactory {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_CAMERA, THING_TYPE_BRIDGE);
    private final TapoCameraApiFactory apiFactory;
    private static final Map<ThingUID, @NonNull TapoCameraHandler> handlerMap = new HashMap<>();

    /**
     * Instantiates a new Tapo camera handler factory.
     *
     * @param apiFactory the api factory
     */
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

        TapoCameraApi api = apiFactory.getApi();
        if (THING_TYPE_CAMERA.equals(thingTypeUID)) {
            return new TapoCameraHandler(thing, api);
        } else if (THING_TYPE_BRIDGE.equals(thingTypeUID)) {
            return new TapoCameraBridge((Bridge) thing, apiFactory);
        }

        return null;
    }

    @Override
    public void unregisterHandler(Thing thing) {
        ThingUID uid = thing.getUID();
        if (!handlerMap.isEmpty()) {
            handlerMap.remove(uid);
        }
        super.unregisterHandler(thing);
    }

    @Override
    public ThingHandler registerHandler(Thing thing) {
        ThingHandler handler = super.registerHandler(thing);
        if (handler instanceof TapoCameraHandler) {
            ThingUID uid = handler.getThing().getUID();
            handlerMap.putIfAbsent(uid, (TapoCameraHandler) handler);
        }
        return handler;
    }

    public static TapoCameraHandler getThingHandlerByThingUID(ThingUID uid) {
        if (!handlerMap.isEmpty() && handlerMap.containsKey(uid)) {
            TapoCameraHandler handler = handlerMap.get(uid);
            if (handler != null) {
                return handler;
            } else {
                throw new RuntimeException("TapoCameraHandler is null");
            }
        } else {
            throw new RuntimeException(String.format("TapoCameraHandler with uid '%s' not found", uid));
        }
    }
}

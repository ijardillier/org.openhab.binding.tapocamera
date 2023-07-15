/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *  SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api.dto.firmware;

/**
 * The type Firmware update info.
 */
public class FirmwareUpdateInfo {
    /**
     * The Connect status.
     */
    public String connect_status;
    /**
     * The Disconnect reason.
     */
    public String disconnect_reason;
    /**
     * The Fw download progress.
     */
    public String fw_download_progress;

    /**
     * The Fw download status.
     */
    public String fw_download_status;
    /**
     * The Reconnect time.
     */
    public String reconnect_time;
    /**
     * The Fw verify status.
     */
    public String fw_verify_status;
}

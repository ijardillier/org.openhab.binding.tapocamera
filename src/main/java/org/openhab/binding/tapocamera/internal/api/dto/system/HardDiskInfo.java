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

package org.openhab.binding.tapocamera.internal.api.dto.system;

/**
 * The type Hard disk info.
 */
public class HardDiskInfo {
    /**
     * The Disk name.
     */
    public String disk_name; // String",
    /**
     * The Rw attr.
     */
    public String rw_attr; // String",
    /**
     * The Detect status.
     */
    public String detect_status; // String",
    /**
     * The Write protect.
     */
    public Integer write_protect; // String",
    /**
     * The Msg push free space.
     */
    public String msg_push_free_space; // String",
    /**
     * The Picture free space.
     */
    public String picture_free_space; // String",
    /**
     * The Free space.
     */
    public String free_space; // String",
    /**
     * The Video free space.
     */
    public String video_free_space; // String",
    /**
     * The Percent.
     */
    public Double percent; // Float",
    /**
     * The Record duration.
     */
    public Integer record_duration; // String",
    /**
     * The Record free duration.
     */
    public Integer record_free_duration; // String",
    /**
     * The Record start time.
     */
    public String record_start_time; // String",
    /**
     * The Status.
     */
    public String status; // String",
    /**
     * The Msg push total space.
     */
    public String msg_push_total_space; // String",
    /**
     * The Picture total space.
     */
    public String picture_total_space; // String",
    /**
     * The Total space.
     */
    public String total_space; // String",
    /**
     * The Video total space.
     */
    public String video_total_space; // String",
    /**
     * The Type.
     */
    public String type; // String"

    /**
     * The Loop record status.
     */
    public Integer loop_record_status;
}

/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information.
 *
 * This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.dto;

import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_AlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_IntrusionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_MotionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_PeopleDetection;

public class CameraState {
    private String ledStatus;

    private Old_AlarmInfo oldAlarmInfo = new Old_AlarmInfo();
    private Old_MotionDetection oldMotionDetection = new Old_MotionDetection();
    private Old_PeopleDetection oldPeopleDetection = new Old_PeopleDetection();

    private Old_LineCrossingDetection oldLineCrossingDetection = new Old_LineCrossingDetection();

    private Old_IntrusionDetection oldIntrusionDetection = new Old_IntrusionDetection();

    public CameraState() {
    }

    public String getLedStatus() {
        return ledStatus;
    }

    public void setLedStatus(String ledStatus) {
        this.ledStatus = ledStatus;
    }

    public Old_AlarmInfo getAlarmInfo() {
        return oldAlarmInfo;
    }

    public void setAlarmInfo(Old_AlarmInfo oldAlarmInfo) {
        this.oldAlarmInfo = oldAlarmInfo;
    }

    public Old_MotionDetection getMotionDetection() {
        return oldMotionDetection;
    }

    public void setMotionDetection(Old_MotionDetection oldMotionDetection) {
        this.oldMotionDetection = oldMotionDetection;
    }

    public Old_PeopleDetection getPeopleDetection() {
        return oldPeopleDetection;
    }

    public void setPeopleDetection(Old_PeopleDetection oldPeopleDetection) {
        this.oldPeopleDetection = oldPeopleDetection;
    }

    public Old_LineCrossingDetection getLineCrossingDetection() {
        return oldLineCrossingDetection;
    }

    public void setLineCrossingDetection(Old_LineCrossingDetection oldLineCrossingDetection) {
        this.oldLineCrossingDetection = oldLineCrossingDetection;
    }

    public Old_IntrusionDetection getIntrusionDetection() {
        return oldIntrusionDetection;
    }

    public void setIntrusionDetection(Old_IntrusionDetection oldIntrusionDetection) {
        this.oldIntrusionDetection = oldIntrusionDetection;
    }
}

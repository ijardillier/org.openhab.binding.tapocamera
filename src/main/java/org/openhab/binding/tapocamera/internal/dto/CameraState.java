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

public class CameraState {
    private String ledStatus;

    private AlarmInfo alarmInfo = new AlarmInfo();
    private MotionDetection motionDetection = new MotionDetection();
    private PeopleDetection peopleDetection = new PeopleDetection();

    public CameraState() {
    }

    public String getLedStatus() {
        return ledStatus;
    }

    public void setLedStatus(String ledStatus) {
        this.ledStatus = ledStatus;
    }

    public AlarmInfo getAlarmInfo() {
        return alarmInfo;
    }

    public void setAlarmInfo(AlarmInfo alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    public MotionDetection getMotionDetection() {
        return motionDetection;
    }

    public void setMotionDetection(MotionDetection motionDetection) {
        this.motionDetection = motionDetection;
    }

    public PeopleDetection getPeopleDetection() {
        return peopleDetection;
    }

    public void setPeopleDetection(PeopleDetection peopleDetection) {
        this.peopleDetection = peopleDetection;
    }
}

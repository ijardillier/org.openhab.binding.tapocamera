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

import org.openhab.binding.tapocamera.internal.api.v2.dto.alarm.MsgAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.IntrusionDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.LineCrossingDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.MotionDetection;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.PersonDetectionInfo;

public class CameraState {
    private String ledStatus;

    private MsgAlarmInfo alarmInfo = new MsgAlarmInfo();
    private MotionDetection motionDetection = new MotionDetection();
    private PersonDetectionInfo personDetectionInfo = new PersonDetectionInfo();

    private LineCrossingDetectionInfo lineCrossingDetectionInfo = new LineCrossingDetectionInfo();

    private IntrusionDetectionInfo intrusionDetectionInfo = new IntrusionDetectionInfo();

    private int speakerVolume = 0;
    private int microphoneVolume = 0;

    public CameraState() {
    }

    public String getLedStatus() {
        return ledStatus;
    }

    public void setLedStatus(String ledStatus) {
        this.ledStatus = ledStatus;
    }

    public MsgAlarmInfo getAlarmInfo() {
        return alarmInfo;
    }

    public void setAlarmInfo(MsgAlarmInfo alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    public MotionDetection getMotionDetection() {
        return motionDetection;
    }

    public void setMotionDetection(MotionDetection motionDetection) {
        this.motionDetection = motionDetection;
    }

    public PersonDetectionInfo getPersonDetectionInfo() {
        return personDetectionInfo;
    }

    public void setPersonDetectionInfo(PersonDetectionInfo personDetectionInfo) {
        this.personDetectionInfo = personDetectionInfo;
    }

    public LineCrossingDetectionInfo getLineCrossingDetection() {
        return lineCrossingDetectionInfo;
    }

    public void setLineCrossingDetection(LineCrossingDetectionInfo lineCrossingDetectionInfo) {
        this.lineCrossingDetectionInfo = lineCrossingDetectionInfo;
    }

    public IntrusionDetectionInfo getIntrusionDetection() {
        return intrusionDetectionInfo;
    }

    public void setIntrusionDetection(IntrusionDetectionInfo intrusionDetectionInfo) {
        this.intrusionDetectionInfo = intrusionDetectionInfo;
    }

    public int getSpeakerVolume() {
        return speakerVolume;
    }

    public void setSpeakerVolume(int speakerVolume) {
        this.speakerVolume = speakerVolume;
    }

    public int getMicrophoneVolume() {
        return microphoneVolume;
    }

    public void setMicrophoneVolume(int microphoneVolume) {
        this.microphoneVolume = microphoneVolume;
    }
}

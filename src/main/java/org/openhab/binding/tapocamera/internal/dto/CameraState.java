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

import org.openhab.binding.tapocamera.internal.api.dto.alarm.MsgAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.IntrusionDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.LineCrossingDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.MotionDetection;
import org.openhab.binding.tapocamera.internal.api.dto.detection.PersonDetectionInfo;

/**
 * The type Camera state.
 */
public class CameraState {
    private String friendlyName;
    private String ledStatus;

    private MsgAlarmInfo alarmInfo = new MsgAlarmInfo();
    private MotionDetection motionDetection = new MotionDetection();
    private PersonDetectionInfo personDetectionInfo = new PersonDetectionInfo();

    private LineCrossingDetectionInfo lineCrossingDetectionInfo = new LineCrossingDetectionInfo();

    private IntrusionDetectionInfo intrusionDetectionInfo = new IntrusionDetectionInfo();

    private int speakerVolume = 0;
    private int microphoneVolume = 0;

    /**
     * Instantiates a new Camera state.
     */
    public CameraState() {
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Gets led status.
     *
     * @return the led status
     */
    public String getLedStatus() {
        return ledStatus;
    }

    /**
     * Sets led status.
     *
     * @param ledStatus the led status
     */
    public void setLedStatus(String ledStatus) {
        this.ledStatus = ledStatus;
    }

    /**
     * Gets alarm info.
     *
     * @return the alarm info
     */
    public MsgAlarmInfo getAlarmInfo() {
        return alarmInfo;
    }

    /**
     * Sets alarm info.
     *
     * @param alarmInfo the alarm info
     */
    public void setAlarmInfo(MsgAlarmInfo alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    /**
     * Gets motion detection.
     *
     * @return the motion detection
     */
    public MotionDetection getMotionDetection() {
        return motionDetection;
    }

    /**
     * Sets motion detection.
     *
     * @param motionDetection the motion detection
     */
    public void setMotionDetection(MotionDetection motionDetection) {
        this.motionDetection = motionDetection;
    }

    /**
     * Gets person detection info.
     *
     * @return the person detection info
     */
    public PersonDetectionInfo getPersonDetectionInfo() {
        return personDetectionInfo;
    }

    /**
     * Sets person detection info.
     *
     * @param personDetectionInfo the person detection info
     */
    public void setPersonDetectionInfo(PersonDetectionInfo personDetectionInfo) {
        this.personDetectionInfo = personDetectionInfo;
    }

    /**
     * Gets line crossing detection.
     *
     * @return the line crossing detection
     */
    public LineCrossingDetectionInfo getLineCrossingDetection() {
        return lineCrossingDetectionInfo;
    }

    /**
     * Sets line crossing detection.
     *
     * @param lineCrossingDetectionInfo the line crossing detection info
     */
    public void setLineCrossingDetection(LineCrossingDetectionInfo lineCrossingDetectionInfo) {
        this.lineCrossingDetectionInfo = lineCrossingDetectionInfo;
    }

    /**
     * Gets intrusion detection.
     *
     * @return the intrusion detection
     */
    public IntrusionDetectionInfo getIntrusionDetection() {
        return intrusionDetectionInfo;
    }

    /**
     * Sets intrusion detection.
     *
     * @param intrusionDetectionInfo the intrusion detection info
     */
    public void setIntrusionDetection(IntrusionDetectionInfo intrusionDetectionInfo) {
        this.intrusionDetectionInfo = intrusionDetectionInfo;
    }

    /**
     * Gets speaker volume.
     *
     * @return the speaker volume
     */
    public int getSpeakerVolume() {
        return speakerVolume;
    }

    /**
     * Sets speaker volume.
     *
     * @param speakerVolume the speaker volume
     */
    public void setSpeakerVolume(int speakerVolume) {
        this.speakerVolume = speakerVolume;
    }

    /**
     * Gets microphone volume.
     *
     * @return the microphone volume
     */
    public int getMicrophoneVolume() {
        return microphoneVolume;
    }

    /**
     * Sets microphone volume.
     *
     * @param microphoneVolume the microphone volume
     */
    public void setMicrophoneVolume(int microphoneVolume) {
        this.microphoneVolume = microphoneVolume;
    }
}

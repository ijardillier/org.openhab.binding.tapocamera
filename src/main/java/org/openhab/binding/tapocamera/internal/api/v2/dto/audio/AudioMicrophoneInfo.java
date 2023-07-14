package org.openhab.binding.tapocamera.internal.api.v2.dto.audio;

public class AudioMicrophoneInfo {
    public Integer sampling_rate; // "8",
    public Integer channels; // "1",
    public String encode_type; // "G711alaw",
    public String mute; // "off",
    public String noise_cancelling; // "on",
    public Integer volume; //  "100"
}
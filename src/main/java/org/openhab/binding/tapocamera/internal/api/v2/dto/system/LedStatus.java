package org.openhab.binding.tapocamera.internal.api.v2.dto.system;

public class LedStatus {
    public String enabled;

    @Override
    public String toString() {
        return "LedStatus{" +
                "enabled='" + enabled + '\'' +
                '}';
    }
}

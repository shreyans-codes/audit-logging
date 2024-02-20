package com.sheru.AuditLogging.Utils;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface LogLevels {
    public static final Marker TASK = MarkerFactory.getMarker("TASK");
    public static final Marker CR = MarkerFactory.getMarker("CR");
}

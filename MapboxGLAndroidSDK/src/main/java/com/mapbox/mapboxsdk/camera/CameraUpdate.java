package com.mapbox.mapboxsdk.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Interface definition for camera updates.
 */
public interface CameraUpdate {

  /**
   * Get the camera position from the camera update.
   *
   * @param mapboxMap Map object to build the position from
   * @return the camera position from the implementing camera update
   */
  @Nullable
  CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap);

}


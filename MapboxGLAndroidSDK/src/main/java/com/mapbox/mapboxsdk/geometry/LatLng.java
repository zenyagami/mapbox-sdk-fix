package com.mapbox.mapboxsdk.geometry;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.FloatRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.constants.GeometryConstants;
import com.mapbox.turf.TurfMeasurement;

import static com.mapbox.turf.TurfConstants.UNIT_METRES;


/**
 * A geographical location which contains a single latitude, longitude pair, with
 * optional altitude.
 * <p>
 * Latitude and longitude are expressed as decimal degrees
 * in the WGS84 datum. By default, altitude is 0.0, or sea level.
 * </p>
 * <p>
 * Mapbox GL displays maps in the Mercator Projection and projects geographical
 * data automatically, so all data enters in the WGS84 datum.
 * </p>
 */
public class LatLng implements Parcelable {

  /**
   * Inner class responsible for recreating Parcels into objects.
   */
  public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
    public LatLng createFromParcel(@NonNull Parcel in) {
      return new LatLng(in);
    }

    public LatLng[] newArray(int size) {
      return new LatLng[size];
    }
  };

  @Keep
  private double latitude;
  @Keep
  private double longitude;
  private double altitude = 0.0;

  /**
   * Construct a new latitude, longitude point at (0, 0)
   */
  public LatLng() {
    this.latitude = 0.0;
    this.longitude = 0.0;
  }

  /**
   * Construct a new latitude, longitude point given double arguments
   *
   * @param latitude  Latitude in degrees
   * @param longitude Longitude in degrees
   */
  @Keep
  public LatLng(double latitude, double longitude) {
    setLatitude(latitude);
    setLongitude(longitude);
  }

  /**
   * Construct a new latitude, longitude, altitude point given double arguments
   *
   * @param latitude  Latitude in degrees
   * @param longitude Longitude in degress
   * @param altitude  Altitude in meters
   */
  public LatLng(double latitude, double longitude, double altitude) {
    setLatitude(latitude);
    setLongitude(longitude);
    setAltitude(altitude);
  }

  /**
   * Construct a new latitude, longitude, altitude point given location argument
   *
   * @param location Android Location
   */
  public LatLng(Location location) {
    this(location.getLatitude(), location.getLongitude(), location.getAltitude());
  }

  /**
   * Construct a new latitude, longitude, altitude point given another latitude, longitude, altitude point.
   *
   * @param latLng LatLng to be cloned.
   */
  public LatLng(LatLng latLng) {
    this.latitude = latLng.latitude;
    this.longitude = latLng.longitude;
    this.altitude = latLng.altitude;
  }

  /**
   * Constructs a new latitude, longitude, altitude tuple given a parcel.
   *
   * @param in the parcel containing the latitude, longitude, altitude values
   */
  protected LatLng(Parcel in) {
    setLatitude(in.readDouble());
    setLongitude(in.readDouble());
    setAltitude(in.readDouble());
  }

  /**
   * Set the latitude, in degrees.
   * <p>
   * This value is in the range of [-90, 90], see {@link GeometryConstants#MIN_LATITUDE} and
   * {@link GeometryConstants#MAX_LATITUDE}
   * </p>
   *
   * @param latitude the latitude value in degrees
   * @see GeometryConstants#MIN_LATITUDE
   * @see GeometryConstants#MAX_LATITUDE
   */
  public void setLatitude(
    @FloatRange(from = GeometryConstants.MIN_LATITUDE, to = GeometryConstants.MAX_LATITUDE) double latitude) {
    if (Double.isNaN(latitude)) {
      //throw new IllegalArgumentException("latitude must not be NaN");
      return;
    }
    if (Math.abs(latitude) > GeometryConstants.MAX_LATITUDE) {
      //throw new IllegalArgumentException("latitude must be between -90 and 90");
      return;
    }
    this.latitude = latitude;
  }

  /**
   * Get the latitude, in degrees.
   * <p>
   * This value is in the range of [-90, 90], see {@link GeometryConstants#MIN_LATITUDE} and
   * {@link GeometryConstants#MAX_LATITUDE}
   * </p>
   *
   * @return the latitude value in degrees
   * @see GeometryConstants#MIN_LATITUDE
   * @see GeometryConstants#MAX_LATITUDE
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Set the longitude, in degrees.
   * <p>
   * This value is in the range of [-180, 180], see {@link GeometryConstants#MIN_LONGITUDE} and
   * {@link GeometryConstants#MAX_LONGITUDE}
   * </p>
   *
   * @param longitude the longitude value in degrees
   * @see GeometryConstants#MIN_LONGITUDE
   * @see GeometryConstants#MAX_LONGITUDE
   */
  public void setLongitude(@FloatRange(from = GeometryConstants.MIN_LONGITUDE, to = GeometryConstants.MAX_LONGITUDE)
                             double longitude) {
    if (Double.isNaN(longitude)) {
      //throw new IllegalArgumentException("longitude must not be NaN");
      return;
    }
    if (Double.isInfinite(longitude)) {
      //throw new IllegalArgumentException("longitude must not be infinite");
      return;
    }
    this.longitude = longitude;
  }

  /**
   * Get the longitude, in degrees.
   * <p>
   * This value is in the range of [-180, 180], see {@link GeometryConstants#MIN_LONGITUDE} and
   * {@link GeometryConstants#MAX_LONGITUDE}
   * </p>
   *
   * @return the longitude value in degrees
   * @see GeometryConstants#MIN_LONGITUDE
   * @see GeometryConstants#MAX_LONGITUDE
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Set the altitude, in meters.
   *
   * @param altitude the altitude in meters
   */
  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  /**
   * Get the altitude, in meters.
   *
   * @return the altitude value in meters
   */
  public double getAltitude() {
    return altitude;
  }

  /**
   * Return a new LatLng object with a wrapped Longitude.  This allows original data object
   * to remain unchanged.
   *
   * @return new LatLng object with wrapped Longitude
   */
  @NonNull
  public LatLng wrap() {
    return new LatLng(latitude, wrap(longitude,
      GeometryConstants.MIN_WRAP_LONGITUDE, GeometryConstants.MAX_WRAP_LONGITUDE)
    );
  }


  /**
   * Constrains value to the given range (including min & max) via modular arithmetic.
   * <p>
   * Same formula as used in Core GL (wrap.hpp)
   * std::fmod((std::fmod((value - min), d) + d), d) + min;
   * </p>
   * <p>
   * Multiples of max value will be wrapped to max.
   * </p>
   *
   * @param value Value to wrap
   * @param min   Minimum value
   * @param max   Maximum value
   * @return Wrapped value
   */
  static double wrap(double value, double min, double max) {
    double delta = max - min;

    double firstMod = (value - min) % delta;
    double secondMod = (firstMod + delta) % delta;

    if (value >= max && secondMod == 0) {
      return max;
    }
    return secondMod + min;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object The object to compare
   * @return True if equal, false if not
   */
  @Override
  public boolean equals(@Nullable Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    LatLng latLng = (LatLng) object;

    return Double.compare(latLng.altitude, altitude) == 0 && Double.compare(latLng.latitude, latitude) == 0
      && Double.compare(latLng.longitude, longitude) == 0;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return the hash code value
   */
  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(altitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return the string representation
   */
  @NonNull
  @Override
  public String toString() {
    return "LatLng [latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + "]";
  }

  /**
   * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
   *
   * @return a bitmask indicating the set of special object types marshaled by this Parcelable object instance.
   */
  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * Flatten this object in to a Parcel.
   *
   * @param out   The Parcel in which the object should be written.
   * @param flags Additional flags about how the object should be written
   */
  @Override
  public void writeToParcel(@NonNull Parcel out, int flags) {
    out.writeDouble(latitude);
    out.writeDouble(longitude);
    out.writeDouble(altitude);
  }

  /**
   * Calculate distance between two points
   *
   * @param other Other LatLng to compare to
   * @return distance in meters
   */
  public double distanceTo(@NonNull LatLng other) {
    return TurfMeasurement.distance(
      Point.fromLngLat(longitude, latitude),
      Point.fromLngLat(other.getLongitude(), other.getLatitude()),
      UNIT_METRES
    );
  }
}

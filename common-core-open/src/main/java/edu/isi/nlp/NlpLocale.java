package edu.isi.nlp;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.icu.util.ULocale;

/**
 * This is a wrapper for ICU Locale which handles serialization
 *
 * @author Ryan Gabbard
 */
public final class NlpLocale {

  private final ULocale icuLocale;

  private NlpLocale(final ULocale icuLocale) {
    this.icuLocale = checkNotNull(icuLocale);
  }

  @JsonCreator
  public static NlpLocale forLocaleString(@JsonProperty("localeString") String localeString) {
    return new NlpLocale(new ULocale(localeString));
  }

  public static NlpLocale forIcuLocale(ULocale icuLocale) {
    return new NlpLocale(icuLocale);
  }

  @JsonProperty("localeString")
  protected String localeString() {
    return icuLocale.getName();
  }

  public ULocale asIcuLocale() {
    return icuLocale;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final NlpLocale that = (NlpLocale) o;

    return icuLocale != null ? icuLocale.equals(that.icuLocale) : that.icuLocale == null;
  }

  @Override
  public int hashCode() {
    return icuLocale != null ? icuLocale.hashCode() : 0;
  }

  @Override
  public String toString() {
    return localeString();
  }
}

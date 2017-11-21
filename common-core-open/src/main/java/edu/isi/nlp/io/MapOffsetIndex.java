package edu.isi.nlp.io;

import edu.isi.nlp.strings.offsets.ByteOffset;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.symbols.Symbol;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

/* package-private */ class MapOffsetIndex implements OffsetIndex {

  private final ImmutableMap<Symbol, OffsetRange<ByteOffset>> map;

  private MapOffsetIndex(
      final Map<Symbol, OffsetRange<ByteOffset>> map) {
    this.map = ImmutableMap.copyOf(map);
  }

  public static MapOffsetIndex fromMap(Map<Symbol, OffsetRange<ByteOffset>> map) {
    return new MapOffsetIndex(map);
  }


  @Override
  public Optional<OffsetRange<ByteOffset>> byteOffsetsOf(final Symbol key) {
    return Optional.fromNullable(map.get(key));
  }

  @Override
  public Set<Symbol> keySet() {
    return map.keySet();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(map);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MapOffsetIndex other = (MapOffsetIndex) obj;
    return Objects.equal(this.map, other.map);
  }
}

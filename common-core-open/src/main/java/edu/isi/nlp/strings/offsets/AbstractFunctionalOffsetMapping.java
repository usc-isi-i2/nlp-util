package edu.isi.nlp.strings.offsets;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import java.util.Collection;

public abstract class AbstractFunctionalOffsetMapping implements FunctionalOffsetMapping {

  @Override
  public Optional<Integer> mapOffsetUniquely(int sourceIdx) {
    final Collection<Integer> mappings = mapOffset(sourceIdx);
    checkState(
        mappings.size() < 2,
        "%s does not obey the requirements of the FunctionalOffsetMapping"
            + " interface: %s maps to %s",
        this.getClass(),
        sourceIdx,
        mappings);
    if (!mappings.isEmpty()) {
      // will never be null by check above
      return Optional.of(Iterables.getFirst(mappings, null));
    } else {
      return Optional.absent();
    }
  }
}

package edu.isi.nlp.parsing;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import edu.isi.nlp.ConstituentNode;

@Beta
interface HeadRule<NodeT extends ConstituentNode<NodeT, ?>> {

  Optional<NodeT> matchForChildren(final Iterable<NodeT> children);
}

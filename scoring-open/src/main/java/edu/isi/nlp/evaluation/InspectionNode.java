package edu.isi.nlp.evaluation;

import com.google.common.annotations.Beta;
import edu.isi.nlp.Finishable;
import edu.isi.nlp.Inspector;

/** An inspection tree node which inspects things. See {@link InspectorTreeDSL}. */
@Beta
public class InspectionNode<InT> extends InspectorTreeNode<InT>
    implements Inspector<InT>, Finishable {

  @Override
  public void inspect(final InT item) {
    for (final Inspector<InT> consumer : consumers()) {
      consumer.inspect(item);
    }
  }
}

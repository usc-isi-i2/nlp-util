package edu.isi.nlp.graphviz;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import edu.isi.nlp.StringUtils;
import java.util.List;

/** Very beta. Do not use for anything but throw-away code. */
@Beta
public final class Edge {

  private final Node source;
  private final Node target;
  private final String label;
  private final double penWidth;
  private final double arrowSize;
  private final int weight;
  private final ImmutableSet<String> styles;

  private static final double DEFAULT_ARROW_SIZE = 1.0;
  private static final Range<Double> LEGAL_ARROW_SIZES = Range.atLeast(0.0);
  private static final double DEFAULT_PEN_WIDTH = 1.0;
  private static final Range<Double> LEGAL_PEN_WIDTHS = Range.atLeast(0.0);
  private static final int DEFAULT_WEIGHT = 1;
  private static final Range<Integer> LEGAL_WEIGHTS = Range.atLeast(0);


  private Edge(Node source, Node target, String label, Iterable<String> styles,
               int weight, double penWidth, double arrowSize) {
    this.source = checkNotNull(source);
    this.target = checkNotNull(target);
    this.label = label;
    this.styles = ImmutableSet.copyOf(styles);
    this.penWidth = penWidth;
    checkArgument(LEGAL_PEN_WIDTHS.contains(penWidth), "Illegal edge pen width %s", penWidth);
    this.arrowSize = arrowSize;
    checkArgument(LEGAL_ARROW_SIZES.contains(arrowSize), "Illegal edge arrow size %s", arrowSize);
    this.weight = weight;
    checkArgument(LEGAL_WEIGHTS.contains(weight), "Illegal edge weight %s", weight);
  }

  public static Builder fromTo(Node source, Node target) {
    return new Builder(source, target);
  }

  public Node source() {
    return source;
  }

  public Node target() {
    return target;
  }

  public String toDot() {
    final List<String> attributes = Lists.newArrayList();
    if (!styles.isEmpty()) {
      if (styles.size() == 1) {
        attributes.add("style=" + Iterables.getFirst(styles, null));
      } else {
        throw new UnsupportedOperationException("Multiple styles not yet supported");
      }
    }
    if (label != null) {
      attributes.add("label=\"" + label + "\"");
    }
    if (penWidth != DEFAULT_PEN_WIDTH) {
      attributes.add("penwidth=\"" + penWidth + "\"");
    }
    if (weight != DEFAULT_WEIGHT) {
      attributes.add("weight=\"" + weight + "\"");
    }
    if (arrowSize != DEFAULT_ARROW_SIZE) {
      attributes.add("arrowsize=\"" + arrowSize + "\"");
    }

    final String attributesString;

    if (!attributes.isEmpty()) {
      attributesString = "[" + StringUtils.commaSpaceJoiner().join(attributes) + "]";
    } else {
      attributesString = "";
    }

    return source.name() + " -> " + target.name() + attributesString + ";";
  }

  public static Function<Edge, String> toDotFunction() {
    return new Function<Edge, String>() {
      @Override
      public String apply(Edge input) {
        return input.toDot();
      }
    };
  }

  public static final class Builder {

    private final Node source;
    private final Node target;
    private final List<String> styles = Lists.newArrayList();
    private String label = null;
    private double penWidth = DEFAULT_PEN_WIDTH;
    private double arrowSize = DEFAULT_ARROW_SIZE;
    private int weight = DEFAULT_WEIGHT;

    private Builder(Node source, Node target) {
      this.source = checkNotNull(source);
      this.target = checkNotNull(target);
    }

    public Builder withLabel(String label) {
      this.label = checkNotNull(label);
      return this;
    }

    public Builder dotted() {
      this.styles.add("dotted");
      return this;
    }

    public Builder withPenWidth(double penWidth) {
      checkArgument(penWidth > 0.0);
      this.penWidth = penWidth;
      return this;
    }

    public Builder withArrowSize(double arrowSize) {
      checkArgument(arrowSize > 0.0);
      this.arrowSize = arrowSize;
      return this;
    }

    public Builder withWeight(int weight) {
      checkArgument(weight > 0);
      this.weight = weight;
      return this;
    }

    public Edge build() {
      return new Edge(source, target, label, styles, weight, penWidth, arrowSize);
    }
  }
}

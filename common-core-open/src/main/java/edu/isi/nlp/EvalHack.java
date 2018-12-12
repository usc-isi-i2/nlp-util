package edu.isi.nlp;

/**
 * Indicates something is a hack for an evaluation and should get cleaned up when we have the
 * chance.
 */
public @interface EvalHack {

  /** Which eval was this hack for? */
  String eval() default "";
}

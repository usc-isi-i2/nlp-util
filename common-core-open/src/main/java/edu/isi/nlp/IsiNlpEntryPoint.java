package edu.isi.nlp;

/**
 * The entry point of a standard BU-E program. Such a program should receive all its configuration
 * information via injection. By convention, implementing classes should contain a module called
 * {@code Module} or {@code FromParametersModule} extending {@link AbstractParameterizedModule}
 * which install all required Guice modules.
 *
 * <p>This interface adds nothing beyond {@link ThrowingRunnable} but provides useful semantics to
 * the reader. For example, when grappling with a new codebase, it could be useful to list all
 * classes implementing {@code IsiNlpEntryPoint}.
 *
 * <p>To run a {@link IsiNlpEntryPoint}, use {@link IsiNlpEntryPoints#runEntryPoint(Class, Class,
 * String[])} . Please see that method's Javadoc for a number of useful features this makes
 * available to all executables.
 *
 * @author Ryan Gabbard
 */
public interface IsiNlpEntryPoint extends ThrowingRunnable {}

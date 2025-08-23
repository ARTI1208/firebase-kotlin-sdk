package dev.gitlive.firebase

/**
 * Denotes an `external` declaration that can be used without module system.
 *
 * By default, an `external` declaration is available regardless your target module system.
 * However, by applying [JsModule] annotation you can make a declaration unavailable to *plain* module system.
 * Some JavaScript libraries are distributed both as a standalone downloadable piece of JavaScript and as a module available
 * as an npm package.
 * To tell the Kotlin compiler to accept both cases, you can augment [JsModule] with the `@JsNonModule` annotation.
 *
 * For example:
 *
 * ```kotlin
 * @JsModule("jquery")
 * @JsNonModule
 * @JsName("$")
 * external abstract class JQuery() {
 *     // some declarations here
 * }
 *
 * @JsModule("jquery")
 * @JsNonModule
 * @JsName("$")
 * external fun JQuery(element: Element): JQuery
 * ```
 *
 * @see JsModule
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
public expect annotation class JsNonModule()

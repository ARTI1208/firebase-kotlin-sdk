package dev.gitlive.firebase

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.BINARY)
public actual annotation class JsNonModule()
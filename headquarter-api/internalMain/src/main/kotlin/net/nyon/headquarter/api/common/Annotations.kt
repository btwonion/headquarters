package net.nyon.headquarter.api.common

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@RequiresOptIn(
    message = "This is an internal Headquarter API that " +
            "should not be used from the outside. No compatibility guarantees are provided. " +
            "It is recommended to report your use-case of internal API to the Headquarter team.",
    level = RequiresOptIn.Level.ERROR
)
annotation class InternalHeadquarterAPI

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@RequiresOptIn(
    message = "This is an experimental API and its use requires care, since" +
            " stability cannot be guaranteed for this API yet." +
            " This means that the functionality of this API may not work correctly yet, or that" +
            " breaking changes can occur at any time.",
    level = RequiresOptIn.Level.WARNING
)
annotation class ExperimentalHeadquarterApi
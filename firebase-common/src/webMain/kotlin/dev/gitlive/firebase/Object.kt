package dev.gitlive.firebase

public expect object Object {

    public fun keys(any: Any): Set<String>

    public fun entries(json: Any?): List<Pair<String, Any?>>

}
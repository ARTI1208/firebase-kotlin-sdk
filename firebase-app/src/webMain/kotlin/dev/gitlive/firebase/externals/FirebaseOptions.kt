package dev.gitlive.firebase.externals

import kotlin.js.js

@Suppress("unused")
public fun createFirebaseOptions(
    apiKey: String,
    authDomain: String?,
    databaseURL: String?,
    projectId: String?,
    storageBucket: String?,
    messagingSenderId: String?,
    appId: String,
    measurementId: String?,
): FirebaseOptions = js(
    """({ 
        apiKey: apiKey, 
        authDomain: authDomain, 
        databaseURL: databaseURL, 
        projectId: projectId, 
        storageBucket: storageBucket, 
        messagingSenderId: messagingSenderId, 
        appId: appId, 
        measurementId: measurementId  
    })"""
)
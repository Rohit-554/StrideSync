package io.jadu.strideSync.permissions

enum class Permission {
    CAMERA,
    LOCATION,
    NOTIFICATIONS,
    RECORD_AUDIO,
    STORAGE,
}

enum class PermissionStatus {
    GRANTED,
    DENIED,
    DENIED_ALWAYS,
    NOT_DETERMINED,
}

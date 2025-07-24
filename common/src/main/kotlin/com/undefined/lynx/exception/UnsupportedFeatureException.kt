package com.undefined.lynx.exception

class UnsupportedFeatureException(feature: String) : RuntimeException("The feature you are trying to use is not supported in this minecraft version. ($feature)") {
}
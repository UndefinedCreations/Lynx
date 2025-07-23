package com.undefined.lynx.exception

import java.lang.RuntimeException

class UnsupportedFeatureException(feature: String) : RuntimeException("The feature you are trying to use is not supported in this minecraft version. ($feature)") {
}
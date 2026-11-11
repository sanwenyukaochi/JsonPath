package com.jayway.jsonpath.internal.path

interface PathTokenAppender {
    fun appendPathToken(next: PathToken): PathTokenAppender
}

package com.hisd3.hismk2.filters

import javax.servlet.ServletException

 class GzipResponseHeadersNotModifiableException extends ServletException {

     GzipResponseHeadersNotModifiableException(String message) {
        super(message)
    }
}
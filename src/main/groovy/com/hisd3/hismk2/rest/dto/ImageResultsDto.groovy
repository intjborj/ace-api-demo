package com.hisd3.hismk2.rest.dto

import javassist.bytecode.ByteArray

class ImageResultsDto {
	UUID id = null
	UUID orderSlip = null
	String mimetype = null
	String originalFilename = null
	ByteArray data = null
	String batch = null
}

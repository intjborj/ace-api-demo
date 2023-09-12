package com.hisd3.hismk2.services

import groovy.transform.TypeChecked
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
@TypeChecked
class PrintingService {
	boolean print(String link, String data) {
		def httpclient = HttpClients.custom().build()
		HttpPost post = new HttpPost(link)
		ArrayList params = new ArrayList(2)
		String wristband3 = data
		params.add(new BasicNameValuePair("wristband_data", wristband3))
		post.entity = new StringEntity(wristband3)
		def response = httpclient.execute(post)
		HttpHeaders responseHeaders = new HttpHeaders()
		try {
			responseHeaders.set(response.entity.contentType.name, response.entity.contentType.value)
		} catch (Exception e) {
			e.printStackTrace()
		}
		return true
	}
}

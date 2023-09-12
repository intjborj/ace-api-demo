package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item

import java.time.Instant

class StockIssueDto {
	Instant issued_date = null
	String issue_to = null // department
	String issued_by = null // employee
	String claimed_by = null //employee
	String issued_from = null //department
	String issue_type = null
	String request_no = null
	String request = null
	String acc_type = null
}

class IssuedItems {
	String id
	Item item
	Integer requestedQty
	Integer issueQty
	BigDecimal unitCost
	Boolean isPosted
	String remarks
	Boolean isNew
}



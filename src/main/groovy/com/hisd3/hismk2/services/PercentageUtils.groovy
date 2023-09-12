package com.hisd3.hismk2.services

import java.math.MathContext
import java.math.RoundingMode

// accepts multiplier
class PercentageUtils {
	
	/**
	 *   ex. input .20   returns   .80
	 
	 */
	static private BigDecimal invertValuePercentage(BigDecimal multiplier) {
		1 - multiplier
	}
	
	static private BigDecimal addToOne(BigDecimal multiplier) {
		1 + multiplier
	}
	
	// returns the amount use to deduct
	static BigDecimal deductionPercentageValue(BigDecimal initialAmount, BigDecimal multiplier, Boolean fromInitial) {
		
		if (fromInitial) {
			
			((initialAmount / (addToOne(multiplier))) * multiplier).setScale(4, RoundingMode.HALF_EVEN)
			
		} else {
			(initialAmount * multiplier).setScale(4, RoundingMode.HALF_EVEN)
		}
		
	}
	
	// returns the new value after multiplier
	static BigDecimal increasePercentageValue(BigDecimal initialAmount, BigDecimal multiplier, Boolean fromInitial) {
		
		if (fromInitial) {
			
			initialAmount * (addToOne(multiplier))
			
		} else {
			
			initialAmount / invertValuePercentage(multiplier)
		}
		
	}
	
	static <T> Map<T, BigDecimal> prorateFromValues(
			BigDecimal givenTotalAmount,
			Map<T, BigDecimal> forAmounts,
			Boolean includeNegative,
			Boolean processExcess
	) {
		
		def result = [:] as Map<T, BigDecimal>
		def forProcessed = [:] as Map<T, BigDecimal>
		
		forAmounts.each {
			t, u ->
				
				def valueAmount = u.setScale(2, RoundingMode.HALF_EVEN)
				if (includeNegative) {
					forProcessed.put(t, valueAmount)
				} else {
					if (valueAmount > 0)
						forProcessed.put(t, valueAmount)
				}
		}
		
		def processTotal = givenTotalAmount.setScale(2, RoundingMode.HALF_EVEN)
		
		def totalGiven = 0.0
		
		forProcessed.collect { it.value }.each {
			totalGiven += it.setScale(2, RoundingMode.HALF_EVEN)
		}
		
		//i.e.  if amount deduct is greater than balance
		if (processTotal >= totalGiven) {
			
			if (!processExcess) {
				forProcessed.forEach { t, u ->
					processTotal -= u
					result.put(t, u)
					
				}
				
				return result // i.e return ang full amount from phic
			}
			
		}
		def oldRunningTotal = 0.0
		def newRunningTotal = 0.0
		def totalShare = 0.0
		
		forProcessed.forEach { t, u ->
			
			def amount = u.setScale(2, RoundingMode.HALF_EVEN)
			def percentage = amount.divide(totalGiven.setScale(2, RoundingMode.HALF_EVEN), MathContext.DECIMAL32)
			
			def share = percentage.multiply(processTotal, MathContext.DECIMAL64).setScale(4, RoundingMode.HALF_EVEN)
			
			newRunningTotal += (share)
			
			def finalShare = (newRunningTotal.setScale(2, RoundingMode.HALF_EVEN) - oldRunningTotal.setScale(2, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.HALF_EVEN)
			result.put(t, finalShare)
			totalShare += finalShare
			
			oldRunningTotal = newRunningTotal
		}
		
		// =====for SafeKeeping=====
		
		if (totalShare != processTotal) {
			
			def diff = processTotal - totalShare
			
			if (diff > BigDecimal.ZERO) {
				def firstKey = result.sort {
					a1, a2 ->
						(a2.value <=> a1.value)
				}.collect { it.key }
						.find() as T
				
				if (firstKey)
					result.put(firstKey, (result.get(firstKey) + diff).setScale(2, RoundingMode.HALF_EVEN))
				
			} else {
				
				def firstKey = result.sort {
					a1, a2 ->
						(a1.value <=> a2.value)
				}.collect { it.key }
						.find() as T
				
				if (firstKey)
					result.put(firstKey, (result.get(firstKey) + diff).setScale(2, RoundingMode.HALF_EVEN))
			}
			
		}
		// =====for SafeKeeping=====
		
		result
	}
	
}

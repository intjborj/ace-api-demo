package com.hisd3.hismk2.domain.types

interface AutoIntegrateable {
    String getDomain()
    String getFlagValue()
    void setFlagValue(String value)
    Map<String,String> getDetails()
}
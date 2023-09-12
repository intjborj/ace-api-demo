package com.hisd3.hismk2.domain.types

import com.hisd3.hismk2.rest.dto.CoaConfig

interface CodeAndDescable {
    String getCode()
    String getDescription()
    //wilson code for custom department viewing
    List<UUID> getDepartment()
    CoaConfig getConfig()
}
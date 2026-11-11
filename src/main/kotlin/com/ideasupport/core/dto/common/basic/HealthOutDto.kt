package com.ideasupport.core.dto.common.basic

data class HealthOutDto(val status: String, val database: String, val aiMode: String, val seedReady: Boolean, val stats: GenerationStatsOutDto = GenerationStatsOutDto())

/** 서버를 켠 뒤 AI로 만든 수(ai*)와 저장된 결과를 재사용한 수(cached*). 후속 가이드는 /recipe 호출을 포함한다. */
data class GenerationStatsOutDto(val aiIdeas: Long = 0, val cachedIdeas: Long = 0, val aiGuides: Long = 0, val cachedGuides: Long = 0)

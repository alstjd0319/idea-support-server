package com.ideasupport.main.small

import com.ideasupport.core.dto.common.basic.GenerationStatsOutDto
import com.ideasupport.infra.ai.AiClient
import com.ideasupport.lib.common.component.GenerationStatsComponent
import com.ideasupport.main.common.health.HealthComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.jdbc.core.JdbcTemplate

class HealthTest {
	private val jdbc = mock<JdbcTemplate>()
	private val ai = mock<AiClient>()
	private val stats = GenerationStatsComponent()
	private val component = HealthComponent(jdbc, ai, stats)

	@Test fun `stats separate AI generation from reused results`() {
		whenever(jdbc.queryForObject(any<String>(), eq(Int::class.java))).thenReturn(1, 3)
		whenever(ai.source).thenReturn("mock")
		stats.recordIdea(false)
		stats.recordIdea(true)
		stats.recordIdea(true)
		stats.recordGuide(false)
		assertEquals(GenerationStatsOutDto(aiIdeas = 1, cachedIdeas = 2, aiGuides = 1, cachedGuides = 0), component.status().stats)
	}

	@Test fun `stats stay visible when the database is down`() {
		whenever(jdbc.queryForObject(any<String>(), eq(Int::class.java))).thenThrow(IllegalStateException("down"))
		whenever(ai.source).thenReturn("mock")
		stats.recordGuide(true)
		assertEquals(1, component.status().stats.cachedGuides)
	}

	@Test fun `healthy database and complete seed return ok`() {
		whenever(jdbc.queryForObject(any<String>(), eq(Int::class.java))).thenReturn(1, 3)
		whenever(ai.source).thenReturn("mock")
		assertEquals("ok", component.status().status)
	}

	@Test fun `incomplete seed degrades readiness without calling DB down`() {
		whenever(jdbc.queryForObject(any<String>(), eq(Int::class.java))).thenReturn(1, 2)
		whenever(ai.source).thenReturn("mock")
		val result = component.status()
		assertEquals("degraded", result.status)
		assertEquals("up", result.database)
		assertFalse(result.seedReady)
	}

	@Test fun `database failure returns degraded status instead of a stack trace`() {
		whenever(jdbc.queryForObject(any<String>(), eq(Int::class.java))).thenThrow(IllegalStateException("private database detail"))
		whenever(ai.source).thenReturn("gemini")
		val result = component.status()
		assertEquals("down", result.database)
		assertFalse(result.seedReady)
		assertEquals("gemini", result.aiMode)
	}
}

package com.ideasupport.lib.common.component
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong
import com.ideasupport.core.dto.common.basic.GenerationStatsOutDto

/** 서버를 켠 뒤 AI로 만든 수와 저장된 결과를 재사용한 수. 같은 조합의 AI 호출 0회를 /common/health로 확인한다. */
@Component
class GenerationStatsComponent {
	private val aiIdeas = AtomicLong()
	private val cachedIdeas = AtomicLong()
	private val aiGuides = AtomicLong()
	private val cachedGuides = AtomicLong()

	fun recordIdea(cached: Boolean) {
		(if (cached) cachedIdeas else aiIdeas).incrementAndGet()
	}

	fun recordGuide(cached: Boolean) {
		(if (cached) cachedGuides else aiGuides).incrementAndGet()
	}

	fun snapshot() = GenerationStatsOutDto(aiIdeas.get(), cachedIdeas.get(), aiGuides.get(), cachedGuides.get())
}

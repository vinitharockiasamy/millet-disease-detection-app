package com.pearlmillet.app.quality

data class QualityResult(
    val passed: Boolean,
    val issues: List<QualityIssue>
)

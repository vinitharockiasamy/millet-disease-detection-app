package com.pearlmillet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.ui.theme.AppColors

@Composable
fun PearlMilletScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = { AppFooter() },
    containerColor: Color = AppColors.SurfaceBg,
    enableScrolling: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            topBar()

            androidx.compose.foundation.layout.BoxWithConstraints(
                modifier = Modifier.weight(1f)
            ) {
                val constraintsMinHeight = maxHeight
                if (enableScrolling) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier.defaultMinSize(minHeight = constraintsMinHeight),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                content()
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                            ) {
                                bottomBar()
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            content()
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        ) {
                            bottomBar()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(4.dp))
        androidx.compose.material3.Text(
            text = "PMC • Crop Disease Detection • v2",
            fontSize = 10.sp,
            color = AppColors.DeepGreen,
            fontWeight = FontWeight.Medium,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}
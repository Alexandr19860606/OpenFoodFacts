package com.korelin.openfoodfacts.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
object AppAnimations {

    val defaultEnterTransition = fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + slideInHorizontally(
        initialOffsetX = { it / 4 },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val defaultExitTransition = fadeOut(
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    ) + slideOutHorizontally(
        targetOffsetX = { -it / 4 },
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    )

    val popEnterTransition = fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + slideInHorizontally(
        initialOffsetX = { -it / 4 },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val popExitTransition = fadeOut(
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    ) + slideOutHorizontally(
        targetOffsetX = { it / 4 },
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    )
}
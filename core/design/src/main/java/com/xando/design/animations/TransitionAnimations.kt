package com.xando.design.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

private const val ANIM_DURATION = 300
private const val OFFSET_PERCENT = 80

/**
 * Content transform без какой-либо анимации
 */
val noTransition: ContentTransform = EnterTransition.None togetherWith ExitTransition.None

/**
 * Анимация появления нового контента справа с лёгким fade-in.
 * Используется как "forward" переход.
 */
fun AnimatedContentTransitionScope<*>.rightInTransition(): ContentTransform =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 100 * OFFSET_PERCENT },
        animationSpec = tween(ANIM_DURATION)
    ) + fadeIn(tween(ANIM_DURATION)) togetherWith ExitTransition.None

/**
 * Анимация ухода текущего контента вправо с лёгким fade-out.
 * Используется как "backward" переход.
 */
fun AnimatedContentTransitionScope<*>.rightOutTransition(): ContentTransform =
    EnterTransition.None togetherWith slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 100 * OFFSET_PERCENT },
        animationSpec = tween(ANIM_DURATION)
    ) + fadeOut(tween(ANIM_DURATION))


/**
 * Анимация одновременного движения экранов: новый въезжает справа, старый уезжает влево.
 * Используется как "forward" переход.
 */
fun AnimatedContentTransitionScope<*>.rightInLeftOutTransition(): ContentTransform =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(ANIM_DURATION)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(ANIM_DURATION)
    )


/**
 * Анимация одновременного движения экранов: новый въезжает слева, старый уезжает вправо.
 * Используется как "backward" переход.
 */
fun AnimatedContentTransitionScope<*>.rightOutLeftInTransition(): ContentTransform =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(ANIM_DURATION)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(ANIM_DURATION)
    )


package com.xando.data.auth.di

import com.xando.core.common.session.SessionStartListener
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * Hilt-модуль, объявляющий набор участников начала сессии. Набор может быть пустым:
 * участники подключаются вместе с модулями, которые их регистрируют.
 */
@Module
@InstallIn(SingletonComponent::class)
interface SessionModule {

    /** @SelfDocumented */
    @Multibinds
    fun sessionStartListeners(): Set<SessionStartListener>
}

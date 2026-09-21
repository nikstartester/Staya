package com.xando.core.models.pet

/**
 * Интерес питомца - чем он любит заниматься.
 */
enum class PetInterest(val group: PetInterestGroup) {

    /** @SelfDocumented */
    FOREST_WALKS(PetInterestGroup.WALK),

    /** @SelfDocumented */
    CITY_WALKS(PetInterestGroup.WALK),

    /** @SelfDocumented */
    DOG_PARK(PetInterestGroup.WALK),

    /** @SelfDocumented */
    SWIMMING(PetInterestGroup.WALK),

    /** @SelfDocumented */
    SNOW_PLAY(PetInterestGroup.WALK),

    /** @SelfDocumented */
    COUNTRYSIDE_TRIPS(PetInterestGroup.WALK),

    /** @SelfDocumented */
    LONG_HIKES(PetInterestGroup.WALK),

    /** @SelfDocumented */
    JOGGING_WITH_OWNER(PetInterestGroup.WALK),

    /** @SelfDocumented */
    PLAYING_FETCH(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    TUG_OF_WAR(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    PULLER(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    SQUEAKY_TOYS(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    CHEW_TOYS(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    PUZZLE_TOYS(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    HIDE_AND_SEEK(PetInterestGroup.GAMES),

    /** @SelfDocumented */
    AGILITY(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    FRISBEE(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    FLYBALL(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    CANICROSS(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    BIKEJORING(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    DOG_DANCING(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    SCENT_TRACKING(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    OBEDIENCE(PetInterestGroup.SPORT),

    /** @SelfDocumented */
    MEETING_PUPPIES(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    PACK_PLAY(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    CHASING_OTHER_DOGS(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    CALM_MEETUPS(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    SMALL_DOGS(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    BIG_DOGS(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    CATS_AND_OTHER_PETS(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    KIDS_AND_NOISY_COMPANY(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    MEETING_PEOPLE(PetInterestGroup.SOCIAL),

    /** @SelfDocumented */
    CUDDLING(PetInterestGroup.HOME),

    /** @SelfDocumented */
    WINDOW_WATCHING(PetInterestGroup.HOME),

    /** @SelfDocumented */
    TRICK_TRAINING(PetInterestGroup.HOME),

    /** @SelfDocumented */
    MASSAGE_AND_GROOMING(PetInterestGroup.HOME),

    /** @SelfDocumented */
    ZOOMIES(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    BELLY_RUBS(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    SOCK_STEALING(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    PUDDLE_SPLASHING(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    ROLLING_IN_GRASS(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    CHASING_PIGEONS(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    VACUUM_HUNTING(PetInterestGroup.QUIRKS),

    /** @SelfDocumented */
    BATHROOM_ESCORT(PetInterestGroup.QUIRKS),
    ;

    companion object {

        /**
         * Интересы, которые выбирают чаще всего, в порядке убывания популярности.
         */
        // TODO: для простоты пока тут, но нужно по-хорошему перенести.
        val POPULAR: List<PetInterest> = listOf(
            PLAYING_FETCH,
            FOREST_WALKS,
            DOG_PARK,
            PACK_PLAY,
            CUDDLING,
            TRICK_TRAINING,
        )

        private val BY_CODE: Map<String, PetInterest> = entries.associateBy(PetInterest::name)

        /**
         * Интерес по коду [code] или `null`, если код неизвестен.
         * Неизвестный код возможен, если питомца завели в более новой версии приложения, где список интересов больше.
         */
        fun fromCodeOrNull(code: String?): PetInterest? = code?.let(BY_CODE::get)
    }
}

/**
 * Группа интересов питомца. Порядок объявления констант значим: это порядок самих групп.
 */
enum class PetInterestGroup {

    /** Прогулка. */
    WALK,

    /** Игры. */
    GAMES,

    /** Спорт и тренировки. */
    SPORT,

    /** Общение. */
    SOCIAL,

    /** Дома. */
    HOME,

    /** Милые странности. */
    QUIRKS,
}

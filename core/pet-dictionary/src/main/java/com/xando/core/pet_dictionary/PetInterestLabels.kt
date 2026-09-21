package com.xando.core.pet_dictionary

import androidx.annotation.StringRes
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetInterestGroup

/**
 * Строковый ресурс с названием интереса.
 */
@get:StringRes
val PetInterest.labelRes: Int
    get() = when (this) {
        PetInterest.FOREST_WALKS -> R.string.pet_dictionary_interest_forest_walks
        PetInterest.CITY_WALKS -> R.string.pet_dictionary_interest_city_walks
        PetInterest.DOG_PARK -> R.string.pet_dictionary_interest_dog_park
        PetInterest.SWIMMING -> R.string.pet_dictionary_interest_swimming
        PetInterest.SNOW_PLAY -> R.string.pet_dictionary_interest_snow_play
        PetInterest.COUNTRYSIDE_TRIPS -> R.string.pet_dictionary_interest_countryside_trips
        PetInterest.LONG_HIKES -> R.string.pet_dictionary_interest_long_hikes
        PetInterest.JOGGING_WITH_OWNER -> R.string.pet_dictionary_interest_jogging_with_owner
        PetInterest.PLAYING_FETCH -> R.string.pet_dictionary_interest_playing_fetch
        PetInterest.TUG_OF_WAR -> R.string.pet_dictionary_interest_tug_of_war
        PetInterest.PULLER -> R.string.pet_dictionary_interest_puller
        PetInterest.SQUEAKY_TOYS -> R.string.pet_dictionary_interest_squeaky_toys
        PetInterest.CHEW_TOYS -> R.string.pet_dictionary_interest_chew_toys
        PetInterest.PUZZLE_TOYS -> R.string.pet_dictionary_interest_puzzle_toys
        PetInterest.HIDE_AND_SEEK -> R.string.pet_dictionary_interest_hide_and_seek
        PetInterest.AGILITY -> R.string.pet_dictionary_interest_agility
        PetInterest.FRISBEE -> R.string.pet_dictionary_interest_frisbee
        PetInterest.FLYBALL -> R.string.pet_dictionary_interest_flyball
        PetInterest.CANICROSS -> R.string.pet_dictionary_interest_canicross
        PetInterest.BIKEJORING -> R.string.pet_dictionary_interest_bikejoring
        PetInterest.DOG_DANCING -> R.string.pet_dictionary_interest_dog_dancing
        PetInterest.SCENT_TRACKING -> R.string.pet_dictionary_interest_scent_tracking
        PetInterest.OBEDIENCE -> R.string.pet_dictionary_interest_obedience
        PetInterest.MEETING_PUPPIES -> R.string.pet_dictionary_interest_meeting_puppies
        PetInterest.PACK_PLAY -> R.string.pet_dictionary_interest_pack_play
        PetInterest.CHASING_OTHER_DOGS -> R.string.pet_dictionary_interest_chasing_other_dogs
        PetInterest.CALM_MEETUPS -> R.string.pet_dictionary_interest_calm_meetups
        PetInterest.SMALL_DOGS -> R.string.pet_dictionary_interest_small_dogs
        PetInterest.BIG_DOGS -> R.string.pet_dictionary_interest_big_dogs
        PetInterest.CATS_AND_OTHER_PETS -> R.string.pet_dictionary_interest_cats_and_other_pets
        PetInterest.KIDS_AND_NOISY_COMPANY -> R.string.pet_dictionary_interest_kids_and_noisy_company
        PetInterest.MEETING_PEOPLE -> R.string.pet_dictionary_interest_meeting_people
        PetInterest.CUDDLING -> R.string.pet_dictionary_interest_cuddling
        PetInterest.WINDOW_WATCHING -> R.string.pet_dictionary_interest_window_watching
        PetInterest.TRICK_TRAINING -> R.string.pet_dictionary_interest_trick_training
        PetInterest.MASSAGE_AND_GROOMING -> R.string.pet_dictionary_interest_massage_and_grooming
        PetInterest.ZOOMIES -> R.string.pet_dictionary_interest_zoomies
        PetInterest.BELLY_RUBS -> R.string.pet_dictionary_interest_belly_rubs
        PetInterest.SOCK_STEALING -> R.string.pet_dictionary_interest_sock_stealing
        PetInterest.PUDDLE_SPLASHING -> R.string.pet_dictionary_interest_puddle_splashing
        PetInterest.ROLLING_IN_GRASS -> R.string.pet_dictionary_interest_rolling_in_grass
        PetInterest.CHASING_PIGEONS -> R.string.pet_dictionary_interest_chasing_pigeons
        PetInterest.VACUUM_HUNTING -> R.string.pet_dictionary_interest_vacuum_hunting
        PetInterest.BATHROOM_ESCORT -> R.string.pet_dictionary_interest_bathroom_escort
    }

/**
 * Строковый ресурс с названием группы интересов.
 */
@get:StringRes
val PetInterestGroup.labelRes: Int
    get() = when (this) {
        PetInterestGroup.WALK -> R.string.pet_dictionary_interest_group_walk
        PetInterestGroup.GAMES -> R.string.pet_dictionary_interest_group_games
        PetInterestGroup.SPORT -> R.string.pet_dictionary_interest_group_sport
        PetInterestGroup.SOCIAL -> R.string.pet_dictionary_interest_group_social
        PetInterestGroup.HOME -> R.string.pet_dictionary_interest_group_home
        PetInterestGroup.QUIRKS -> R.string.pet_dictionary_interest_group_quirks
    }

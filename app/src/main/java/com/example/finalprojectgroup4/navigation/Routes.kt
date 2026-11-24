package com.example.finalprojectgroup4.navigation

/**
 * Every route name in Spot, kept in one place so screens never build route strings by hand.
 *
 * Kind: object. Owner: Track A (Sébastien).
 */
object Routes {

    /** Name of the argument that carries a check-in id in DETAIL and EDIT. */
    const val ARG_ID = "id"

    // Start and sign-in flow
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"

    // Bottom tab destinations
    const val HOME = "home"
    const val MAP = "map"
    const val CHECKINS = "checkins"

    // Stack destinations opened on top of the tabs
    const val DETAIL = "checkin/{$ARG_ID}"
    const val NEW = "new"
    const val EDIT = "edit/{$ARG_ID}"

    /** Builds the real Detail route for one check-in, e.g. "checkin/42". */
    fun detail(id: String) = "checkin/$id"

    /** Builds the real Edit route for one check-in, e.g. "edit/42". */
    fun edit(id: String) = "edit/$id"
}

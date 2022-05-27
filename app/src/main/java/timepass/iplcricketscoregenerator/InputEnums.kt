@file:Suppress("PropertyName", "ClassName")
package timepass.iplcricketscoregenerator

enum class FootValues{
    FRONT,
    ADVANCE,
    BACK;
}

fun getFootFromLength(length: Int): FootValues{
    return FootValues.values()[length]
}

fun getShotsBetter(): MutableList<ShotValues>{
    val x = ShotValues.values().toMutableList()
    ShotValues.values().forEach {
        x.add(if(it != ShotValues.SMALL_LOFT) it else ShotValues.STROKE)
        x.add(if(it != ShotValues.SMALL_LOFT && it != ShotValues.DEFEND) it else ShotValues.BIG_LOFT)
        x.add(if(it != ShotValues.SMALL_LOFT && it != ShotValues.DEFEND) it else ShotValues.STROKE)
    }
    return x
}

enum class ShotValues{
    DEFEND,
    PUSH,
    STROKE,
    SMALL_LOFT,
    BIG_LOFT
}

object LengthValues{
    const val SLOT = 0
    const val YORKER = 0
    const val LENGTH = 1
    const val SHORT = 2

    fun valueOf(s: String): Int{
        return when(s){
            "SLOT", "YORKER" -> 0
            "LENGTH" -> 1
            "SHORT" -> 2
            else -> 0
        }
    }

    fun values(): List<String>{
        return listOf("SLOT", "YORKER", "LENGTH", "SHORT")
    }
}

data class Input(
    val foot: Int,
    val shot: ShotValues,
    val length: Int,
    val degree: Int
)

//object Runs{
//    val PUSH = listOf(
//        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//        2, 2, 2, 2, 2, 2, 2,
//        3, 3,
//        4, 4
//    )
//
//    val STROKE = listOf(
//        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//        2, 2, 2, 2, 2, 2, 2,
//        3, 3, 3, 3,
//        4, 4, 4, 4, 4, 4
//    )
//
//    val SMALL_LOFT = listOf(
//        1, 1, 1, 1, 1, 1, 1, 1,
//        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//        3, 3, 3, 3, 3,
//        4, 4, 4, 4, 4, 4, 4, 4
//    )
//    val BIG_LOFT = listOf(
//        1, 1,
//        2, 2, 2, 2, 2,
//        3, 3, 3,
//        4, 4, 4, 4, 4, 4, 4, 4, 4,
//        6, 6, 6, 6, 6, 6
//    )
//}

fun getRuns(inputParameter: Input): Int{
    val deg = inputParameter.degree % 360
    val deg40 = (deg / 40) * 40
    val diffDeg = deg % 40
    when(inputParameter.foot - inputParameter.length){
        0 -> {
            when(inputParameter.shot){
                ShotValues.DEFEND -> return 0

                ShotValues.BIG_LOFT -> return listOf(4, 6, 6, 6).random()

                ShotValues.PUSH -> {
                    return listOf(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3,
                        4, 4
                    ).random()
                }

                ShotValues.SMALL_LOFT -> {
                    if(diffDeg <= 15){
                        if((deg40 / 40) % 2 == 1){
                            return -1
                        }
                    } else if(diffDeg >= 25){
                        if((deg40 / 40) % 2 == 0){
                            return -1
                        }
                    }
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3, 3, 3,
                        4, 4, 4, 4, 4
                    ).random()
                }

                ShotValues.STROKE -> {
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3, 3,
                        4, 4, 4
                    ).random()
                }
            }
        }

        1, -1 -> {
            when(inputParameter.shot){
                ShotValues.DEFEND -> return 0

                ShotValues.BIG_LOFT -> {
                    if(diffDeg <= 15){
                        if((deg40 / 40) % 2 == 0){
                            return -1
                        }
                    } else if(diffDeg >= 25){
                        if((deg40 / 40) % 2 == 1){
                            return -1
                        }
                    }
                    return listOf(
                        1, 1,
                        2, 2, 2,
                        3, 3, 3,
                        4, 4, 4, 4, 4, 4, 4,
                        6, 6, 6, 6
                    ).random()
                }

                ShotValues.PUSH -> {
                    return listOf(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3,
                        4, 4
                    ).random()
                }

                ShotValues.SMALL_LOFT -> {
                    if(diffDeg <= 15){
                        if((deg40 / 40) % 2 == 1){
                            return -1
                        }
                    } else if(diffDeg >= 25){
                        if((deg40 / 40) % 2 == 0){
                            return -1
                        }
                    }
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3, 3, 3,
                        4, 4, 4
                    ).random()
                }

                ShotValues.STROKE -> {
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3, 3,
                        4, 4, 4
                    ).random()
                }
            }
        }

        2, -2 -> {
            when(inputParameter.shot){
                ShotValues.DEFEND -> return 0

                ShotValues.BIG_LOFT -> {
                    if(diffDeg <= 15){
                        if((deg40 / 40) % 2 == 1){
                            return -1
                        }
                    } else if(diffDeg >= 25){
                        if((deg40 / 40) % 2 == 0){
                            return -1
                        }
                    }
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3,
                        4
                    ).random()
                }

                ShotValues.PUSH -> {
                    return listOf(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3,
                        4, 4
                    ).random()
                }

                ShotValues.SMALL_LOFT -> {
                    if(diffDeg <= 15){
                        if((deg40 / 40) % 2 == 1){
                            return -1
                        }
                    } else if(diffDeg >= 25){
                        if((deg40 / 40) % 2 == 0){
                            return -1
                        }
                    }
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2,
                        3, 3, 3,
                        4, 4, 4
                    ).random()
                }

                ShotValues.STROKE -> {
                    return listOf(
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 2, 2, 2, 2, 2, 2,
                        3, 3, 3, 3,
                        4, 4, 4
                    ).random()
                }
            }
        }
    }
    return 0
}
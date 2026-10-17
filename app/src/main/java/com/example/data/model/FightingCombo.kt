package com.example.data.model

data class FightingCombo(
    val id: String,
    val name: String,
    val discipline: String, // Boxing, Muay Thai, Kickboxing, MMA
    val level: String, // Beginner, Intermediate, Advanced, Champion
    val sequence: List<CombatTechnique>,
    val tips: String
) {
    val calloutText: String
        get() = sequence.joinToString(" - ") { it.displayName }

    val codeText: String
        get() = sequence.joinToString("-") { it.code }
}

object PresetCombos {
    val list = listOf(
        FightingCombo(
            id = "combo_1",
            name = "The Classic 1-2",
            discipline = "Boxing",
            level = "Beginner",
            sequence = listOf(CombatTechnique.JAB, CombatTechnique.CROSS),
            tips = "Keep your chin tucked behind the shoulder and snap the jab back instantly."
        ),
        FightingCombo(
            id = "combo_2",
            name = "The Triad: 1-2-3",
            discipline = "Boxing",
            level = "Beginner",
            sequence = listOf(CombatTechnique.JAB, CombatTechnique.CROSS, CombatTechnique.LEAD_HOOK),
            tips = "Pivot on the lead ball of your foot during the lead hook for maximum rotational torque."
        ),
        FightingCombo(
            id = "combo_3",
            name = "Slip & Rip Counter",
            discipline = "Boxing",
            level = "Intermediate",
            sequence = listOf(CombatTechnique.SLIP_LEFT, CombatTechnique.LEAD_HOOK, CombatTechnique.CROSS),
            tips = "Bait the opponent's right hand, slip outside, and explode with the counter hook."
        ),
        FightingCombo(
            id = "combo_4",
            name = "Mayweather Roll & Cross",
            discipline = "Boxing",
            level = "Advanced",
            sequence = listOf(CombatTechnique.GUARD_BLOCK, CombatTechnique.SLIP_RIGHT, CombatTechnique.CROSS, CombatTechnique.LEAD_UPPERCUT),
            tips = "Deflect off tight guard, slip to the outside pocket and throw the rear straight."
        ),
        FightingCombo(
            id = "combo_5",
            name = "The Dutch Kick Combo",
            discipline = "Kickboxing",
            level = "Intermediate",
            sequence = listOf(CombatTechnique.JAB, CombatTechnique.CROSS, CombatTechnique.LEAD_HOOK, CombatTechnique.REAR_LOW_KICK),
            tips = "High punch combination blinds the guard and leaves the lead leg wide open for the low chop."
        ),
        FightingCombo(
            id = "combo_6",
            name = "Bangkok Knee Devastator",
            discipline = "Muay Thai",
            level = "Advanced",
            sequence = listOf(CombatTechnique.JAB, CombatTechnique.REAR_UPPERCUT, CombatTechnique.MUAY_THAI_KNEE),
            tips = "Lift the opponent's chin with the uppercut, control the collar tie and drive the spear knee."
        ),
        FightingCombo(
            id = "combo_7",
            name = "Tyson Peek-a-boo Shift",
            discipline = "Boxing",
            level = "Champion",
            sequence = listOf(CombatTechnique.ROLL_WEAVE, CombatTechnique.LEAD_UPPERCUT, CombatTechnique.LEAD_HOOK, CombatTechnique.CROSS),
            tips = "Dip low under the jab line, explode upward with hook torque."
        ),
        FightingCombo(
            id = "combo_8",
            name = "Switch Kick Rhythm",
            discipline = "Muay Thai",
            level = "Intermediate",
            sequence = listOf(CombatTechnique.JAB, CombatTechnique.CROSS, CombatTechnique.LEAD_BODY_KICK),
            tips = "Quick hop switch stance as the cross extends, snapping the lead roundhouse into the ribcage."
        )
    )
}

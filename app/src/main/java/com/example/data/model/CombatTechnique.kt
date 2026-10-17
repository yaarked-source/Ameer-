package com.example.data.model

enum class CombatTechnique(
    val code: String,
    val displayName: String,
    val category: String,
    val description: String
) {
    JAB("1", "Jab", "Punches", "Quick lead hand straight punch to measure distance and disrupt opponent."),
    CROSS("2", "Cross", "Punches", "Powerful rear hand power straight straight through the guard."),
    LEAD_HOOK("3", "Lead Hook", "Punches", "Horizontal arc strike rotating hips to clip jaw or temple."),
    REAR_HOOK("4", "Rear Hook", "Punches", "Heavy rear rotational punch targeting side of head or ribs."),
    LEAD_UPPERCUT("5", "Lead Uppercut", "Punches", "Vertical punch exploding upward under opponent's chin."),
    REAR_UPPERCUT("6", "Rear Uppercut", "Punches", "Devastating rear rising punch through the center line."),
    SLIP_LEFT("SL", "Slip Left", "Defense", "Tilt torso to the left outside lead punch trajectory."),
    SLIP_RIGHT("SR", "Slip Right", "Defense", "Dodge to the right under rear straight punch."),
    ROLL_WEAVE("RW", "Roll / Duck", "Defense", "Deep U-motion dip beneath incoming horizontal hooks."),
    GUARD_BLOCK("GB", "High Guard", "Defense", "Forearms locked against forehead absorbing heavy impact."),
    MUAY_THAI_KNEE("KNEE", "Flying Knee", "Muay Thai", "Explosive driving knee strike into ribs or solar plexus."),
    LEAD_BODY_KICK("LBK", "Switch Kick", "Kicks", "Swift lead leg roundhouse whip into liver/ribs."),
    REAR_LOW_KICK("RLK", "Low Calf Kick", "Kicks", "Heavy shin chop battering lead thigh or calf.")
}

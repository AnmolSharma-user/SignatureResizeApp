package com.signatureresize.app.models

data class Preset(
    val id: String,
    val name: String,
    val widthPx: Int?,
    val heightPx: Int?,
    val minKb: Int = 10,
    val maxKb: Int = 50,
    val description: String
)

object ExamPresets {
    val presets = listOf(
        Preset(
            id = "upsc_sig",
            name = "UPSC Signature",
            widthPx = 350, // Approx for 3.5cm at 200dpi
            heightPx = 150, // Approx for 1.5cm
            minKb = 10,
            maxKb = 20,
            description = "Dimensions: 3.5cm x 1.5cm"
        ),
        Preset(
            id = "upsc_photo",
            name = "UPSC Photo",
            widthPx = 350,
            heightPx = 350,
            minKb = 20,
            maxKb = 300,
            description = "Dimensions: 3.5cm x 3.5cm"
        ),
        Preset(
            id = "gate_sig",
            name = "GATE Signature",
            widthPx = 280, // Aspect ratio huge variance, using standard
            heightPx = 80,
            minKb = 5,
            maxKb = 200,
            description = "High variance accepted"
        ),
        Preset(
            id = "ssc_sig",
            name = "SSC Signature",
            widthPx = 160,
            heightPx = 80,
            minKb = 10,
            maxKb = 20,
            description = "4.0cm x 2.0cm"
        ),
        Preset(
            id = "passport",
            name = "Passport Photo",
            widthPx = 413, // 35mm
            heightPx = 531, // 45mm
            minKb = 50,
            maxKb = 200,
            description = "Standard 35mm x 45mm"
        ),
        Preset(
            id = "pan_card",
            name = "PAN Card Photo",
            widthPx = 213, // 2.5cm
            heightPx = 213, // 3.5cm approx
            minKb = 20,
            maxKb = 50,
            description = "2.5cm x 3.5cm"
        )
    )
}

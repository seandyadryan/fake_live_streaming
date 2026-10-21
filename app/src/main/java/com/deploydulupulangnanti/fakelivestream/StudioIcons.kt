package com.deploydulupulangnanti.fakelivestream

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Small, native vector set: avoids shipping thousands of unused icon classes. */
object StudioIcons {
    private fun icon(name: String, path: String, filled: Boolean = false): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).addPath(
            pathData = PathParser().parsePathString(path).toNodes(),
            fill = if (filled) SolidColor(Color.Black) else null,
            stroke = if (filled) null else SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ).build()

    val Videocam = icon("Camera", "M4,6 H15 Q17,6 17,8 V16 Q17,18 15,18 H4 Q2,18 2,16 V8 Q2,6 4,6 Z M17,10 L22,7 V17 L17,14")
    val BarChart = icon("Sessions", "M5,19 V12 M12,19 V5 M19,19 V9")
    val Tune = icon("About", "M3,6 H8 M12,6 H21 M3,12 H14 M18,12 H21 M3,18 H5 M9,18 H21 M10,4 V8 M16,10 V14 M7,16 V20")
    val Edit = icon("Edit", "M4,16 L15,5 L19,9 L8,20 L3,21 Z M13,7 L17,11 M16,4 L18,2 L22,6 L20,8")
    val Forum = icon("Chat", "M3,4 H17 V14 H8 L3,18 Z M9,18 H16 L21,21 V10")
    val Redeem = icon("Gift", "M3,9 H21 V13 H3 Z M5,13 V21 H19 V13 M12,9 V21 M12,9 C3,10 4,1 8,3 C11,4 12,9 12,9 C12,9 13,4 16,3 C20,1 21,10 12,9")
    val PlayArrow = icon("Play", "M8,4 L21,12 L8,20 Z", true)
    val ArrowForward = icon("Forward", "M4,12 H20 M14,6 L20,12 L14,18")
    val Close = icon("Close", "M6,6 L18,18 M18,6 L6,18")
    val Visibility = icon("Viewers", "M2,12 C7,3 17,3 22,12 C17,21 7,21 2,12 Z M15,12 A3,3 0,1 1,9,12 A3,3 0,1 1,15,12")
    private const val heart = "M12,21 C8,17 2,13 2,8 C2,2 9,1 12,6 C15,1 22,2 22,8 C22,13 16,17 12,21 Z"
    val FavoriteBorder = icon("Hearts", heart)
    val Favorite = icon("Heart", heart, true)
    val Pause = icon("Pause", "M7,5 H9 V19 H7 Z M15,5 H17 V19 H15 Z", true)
    val Cameraswitch = icon("SwitchCamera", "M3,7 H7 L9,4 H15 L17,7 H21 V20 H3 Z M8,12 C10,9 15,10 16,13 M16,10 V13 H13 M16,16 C13,19 9,18 8,15 M8,18 V15 H11")
    val GraphicEq = icon("Voice", "M4,10 V14 M8,6 V18 M12,3 V21 M16,7 V17 M20,10 V14")
    val Send = icon("Send", "M3,3 L22,12 L3,21 L7,12 Z M7,12 H21")
    val AutoAwesome = icon("Sparkles", "M12,3 L15,9 L21,12 L15,15 L12,21 L9,15 L3,12 L9,9 Z M4,2 V6 M2,4 H6 M20,18 V22 M18,20 H22")
    val Shield = icon("Privacy", "M12,2 L21,6 V12 C21,18 16,21 12,23 C8,21 3,18 3,12 V6 Z M8,12 L11,15 L17,9")
    val WifiOff = icon("Offline", "M3,3 L21,21 M8,5 C13,3 19,5 22,8 M3,8 L5,6 M5,12 L8,10 M12,9 C15,9 18,10 19,12 M8,16 C10,14 14,14 16,16 M11,20 H13")
}

package com.deploydulupulangnanti.fakelivestream

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF0C0D16)
private val Panel = Color(0xFF181A29)
private val Muted = Color(0xFFA5A8BF)
private val Violet = Color(0xFFA998FF)
private val Mint = Color(0xFF69E7D2)
private val Coral = Color(0xFFFF7696)
private val Border = Color(0xFF2D3044)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Violet, onPrimary = Ink, secondary = Mint, background = Ink,
                surface = Panel, onSurface = Color.White, onSurfaceVariant = Muted,
                surfaceContainer = Panel, outline = Border
            )) { LiveMuseApp() }
        }
    }
}

@Composable
fun LiveMuseApp(model: StudioModel = viewModel()) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var endDialog by remember { mutableStateOf(false) }
    LaunchedEffect(model.screen, lifecycle) {
        if (model.screen == "live") lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) { delay(1000); model.tick() }
        }
    }
    BackHandler(enabled = model.screen != "studio") {
        if (model.screen == "live") endDialog = true else model.screen = "studio"
    }
    Surface(color = Ink, modifier = Modifier.fillMaxSize()) {
        if (model.screen == "live") {
            LiveScreen(model, onEnd = { endDialog = true })
        } else {
            Scaffold(containerColor = Ink, bottomBar = {
                if (model.screen != "summary") NavigationBar(containerColor = Ink, tonalElevation = 0.dp) {
                    listOf(Triple("studio", "Studio", StudioIcons.Videocam), Triple("history", "Sessions", StudioIcons.BarChart), Triple("about", "About", StudioIcons.Tune)).forEach { (route, name, icon) ->
                        NavigationBarItem(selected = model.screen == route, onClick = { model.screen = route },
                            icon = { Icon(icon, null) }, label = { Text(name) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Panel, selectedIconColor = Violet, selectedTextColor = Violet))
                    }
                }
            }) { padding ->
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    when (model.screen) {
                        "history" -> HistoryScreen(model)
                        "about" -> AboutScreen(model)
                        "summary" -> SummaryScreen(model)
                        else -> StudioScreen(model)
                    }
                }
            }
        }
    }
    if (endDialog) AlertDialog(onDismissRequest = { endDialog = false },
        title = { Text("Finish your practice?") },
        text = { Text("Your session summary will be saved on this device.") },
        confirmButton = { TextButton(onClick = { endDialog = false; model.finish() }) { Text("Finish session") } },
        dismissButton = { TextButton(onClick = { endDialog = false }) { Text("Keep practicing") } })
}

@Composable
private fun BrandHeader() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Panel), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_brand), null, tint = Color.Unspecified, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text("LiveMuse", fontWeight = FontWeight.Bold, fontSize = 23.sp, letterSpacing = (-0.7).sp)
        Spacer(Modifier.weight(1f))
        Tag("LOCAL STUDIO", Mint)
    }
}

@Composable
private fun StudioScreen(model: StudioModel) {
    BoxWithConstraints(Modifier.widthIn(max = 1120.dp).fillMaxSize()) {
        val wide = maxWidth >= 760.dp
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            BrandHeader()
            Column {
                Text("YOUR MOMENT STARTS HERE", color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                Text("Big energy.\nZero pressure.", fontSize = if (wide) 44.sp else 36.sp, lineHeight = if (wide) 48.sp else 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.5).sp)
                Spacer(Modifier.height(10.dp))
                Text("Find your voice in your own practice room.", color = Muted, fontSize = 14.sp)
            }
            if (wide) Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) { StudioPreview(model.config); OfflineNote() }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) { StudioControls(model) }
            } else {
                StudioPreview(model.config)
                StudioControls(model)
                OfflineNote()
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StudioPreview(config: StudioConfig) {
    Box(Modifier.fillMaxWidth().height(208.dp).clip(RoundedCornerShape(24.dp))) {
        Backdrop(config.scene)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Ink.copy(alpha = .8f)))))
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Tag("✦  ${config.scene.label.uppercase()} STAGE", Color.White)
                Tag("SIMULATION", Mint)
            }
            Column {
                Text("A space to be yourself.", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MiniLabel(StudioIcons.Forum, "Friendly chat")
                    MiniLabel(StudioIcons.Redeem, "Surprise gifts")
                }
            }
        }
    }
}

@Composable
private fun StudioControls(model: StudioModel) {
    var editing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var permissionNote by rememberSaveable { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf(false) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        model.configure(model.config.copy(camera = granted))
        cameraError = !granted
    }
    val config = model.config
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Avatar(config.name, Violet)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(config.name.ifBlank { "Your creator name" }, fontWeight = FontWeight.SemiBold)
            Text(config.title.ifBlank { "My practice room" }, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = { editing = true }) { Icon(StudioIcons.Edit, "Edit profile", tint = Violet) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("01", "Choose your audience")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Crowd.entries.forEach { crowd ->
                val active = config.crowd == crowd
                Surface(onClick = { model.configure(config.copy(crowd = crowd)) }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp), color = if (active) Color(0xFF2A2344) else Panel,
                    border = BorderStroke(1.dp, if (active) Violet else Border)) {
                    Column(Modifier.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(when (crowd) { Crowd.COZY -> "◌"; Crowd.BUZZING -> "✦"; Crowd.SPOTLIGHT -> "✧" }, fontSize = 25.sp, color = if (active) Violet else Muted)
                        Text(crowd.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${compact(crowd.base)} viewers", fontSize = 10.sp, color = Muted)
                    }
                }
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("02", "Set the mood")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scene.entries.forEach { scene -> FilterChip(selected = config.scene == scene, onClick = { model.configure(config.copy(scene = scene)) }, label = { Text(scene.label) }) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Topic.entries.forEach { topic -> FilterChip(selected = config.topic == topic, onClick = { model.configure(config.copy(topic = topic)) }, label = { Text(topic.label, fontSize = 12.sp) }) }
        }
    }
    Surface(shape = RoundedCornerShape(20.dp), color = Panel) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SettingRow(StudioIcons.Redeem, "Random gifts", "A little surprise from your virtual crowd", config.randomGifts) { model.configure(config.copy(randomGifts = it)) }
            HorizontalDivider(color = Border)
            SettingRow(StudioIcons.Videocam, "Use my camera", "Preview only · never recorded", config.camera) {
                if (!it) model.configure(config.copy(camera = false))
                else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) model.configure(config.copy(camera = true))
                else permissionNote = true
            }
        }
    }
    Button(onClick = { model.start() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
        Icon(StudioIcons.PlayArrow, null)
        Spacer(Modifier.width(8.dp))
        Text("Start practice live", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.weight(1f))
        Icon(StudioIcons.ArrowForward, null)
    }
    if (editing) ProfileDialog(config, onClose = { editing = false }, onSave = { model.configure(it); editing = false })
    if (permissionNote) AlertDialog(onDismissRequest = { permissionNote = false }, title = { Text("Your camera, just for you") },
        text = { Text("LiveMuse uses the camera only for a live preview on your screen. No video or audio is recorded, stored, or uploaded. You can also practice with a studio background.") },
        confirmButton = { TextButton(onClick = { permissionNote = false; permission.launch(Manifest.permission.CAMERA) }) { Text("Continue") } },
        dismissButton = { TextButton(onClick = { permissionNote = false }) { Text("Use background") } })
    if (cameraError) AlertDialog(onDismissRequest = { cameraError = false }, title = { Text("Studio mode is ready") },
        text = { Text("Camera access wasn't granted. You can practice with a background, or allow camera access in Android Settings when you're ready.") },
        confirmButton = { TextButton(onClick = { cameraError = false }) { Text("Got it") } })
}

@Composable
private fun ProfileDialog(config: StudioConfig, onClose: () -> Unit, onSave: (StudioConfig) -> Unit) {
    var name by rememberSaveable { mutableStateOf(config.name) }
    var title by rememberSaveable { mutableStateOf(config.title) }
    AlertDialog(onDismissRequest = onClose, title = { Text("Make it yours") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it.take(32) }, label = { Text("Creator name") }, singleLine = true)
            OutlinedTextField(value = title, onValueChange = { title = it.take(80) }, label = { Text("Room title") }, maxLines = 3)
            Text("Stored only on this device.", color = Muted, fontSize = 12.sp)
        } }, confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { onSave(config.copy(name = name.trim(), title = title.trim())) }) { Text("Save profile") } },
        dismissButton = { TextButton(onClick = onClose) { Text("Cancel") } })
}

@Composable
private fun LiveScreen(model: StudioModel, onEnd: () -> Unit) {
    var giftsOpen by remember { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var frontCamera by rememberSaveable { mutableStateOf(true) }
    val state = model.live
    val config = model.config
    val context = LocalContext.current
    val canUseCamera = config.camera && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 760.dp
        Backdrop(config.scene)
        if (canUseCamera) CameraPreview(frontCamera, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Ink.copy(alpha = .65f), Color.Transparent, Ink.copy(alpha = .95f)))))
        Row(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(if (wide) 28.dp else 16.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(config.name, Violet)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(config.name.ifBlank { "Creator" }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${duration(state.seconds)}  ·  Practice room", color = Color.White.copy(.75f), fontSize = 11.sp)
                    }
                    Tag("SIMULATED", Mint)
                    IconButton(onClick = onEnd) { Icon(StudioIcons.Close, "End session") }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPill(StudioIcons.Visibility, compact(state.viewers))
                    StatPill(StudioIcons.FavoriteBorder, compact(state.likes))
                    StatPill(StudioIcons.Redeem, "${state.gifts}")
                }
                Spacer(Modifier.height(20.dp))
                Text(config.title.ifBlank { "My practice room" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                Text("${config.topic.label}  ·  ${config.crowd.label} audience", color = Color.White.copy(.65f), fontSize = 11.sp)
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val showOrb = maxHeight >= 180.dp
                    if (model.paused) Surface(color = Ink.copy(.8f), shape = RoundedCornerShape(24.dp)) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(StudioIcons.Pause, null, tint = Violet, modifier = Modifier.size(36.dp))
                            Text("Take a breath.", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("Your practice is paused.", color = Muted)
                        }
                    } else if (!canUseCamera) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (showOrb) {
                                PulseOrb()
                                Spacer(Modifier.height(14.dp))
                            }
                            Text("The stage is yours", fontSize = 23.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-.5).sp)
                            Text("Speak freely. This is your space.", fontSize = 12.sp, color = Color.White.copy(.65f))
                        }
                    }
                }
                AnimatedContent(targetState = state.gift, label = "Gift arrival") { event ->
                    if (event != null) GiftBanner(event) else Spacer(Modifier.height(4.dp))
                }
                if (!wide) ChatFeed(state.chat.takeLast(4), Modifier.fillMaxWidth().heightIn(max = 175.dp))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = message, onValueChange = { message = it.take(160) }, placeholder = { Text("Say something…", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(28.dp),
                        enabled = !model.paused,
                        trailingIcon = { IconButton(enabled = message.isNotBlank() && !model.paused, onClick = { model.comment(message); message = "" }) { Icon(StudioIcons.Send, "Send comment", tint = Violet) } })
                    IconButton(onClick = { giftsOpen = true }, enabled = !model.paused, modifier = Modifier.background(Violet, CircleShape)) { Icon(StudioIcons.Redeem, "Open gifts", tint = Ink) }
                    IconButton(onClick = { model.like() }, enabled = !model.paused) { Icon(StudioIcons.Favorite, "Send heart", tint = Coral) }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { model.paused = !model.paused }) {
                        Icon(if (model.paused) StudioIcons.PlayArrow else StudioIcons.Pause, null, modifier = Modifier.size(18.dp))
                        Text(if (model.paused) "Resume" else "Pause", fontSize = 12.sp)
                    }
                    if (canUseCamera) IconButton(onClick = { frontCamera = !frontCamera }) { Icon(StudioIcons.Cameraswitch, "Switch camera") }
                    Spacer(Modifier.weight(1f))
                    Text("Virtual audience · No real money", color = Muted, fontSize = 10.sp)
                }
            }
            if (wide) Surface(modifier = Modifier.width(320.dp).fillMaxHeight(), color = Ink.copy(alpha = .78f), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Tag("YOUR PRACTICE ROOM", Mint)
                    Text("Good vibes.\nGreat practice.", fontSize = 29.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)
                    Text("A virtual crowd to help you find your flow.", color = Muted)
                    HorizontalDivider(color = Border)
                    Text("LIVE CHAT", color = Violet, fontSize = 11.sp, letterSpacing = 2.sp)
                    ChatFeed(state.chat, Modifier.weight(1f))
                    OfflineNote()
                }
            }
        }
    }
    if (giftsOpen) GiftSheet(onClose = { giftsOpen = false }, onGift = { model.gift(it); giftsOpen = false })
}

@Composable
private fun PulseOrb() {
    val transition = rememberInfiniteTransition(label = "Studio glow")
    val alpha by transition.animateFloat(.3f, .7f, infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "Glow opacity")
    Box(Modifier.size(118.dp).background(Violet.copy(alpha = alpha * .14f), CircleShape).border(1.dp, Violet.copy(alpha = alpha), CircleShape), contentAlignment = Alignment.Center) {
        Box(Modifier.size(84.dp).background(Violet.copy(alpha = .18f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(StudioIcons.GraphicEq, null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun ChatFeed(chat: List<ChatLine>, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        chat.forEachIndexed { index, line ->
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(26.dp).background(listOf(Violet, Mint, Coral)[index % 3].copy(.22f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(line.name.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(line.name, color = listOf(Violet, Mint, Coral)[index % 3], fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(line.message, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun GiftBanner(event: GiftEvent) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFF302343), modifier = Modifier.padding(vertical = 10.dp).widthIn(max = 360.dp), border = BorderStroke(1.dp, Violet.copy(.4f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(event.gift.symbol, fontSize = 36.sp, color = Color(event.gift.color))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${event.sender} sent ${event.gift.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Virtual gift · just for fun", color = Muted, fontSize = 10.sp)
            }
            Text("×1", fontSize = 21.sp, color = Violet, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftSheet(onClose: () -> Unit, onGift: (Gift) -> Unit) {
    ModalBottomSheet(onDismissRequest = onClose, containerColor = Panel,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().align(Alignment.CenterHorizontally).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("A little extra magic.", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Trigger a gift from your virtual audience.", color = Muted, fontSize = 14.sp)
            giftCatalog.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { gift ->
                        Surface(onClick = { onGift(gift) }, modifier = Modifier.weight(1f), color = Ink, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Border)) {
                            Column(Modifier.padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(gift.symbol, fontSize = 34.sp, color = Color(gift.color))
                                Spacer(Modifier.height(6.dp))
                                Text(gift.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("FREE VIRTUAL GIFT", fontSize = 7.sp, color = Muted)
                            }
                        }
                    }
                }
            }
            Text("No purchases. No cash value. Just a moment of joy.", color = Muted, fontSize = 11.sp)
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HistoryScreen(model: StudioModel) {
    Column(Modifier.widthIn(max = 900.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        BrandHeader()
        Text("Every session.\nA little more you.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
        Text("Your practice journal, saved only on this device.", color = Muted)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metric("${model.history.size}", "Sessions", Modifier.weight(1f), Violet)
            Metric("${model.history.sumOf { it.seconds } / 60}", "Minutes practiced", Modifier.weight(1f), Mint)
        }
        if (model.history.isEmpty()) {
            Surface(shape = RoundedCornerShape(24.dp), color = Panel) {
                Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(StudioIcons.AutoAwesome, null, tint = Violet, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Your first moment is waiting.", fontWeight = FontWeight.Bold)
                    Text("Finish a practice live to see it here.", color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { model.screen = "studio" }) { Text("Go to studio") }
                }
            }
        } else model.history.forEach { record ->
            Surface(shape = RoundedCornerShape(20.dp), color = Panel) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.ENGLISH).format(Date(record.date)), color = Violet, fontSize = 11.sp)
                    Text(record.title, fontWeight = FontWeight.Bold)
                    Text("${duration(record.seconds)} practiced  ·  ${compact(record.peak)} peak viewers", color = Muted, fontSize = 12.sp)
                    Text("${compact(record.likes)} hearts  ·  ${record.gifts} gifts  ·  All simulated", color = Mint, fontSize = 11.sp)
                }
            }
        }
        Text("Keeps your 50 most recent sessions. Audience metrics are simulated.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun SummaryScreen(model: StudioModel) {
    val record = model.latest ?: return
    Column(Modifier.widthIn(max = 640.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(28.dp), verticalArrangement = Arrangement.spacedBy(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(12.dp))
        Tag("PRACTICE COMPLETE", Mint)
        PulseOrb()
        Text("Look at you go.", fontSize = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
        Text("One more step toward feeling at home on camera.", color = Muted)
        Surface(shape = RoundedCornerShape(24.dp), color = Panel) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(record.title, fontWeight = FontWeight.SemiBold)
                Row {
                    Metric(duration(record.seconds), "Time practiced", Modifier.weight(1f), Violet)
                    Metric(compact(record.peak), "Peak virtual viewers", Modifier.weight(1f), Mint)
                }
                Row {
                    Metric(compact(record.likes), "Simulated hearts", Modifier.weight(1f), Coral)
                    Metric("${record.gifts}", "Virtual gifts", Modifier.weight(1f), Color(0xFFFFD185))
                }
            }
        }
        Text("The confidence is yours.\nThe audience and gifts are simulated.", color = Muted, fontSize = 13.sp)
        Button(onClick = { model.screen = "studio" }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) { Text("Back to studio", fontWeight = FontWeight.Bold) }
        TextButton(onClick = { model.screen = "history" }) { Text("View my sessions") }
    }
}

@Composable
private fun AboutScreen(model: StudioModel) {
    val context = LocalContext.current
    var clearDialog by remember { mutableStateOf(false) }
    var privacy by remember { mutableStateOf(false) }
    Column(Modifier.widthIn(max = 800.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        BrandHeader()
        Text("Your space.\nYour peace of mind.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
        Text("A creative playground for your next big idea.", color = Muted)
        InfoCard(StudioIcons.WifiOff, "Offline by design", "No accounts, ads, trackers, or internet permission. Your practice stays on your device.")
        InfoCard(StudioIcons.Shield, "A simulation, always", "Viewers, chat, likes, and gifts are generated locally. Nothing is broadcast. Gifts have no monetary value.")
        InfoCard(StudioIcons.Videocam, "Camera on your terms", "Camera access is optional and used only for preview. LiveMuse never records video or audio.")
        OutlinedButton(onClick = { privacy = true }, modifier = Modifier.fillMaxWidth()) { Text("Privacy policy") }
        OutlinedButton(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:seandyadryan2@gmail.com")).putExtra(Intent.EXTRA_SUBJECT, "LiveMuse support")
            runCatching { context.startActivity(Intent.createChooser(intent, "Contact support")) }
                .onFailure { android.widget.Toast.makeText(context, "Email seandyadryan2@gmail.com", android.widget.Toast.LENGTH_LONG).show() }
        }, modifier = Modifier.fillMaxWidth()) { Text("Contact support") }
        TextButton(onClick = { clearDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Delete session history", color = Coral) }
        Text("LiveMuse ${BuildConfig.VERSION_NAME}\nPT Deploy Dulu Pulang Nanti\nseandyadryan2@gmail.com", fontSize = 12.sp, color = Muted, lineHeight = 20.sp)
        Text("An independent live simulator. Not affiliated with TikTok or any social media platform.", color = Muted, fontSize = 11.sp)
    }
    if (clearDialog) AlertDialog(onDismissRequest = { clearDialog = false }, title = { Text("Delete your session history?") },
        text = { Text("This removes all saved session summaries from this device. It cannot be undone.") },
        confirmButton = { TextButton(onClick = { model.clearHistory(); clearDialog = false }) { Text("Delete history") } },
        dismissButton = { TextButton(onClick = { clearDialog = false }) { Text("Cancel") } })
    if (privacy) AlertDialog(onDismissRequest = { privacy = false }, title = { Text("Privacy policy") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) { Text(PRIVACY_TEXT, fontSize = 13.sp) } },
        confirmButton = { TextButton(onClick = { privacy = false }) { Text("Close policy") } })
}

@Composable
private fun Backdrop(scene: Scene) {
    Box(Modifier.fillMaxSize()) {
        if (scene == Scene.AURORA) Image(painterResource(R.drawable.studio_aurora), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Box(Modifier.fillMaxSize().background(Brush.linearGradient(if (scene == Scene.SUNSET) listOf(Color(0xFF5B2A4C), Color(0xFF30253E), Color(0xFF101522)) else listOf(Color(0xFF192E43), Color(0xFF111A32), Color(0xFF080B19)))))
    }
}
@Composable
private fun Tag(text: String, color: Color) {
    Surface(color = Ink.copy(.6f), shape = RoundedCornerShape(8.dp)) { Text(text, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .6.sp) }
}
@Composable
private fun Avatar(name: String, color: Color) {
    Box(Modifier.size(42.dp).background(color.copy(.18f), CircleShape).border(1.dp, color.copy(.5f), CircleShape), contentAlignment = Alignment.Center) {
        Text(name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
@Composable
private fun SectionLabel(number: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(number, color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
@Composable
private fun MiniLabel(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, Modifier.size(14.dp), tint = Mint)
        Text(text, fontSize = 10.sp, color = Color.White.copy(.8f))
    }
}
@Composable
private fun StatPill(icon: ImageVector, text: String) {
    Surface(color = Ink.copy(.6f), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, null, Modifier.size(14.dp), tint = Violet)
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Icon(icon, null, tint = Violet, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 9.sp, color = Muted, lineHeight = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onChange, modifier = Modifier.padding(start = 6.dp).semantics { contentDescription = title })
    }
}
@Composable
private fun OfflineNote() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(StudioIcons.Shield, null, tint = Mint, modifier = Modifier.size(16.dp))
        Text("100% practice. No real broadcast.\nViewers, comments, and gifts are simulated.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
@Composable
private fun Metric(value: String, label: String, modifier: Modifier, color: Color) {
    Column(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = Muted)
    }
}
@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Panel) {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = Violet)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(title, fontWeight = FontWeight.Bold); Text(body, color = Muted, fontSize = 13.sp, lineHeight = 20.sp) }
        }
    }
}

const val PRIVACY_TEXT = """Effective date: October 20, 2026

LiveMuse is provided by PT Deploy Dulu Pulang Nanti.

Data collection
We do not collect, transmit, sell, or share personal data through the app. LiveMuse has no internet permission, accounts, analytics SDKs, advertising SDKs, or in-app purchases.

Camera
If you grant camera permission, the camera is used only for an on-screen preview while the practice room is open. Video is not recorded, saved, or sent anywhere. Audio is not captured. You can deny or revoke camera permission in Android Settings and continue using studio backgrounds.

Local information
Your creator name, room preferences, and up to 50 session summaries are saved in the app's private storage on your device. Comments are held only for the current session. Android app backup is disabled. Delete session summaries from About, or remove all app data through Android Settings or by uninstalling the app.

Simulated interactions
Viewers, comments, likes, and gifts are fictional, generated on your device. Gifts have no cash value. No content is broadcast to other people.

Support and external services
If you choose Contact support, your email app handles the message under its own privacy policy. We receive the email address and information you choose to send and use them only to respond to support requests. Request deletion of support correspondence by email. Google Play may process purchase or store-related information under Google's own policies; LiveMuse does not receive payment card details.

Children
LiveMuse is intended for a general audience and is not specifically directed at children under 13. We do not knowingly collect children's personal information through the app.

Changes and contact
This policy may be updated when the app changes. Updates will include a new effective date.
PT Deploy Dulu Pulang Nanti
seandyadryan2@gmail.com"""

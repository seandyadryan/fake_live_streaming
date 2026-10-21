package com.deploydulupulangnanti.fakelivestream

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val LivePink = Color(0xFFFF3366)

@Composable
private fun stagePhoto(name: String?): ImageBitmap? {
    val context = LocalContext.current
    val image by produceState<ImageBitmap?>(null, name) {
        value = withContext(Dispatchers.IO) {
            runCatching { PhotoStore.file(context, name)?.let { BitmapFactory.decodeFile(it.path)?.asImageBitmap() } }.getOrNull()
        }
    }
    return image
}

@Composable
internal fun StageBackground(config: StudioConfig) {
    val photo = stagePhoto(config.photo)
    if (photo != null) Image(photo, "Your live photo", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    else Backdrop(config.scene)
}

@Composable
private fun HostAvatar(config: StudioConfig, size: Dp = 38.dp) {
    val photo = stagePhoto(config.photo)
    if (photo != null) Image(photo, null, Modifier.size(size).clip(CircleShape).border(1.dp, Color.White.copy(.5f), CircleShape), contentScale = ContentScale.Crop)
    else Box(Modifier.size(size).background(Violet.copy(.35f), CircleShape), contentAlignment = Alignment.Center) {
        Text(config.name.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
internal fun LiveScreen(model: StudioModel, onEnd: () -> Unit) {
    var giftsOpen by remember { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var frontCamera by rememberSaveable { mutableStateOf(true) }
    var followed by rememberSaveable { mutableStateOf(false) }
    val state = model.live
    val config = model.config
    val context = LocalContext.current
    val canUseCamera = config.camera && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compactRoom = maxHeight < 560.dp || WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val wide = maxWidth >= 760.dp && !compactRoom
        StageBackground(config)
        if (canUseCamera) CameraPreview(frontCamera, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(.65f), Color.Transparent, Color.Black.copy(.9f)))))
        if (!compactRoom && !model.paused) FloatingHearts(Modifier.align(Alignment.BottomEnd).padding(end = if (wide) 370.dp else 12.dp, bottom = 148.dp))
        Row(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(horizontal = if (wide) 28.dp else 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(color = Color.Black.copy(.48f), shape = RoundedCornerShape(40.dp), modifier = Modifier.weight(1f)) {
                        Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            HostAvatar(config)
                            Column(Modifier.weight(1f).padding(horizontal = 7.dp)) {
                                Text(config.name.ifBlank { "Creator" }, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("♥ ${compact(state.likes)}", fontSize = 11.sp, color = Color.White.copy(.85f))
                            }
                            Surface(onClick = { followed = !followed }, color = if (followed) Color.White.copy(.15f) else LivePink, shape = RoundedCornerShape(30.dp)) {
                                Box(Modifier.heightIn(min = 48.dp).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                                    Text(if (followed) "Following" else "+ Follow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Surface(color = Color.Black.copy(.48f), shape = CircleShape) {
                        Row(Modifier.heightIn(min = 48.dp).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(StudioIcons.Visibility, "Viewers", Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(compact(state.viewers), fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = onEnd, modifier = Modifier.size(48.dp)) { Icon(StudioIcons.Close, "End session", Modifier.size(28.dp)) }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("LIVE  ${duration(state.seconds)}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    if (!compactRoom) Text("✦ ${state.gifts} gifts", fontSize = 11.sp, color = Color(0xFFFFD185))
                }
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val celebrationSize = minOf(148.dp, (maxHeight - 55.dp).coerceAtLeast(48.dp))
                    val hasStageSpace = maxHeight >= 110.dp
                    if (model.paused && !compactRoom && hasStageSpace) Surface(color = Color.Black.copy(.7f), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Take a breath.", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                            Text("Your practice is paused.", fontSize = 12.sp, color = Muted)
                        }
                    } else if (!compactRoom && !model.paused && hasStageSpace) {
                        AnimatedContent(targetState = state.gift, label = "Gift celebration") { event ->
                            if (event != null) GiftCelebration(event, celebrationSize)
                            else if (config.photo == null && !canUseCamera) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(StudioIcons.GraphicEq, null, tint = Color.White.copy(.65f), modifier = Modifier.size(48.dp))
                                Text("Your moment, your stage", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("LiveMuse practice room", color = Color.White.copy(.6f), fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (!compactRoom && state.gift != null) GiftBanner(state.gift)
                if (!wide) ChatFeed(state.chat.takeLast(if (compactRoom) 2 else 4), Modifier.fillMaxWidth().padding(end = if (compactRoom) 0.dp else 40.dp).heightIn(max = if (compactRoom) 90.dp else 180.dp))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(value = message, onValueChange = { message = it.take(160) }, placeholder = { Text(stringResource(R.string.chat_input_hint), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(30.dp), enabled = !model.paused,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.Black.copy(.4f), unfocusedBorderColor = Color.Transparent, focusedBorderColor = LivePink),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { model.comment(message); message = "" }),
                        trailingIcon = { if (message.isNotBlank()) IconButton(enabled = !model.paused, onClick = { model.comment(message); message = "" }) { Icon(StudioIcons.Send, stringResource(R.string.chat_send_action), tint = Color.White) } })
                    IconButton(onClick = { model.gift(giftCatalog.first()) }, enabled = !model.paused, modifier = Modifier.background(Color.Black.copy(.4f), CircleShape)) {
                        GiftArt(giftCatalog.first(), 35.dp, "Send rose")
                    }
                    IconButton(onClick = { giftsOpen = true }, enabled = !model.paused, modifier = Modifier.background(Color.Black.copy(.4f), CircleShape)) { Icon(StudioIcons.Redeem, "Open gifts", tint = LivePink, modifier = Modifier.size(30.dp)) }
                    IconButton(onClick = model::like, enabled = !model.paused) { Icon(StudioIcons.Favorite, "Send heart", tint = LivePink) }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { model.paused = !model.paused }) {
                        Icon(if (model.paused) StudioIcons.PlayArrow else StudioIcons.Pause, null, Modifier.size(16.dp))
                        Text(if (model.paused) "Resume" else "Pause", fontSize = 11.sp)
                    }
                    if (canUseCamera) IconButton(onClick = { frontCamera = !frontCamera }) { Icon(StudioIcons.Cameraswitch, "Switch camera") }
                    Spacer(Modifier.weight(1f))
                }
            }
            if (wide) Surface(Modifier.width(320.dp).fillMaxHeight(), color = Color.Black.copy(.55f), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.chat_heading), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Border)
                    ChatFeed(state.chat, Modifier.weight(1f))
                }
            }
        }
    }
    if (giftsOpen) GiftSheet(model, onClose = { giftsOpen = false })
}

@Composable
private fun FloatingHearts(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "Floating hearts")
    val phase by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(3600, easing = LinearEasing)), label = "Heart rise")
    Box(modifier.width(46.dp).height(230.dp)) {
        repeat(5) { index ->
            val progress = (phase + index / 5f) % 1f
            Icon(StudioIcons.Favorite, null, tint = listOf(LivePink, Coral, Violet)[index % 3], modifier = Modifier.align(Alignment.BottomCenter)
                .offset(x = (kotlin.math.sin(progress * 8 + index) * 12).dp, y = (-progress * 210).dp)
                .size((20 + index * 3).dp).alpha((1 - progress) * .85f))
        }
    }
}

@Composable
internal fun GiftArt(gift: Gift, size: Dp, description: String? = null) {
    val resource = when (gift.id) {
        "rose" -> R.drawable.gift_rose
        "coffee" -> R.drawable.gift_coffee
        "heart" -> R.drawable.gift_heart
        "bloom" -> R.drawable.gift_bloom
        "comet" -> R.drawable.gift_comet
        "crown" -> R.drawable.gift_crown
        else -> null
    }
    if (resource != null) Image(painterResource(resource), description, Modifier.size(size), contentScale = ContentScale.Fit)
    else Icon(if (gift.id == "gem") StudioIcons.AutoAwesome else StudioIcons.GraphicEq, description, Modifier.size(size).padding(size * .17f), tint = Color(gift.color))
}

@Composable
private fun GiftCelebration(event: GiftEvent, size: Dp) {
    val scale = remember(event.id) { Animatable(.45f) }
    LaunchedEffect(event.id) { scale.animateTo(1f, spring(dampingRatio = .55f)) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.scale(scale.value)) {
        GiftArt(event.gift, size)
        Text("${event.gift.name} ×${event.combo}", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("A gift from ${event.sender}", fontSize = 11.sp, color = Color.White.copy(.8f))
    }
}

@Composable
private fun GiftBanner(event: GiftEvent) {
    Surface(shape = RoundedCornerShape(32.dp), color = Color(0xFF462348).copy(.85f), modifier = Modifier.padding(bottom = 12.dp).widthIn(max = 310.dp)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(event.sender, Coral)
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(event.sender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Sent ${event.gift.name}", fontSize = 10.sp, color = Color.White.copy(.75f))
            }
            GiftArt(event.gift, 48.dp)
            Text("×${event.combo}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD185))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftSheet(model: StudioModel, onClose: () -> Unit) {
    var category by rememberSaveable { mutableStateOf("All gifts") }
    var selectedId by rememberSaveable { mutableStateOf(giftCatalog.first().id) }
    var sent by remember { mutableStateOf(false) }
    val selected = giftCatalog.first { it.id == selectedId }
    val catalog = when (category) { "Popular" -> giftCatalog.filter { !it.premium }; "Celebration" -> giftCatalog.filter { it.premium }; else -> giftCatalog }
    ModalBottomSheet(onDismissRequest = onClose, containerColor = Color(0xFF29292D), sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.widthIn(max = 680.dp).fillMaxWidth().align(Alignment.CenterHorizontally).navigationBarsPadding().padding(horizontal = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Virtual gifts", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Unlimited demo coins · No purchases", color = Color(0xFFFFD185), fontSize = 11.sp)
                }
                IconButton(onClick = onClose) { Icon(StudioIcons.Close, "Close gifts") }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All gifts", "Popular", "Celebration").forEach { name ->
                    FilterChip(selected = category == name, onClick = { category = name; sent = false }, label = { Text(name, fontSize = 12.sp) })
                }
            }
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                catalog.chunked(4).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        row.forEach { gift ->
                            Surface(onClick = { selectedId = gift.id; sent = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                                color = if (selectedId == gift.id) Color(0xFF484138) else Color.Transparent,
                                border = if (selectedId == gift.id) BorderStroke(1.dp, Color(0xFFFFD185)) else null) {
                                Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    GiftArt(gift, 65.dp)
                                    Text(gift.name, fontSize = 11.sp, maxLines = 1)
                                    Text("● ${compact(gift.coins)}", color = Color(0xFFFFD185), fontSize = 11.sp)
                                }
                            }
                        }
                        repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
            HorizontalDivider(Modifier.padding(top = 12.dp), color = Color.White.copy(.1f))
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (sent) "${selected.name} sent! ×${model.live.gift?.combo ?: 1}" else selected.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(onClick = { model.gift(selected); sent = true }, enabled = !model.paused, colors = ButtonDefaults.buttonColors(containerColor = LivePink, contentColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                    Text(if (sent) "Combo +1" else "Send gift", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

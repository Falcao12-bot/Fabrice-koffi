package com.example.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.home.EDUCI_SHARED_WEB_URL
import com.example.ui.screens.home.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.ThemeMode

@Composable
fun ProfileScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val completedLessons by viewModel.userCompletedLessonsCount.collectAsStateWithLifecycle()
    val attemptsCount by viewModel.userExerciseAttemptsCount.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showClassDialog by remember { mutableStateOf(false) }
    var showPwaInstallDialog by remember { mutableStateOf(false) }
    var showOfflineDialog by remember { mutableStateOf(false) }

    val classesList = listOf("CP1", "CP2", "CE1", "CE2", "CM1", "CM2", "6e", "5e", "4e", "3e", "2nde", "1ère", "Terminale")

    val u = user ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Surface(
                        shape = CircleShape,
                        color = EduCiGreenPrimary,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${u.firstName.firstOrNull() ?: 'E'}${u.lastName.firstOrNull() ?: 'D'}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${u.firstName} ${u.lastName}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = u.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Classe : ${u.className}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (u.isPremium) EduCiGoldXp else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (u.isPremium) "⭐ Membre Premium" else "Compte Gratuit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (u.isPremium) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Theme Mode Selector Card (Vert-Blanc / Sombre / Système)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Thème de l'application",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (themeMode) {
                                        ThemeMode.LIGHT -> "Mode Clair (Fond Vert-Blanc)"
                                        ThemeMode.DARK -> "Mode Sombre (Nuit Émeraude)"
                                        ThemeMode.SYSTEM -> "Automatique (Système)"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = themeMode == ThemeMode.DARK,
                            onCheckedChange = { isDark ->
                                viewModel.setThemeMode(if (isDark) ThemeMode.DARK else ThemeMode.LIGHT)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EduCiDarkPrimary,
                                checkedTrackColor = EduCiDarkPrimaryContainer,
                                uncheckedThumbColor = EduCiGreenPrimary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                            label = { Text("🌿 Vert-Blanc", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                            label = { Text("🌙 Sombre", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                            label = { Text("⚙️ Système", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Stats summary
        item {
            Text(
                text = "Mes Statistiques d'Apprentissage",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Exercices faits",
                    value = "$attemptsCount",
                    icon = Icons.Default.Quiz,
                    iconColor = EduCiGreenPrimary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Temps d'étude",
                    value = "${u.studyTimeMinutes} min",
                    icon = Icons.Default.Schedule,
                    iconColor = EduCiOrangeAccent
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Leçons vues",
                    value = "$completedLessons",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconColor = Color(0xFF2563EB)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "XP Total",
                    value = "${u.xp} XP",
                    icon = Icons.Default.Star,
                    iconColor = EduCiGoldXp
                )
            }
        }

        // Premium Promo / Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (u.isPremium) EduCiGoldLight else Color(0xFFFFFBEB)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "👑", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (u.isPremium) "Abonnement Premium Actif" else "Passer à EduCI Premium",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF78350F)
                            )
                            Text(
                                text = if (u.isPremium) "Accès illimité à tous les examens et à l'IA" else "Tous les corrigés officiels, examens et IA illimitée",
                                fontSize = 12.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.togglePremium() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (u.isPremium) Color(0xFF92400E) else EduCiGoldXp
                        )
                    ) {
                        Text(
                            text = if (u.isPremium) "Désactiver Premium (Démo)" else "Activer EduCI Premium (1 500 FCFA/mois)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Settings list
        item {
            Text(
                text = "Paramètres de l'Application",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuRow(
                        title = "Changer ma classe actuelle",
                        subtitle = "Actuellement en classe de ${u.className}",
                        icon = Icons.Default.School,
                        onClick = { showClassDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "Notifications & Annonces",
                        subtitle = "Voir les alertes de nouveaux cours",
                        icon = Icons.Default.Notifications,
                        onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "Mode Hors Connexion",
                        subtitle = "Base locale SQLite synchronisée",
                        icon = Icons.Default.CloudDone,
                        onClick = { showOfflineDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "Installer l'application",
                        subtitle = "Guide d'installation PWA / Mobile",
                        icon = Icons.Default.Download,
                        onClick = { showPwaInstallDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "Espace Administrateur",
                        subtitle = "Gérer les leçons, examens et élèves",
                        icon = Icons.Default.AdminPanelSettings,
                        iconTint = EduCiOrangeAccent,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "Déconnexion",
                        subtitle = "Quitter la session actuelle",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        iconTint = Color(0xFFDC2626),
                        onClick = { viewModel.logout() }
                    )
                }
            }
        }
    }

    // Dialog for changing class
    if (showClassDialog) {
        AlertDialog(
            onDismissRequest = { showClassDialog = false },
            title = { Text("Sélectionner ma classe") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (c in classesList) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectClass(c)
                                    showClassDialog = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (u.className == c) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = c,
                                fontWeight = if (u.className == c) FontWeight.Bold else FontWeight.Normal,
                                color = if (u.className == c) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClassDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }

    // Dialog for PWA installation instructions
    if (showPwaInstallDialog) {
        AlertDialog(
            onDismissRequest = { showPwaInstallDialog = false },
            icon = { Icon(Icons.Default.InstallMobile, contentDescription = null, tint = EduCiGreenPrimary, modifier = Modifier.size(32.dp)) },
            title = { Text("Accès en Ligne & Installation Mobile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "EduCI est disponible en ligne et installable sur tous les smartphones (Android & iOS) :",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = EDUCI_SHARED_WEB_URL,
                                fontSize = 11.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("URL EduCI", EDUCI_SHARED_WEB_URL)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "URL copiée !", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copier", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Accédez à EduCI : $EDUCI_SHARED_WEB_URL")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Partager l'application"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Partager", fontSize = 11.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📲 Installation Rapide sur Mobile (PWA)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Ouvrez le lien sur votre téléphone (Chrome ou Safari).\n2. Appuyez sur le menu (⋮) ou l'icône Partager.\n3. Choisissez « Ajouter à l'écran d'accueil ».\n4. L'application apparaîtra avec son icône sur votre écran d'accueil !",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📦 Fichier APK Android",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Téléchargez directement le fichier .apk depuis le menu d'exportation en haut à droite d'AI Studio pour l'installer sur n'importe quel smartphone Android.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPwaInstallDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EduCiGreenPrimary)
                ) {
                    Text("J'ai compris")
                }
            }
        )
    }

    // Dialog for offline mode info
    if (showOfflineDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineDialog = false },
            icon = { Icon(Icons.Default.CloudDone, contentDescription = null, tint = EduCiGreenPrimary) },
            title = { Text("Mode Hors Connexion Prêt") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tous tes cours, exercices et sujets d'examens sont automatiquement sauvegardés dans la base de données interne de ton appareil.")
                    Text("Tu peux réviser sans connexion Internet même dans les zones sans réseau.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showOfflineDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EduCiGreenPrimary)
                ) {
                    Text("Super !")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = EduCiGreenPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

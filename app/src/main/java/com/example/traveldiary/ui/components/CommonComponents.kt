package com.example.traveldiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "BIENVENIDO DE NUEVO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Mi Diario de Viaje",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondary,
                        )
                    )
                )
                .padding(2.5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "AL",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String = "",
    onQueryChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        placeholder = {
            Text(
                "Buscar destinos, recuerdos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        singleLine = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(
    chips: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            FilterChip(
                selected = chip == selectedChip,
                onClick = { onChipSelected(chip) },
                label = {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = chip == selectedChip,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        TextButton(onClick = {}) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
        val items = listOf(
            Triple("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
            Triple("Mapa", Icons.Filled.Map, Icons.Outlined.Map),
            Triple("Recuerdos", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
            Triple("Perfil", Icons.Filled.Person, Icons.Outlined.Person),
        )
        items.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) filledIcon else outlinedIcon,
                        contentDescription = label,
                    )
                },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

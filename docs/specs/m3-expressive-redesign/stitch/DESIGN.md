

## Brand & Style

This design system embodies an introspective, premium nocturnal expedition aesthetic tailored for Android Material 3 Expressive. Built for travelers documenting memories, routes, and emotional landscapes, it evokes intimacy, quiet luxury, and timelessness. The aesthetic rejects playful novelty and emoji iconography in favor of editorial dignity, evoking a leather-bound, gilded celestial notebook rendered on modern glass.

The visual tone balances Material 3 Expressive's bold, kinetic nature with the contemplative gravity of night-time travel: deep atmospheric depths, incandescent amber highlights evoking vintage lantern light or golden-hour memories, and subdued, archival slate blues. All copy and architectural messaging are structured in polished Spanish, maintaining an articulate, poetic editorial standard.

## Layout & Spacing

Layouts adhere to an Android-first fluid vertical scroll grid designed for single-hand interaction. The rhythm is governed by a strict 4-column layout on compact mobile viewports, stepping to an 8-column layout on foldable expanded states and tablets.

- **Canvas Margins**: Default mobile outer margin is fixed at `1.25rem` (20dp), maintaining optical edge clearance for squircle cards while maximizing entry reading width.
- **Vertical Hierarchy**: Spacing scales dynamically between entries: `space-xl` separates distinct narrative dates, while `space-sm` groups interrelated metadata (time, location pill, temperature).
- **Safe Areas**: Top margins accommodate dynamic Android edge-to-edge status bars; bottom margins include generous trailing padding (`2.5rem`) to guarantee effortless scrolling clearance above the floating action bar.

## Elevation & Depth

Depth is established primarily through **tonal elevation tiers** rather than heavy dropped drop-shadows, mirroring modern OLED-optimized Material 3 engineering.

- **Level 0 (Root Canvas)**: `#0A0A14` with zero shadow.
- **Level 1 (Entry Cards & Timeline Logs)**: `#131422` with a 1px inner ambient border tinted at `rgba(255, 255, 255, 0.04)` to separate adjacent deep surfaces.
- **Level 2 (Active Cards, Modals, Top App Bar on Scroll)**: `#1A1C2E` with an ambient glow shadow: `0 8px 24px -4px rgba(0, 0, 0, 0.55), 0 2px 6px -1px rgba(10, 10, 20, 0.4)`.
- **Level 3 (FAB & Floating Dialogs)**: `#23263E` or solid `#FFB800` paired with a directional amber ambient aura when hovered or pressed: `0 12px 32px -4px rgba(255, 184, 0, 0.25)`.
- **Outlines**: Never utilize solid 100% opaque borders. Borders rely exclusively on sub-surface stroke thresholds (`#2E3352` at 50% opacity) for subtle tactile definition.

## Components

- **Botones (Buttons)**:
  - *Primario*: Background in `#FFB800`, text and icons in `#0A0A14` (`label-lg`, Epilogue Bold), 20px radius, 56px touch target height.
  - *Secundario / Tonal*: Background in `#1A1C2E`, text in `#F0F2FA`, subtle border stroke of `rgba(143, 163, 184, 0.2)`.
  - *Terciario (Ghost)*: Unfilled, color `#8FA3B8`, interactive ripple in `#FFB800` at 10% opacity.

- **Fichas (Chips)**:
  - Contextual tags (e.g., *Ruta*, *Reflexión*, *Fotografía*) render at 36px height with 16px radius.
  - Background `#131422`, text `#8FA3B8`. When selected, background shifts to `#FFB800` with text `#0A0A14` and a high-contrast tick icon. No emojis are permitted; geometric vector glyphs only.

- **Tarjetas de Entrada (Diary Cards)**:
  - Constructed using `#131422` with a 28px radius and `space-md` internal padding.
  - Features an expressive date block: day in `display-md` (`#FFB800`), month and year in `label-sm` (`#8FA3B8`).
  - Narrative preview rendered in `body-md` (`#F0F2FA`) truncated cleanly after 3 lines.

- **Campos de Texto (Inputs)**:
  - Material filled-variant with 20px top squircle curvature and flat bottom or full 20px enclosed container.
  - Background `#1A1C2E`, active indicator border in `#FFB800` (2px).
  - Floating labels in `label-md` (`#8FA3B8`). Placeholder text remains muted in `#5C6479`.

- **Listas y Cronología (Timeline Lists)**:
  - Entries are linked through a continuous 2px vertical stroke in `#1A1C2E`.
  - Timeline node points utilize a concentric dot: 12px circle in `#FFB800` surrounded by an 8px halo of `rgba(255, 184, 0, 0.15)`.

- **Botón de Acción Flotante (Expressive FAB)**:
  - Sits at bottom-right, offset by `margin`. 64x64dp rounded container with 24px radius.
  - High-intensity `#FFB800` surface housing a crisp feather-weight quill or plus iconography in `#0A0A14`.

- **Controles de Selección (Checkboxes & Radios)**:
  - 24px container with 8px radius for checkboxes, full circle for radios.
  - Unselected state uses 2px stroke in `#8FA3B8`. Selected state fills with `#FFB800` featuring a `#0A0A14` internal checkmark.
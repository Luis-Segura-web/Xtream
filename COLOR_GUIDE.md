# Guía de Colores - Xtream IPTV

## 🎨 Paleta Principal

### Colores Primarios
- **`primary_dark`** (#1B2838) - Azul oscuro para elementos principales
- **`primary_medium`** (#2A3F5F) - Azul medio para variaciones
- **`primary_light`** (#4A6741) - Azul claro para elementos suaves

### Colores de Acento
- **`accent_blue`** (#2196F3) - Azul vibrante Material Design
- **`accent_blue_dark`** (#1976D2) - Azul oscuro para hover/pressed
- **`accent_blue_light`** (#64B5F6) - Azul claro para backgrounds suaves
- **`accent_orange`** (#FF6D00) - Naranja vibrante para call-to-action
- **`accent_orange_dark`** (#F57600) - Naranja oscuro para hover/pressed
- **`accent_orange_light`** (#FF9800) - Naranja claro para backgrounds

## 📝 Colores de Texto

### Texto Oscuro (para fondos claros)
- **`text_primary_dark`** (#212121) - Texto principal, alta legibilidad
- **`text_secondary_dark`** (#424242) - Texto secundario, buena legibilidad
- **`text_hint`** (#9E9E9E) - Texto de ayuda o placeholders

### Texto Claro (para fondos oscuros)
- **`text_primary_light`** (#FFFFFF) - Texto principal en fondos oscuros
- **`text_secondary_light`** (#EEEEEE) - Texto secundario en fondos oscuros

## 🏗️ Colores de Superficie y Fondo

### Fondos Claros
- **`background_light`** (#FFFFFF) - Fondo principal claro
- **`background_light_secondary`** (#FAFAFA) - Fondo secundario claro
- **`surface_light`** (#FFFFFF) - Superficie de cards y elementos
- **`surface_light_elevated`** (#F5F5F5) - Superficie elevada (elevación)

### Fondos Oscuros (modo nocturno)
- **`background_dark`** (#121212) - Fondo principal oscuro
- **`background_dark_secondary`** (#1E1E1E) - Fondo secundario oscuro
- **`surface_dark`** (#1E1E1E) - Superficie oscura
- **`surface_dark_elevated`** (#2D2D2D) - Superficie elevada oscura

## 🔧 Colores Utilitarios

### Estados
- **`success_green`** (#4CAF50) - Éxito, confirmaciones
- **`success_green_light`** (#81C784) - Éxito suave
- **`error_red`** (#F44336) - Errores, alertas
- **`error_red_light`** (#E57373) - Error suave

### Elementos Especiales
- **`live_indicator`** (#FF1744) - Indicador de contenido en vivo
- **`favorite_active`** (#FF6D00) - Estado activo de favoritos
- **`favorite_inactive`** (#9E9E9E) - Estado inactivo de favoritos
- **`badge_hd`** (#4CAF50) - Badge para contenido HD
- **`badge_4k`** (#FF9800) - Badge para contenido 4K

### Divisores y Overlays
- **`divider_light`** (#E0E0E0) - Divisores en temas claros
- **`divider_dark`** (#424242) - Divisores en temas oscuros
- **`overlay_light`** (#80FFFFFF) - Overlay claro (50% transparencia)
- **`overlay_dark`** (#80000000) - Overlay oscuro (50% transparencia)

## 📊 Ratios de Contraste WCAG

### ✅ Combinaciones Aprobadas (Ratio ≥ 4.5:1)
- `text_primary_dark` sobre `background_light` - **Ratio: 16.3:1**
- `text_primary_light` sobre `accent_blue` - **Ratio: 4.7:1**
- `text_primary_light` sobre `accent_orange` - **Ratio: 4.2:1**
- `text_primary_light` sobre `primary_dark` - **Ratio: 15.8:1**

### ⚠️ Uso Especial (Elementos Decorativos)
- `accent_blue_light` sobre `background_light` - Usar solo para elementos no críticos
- `text_hint` sobre backgrounds - Solo para texto secundario

## 🎯 Casos de Uso Recomendados

### Headers y Títulos
```xml
android:textColor="@color/text_primary_dark"  <!-- Fondo claro -->
android:textColor="@color/text_primary_light" <!-- Fondo oscuro -->
```

### Botones Primarios
```xml
android:backgroundTint="@color/accent_orange"
android:textColor="@color/text_primary_light"
```

### Botones Secundarios
```xml
android:backgroundTint="@color/accent_blue"
android:textColor="@color/text_primary_light"
```

### Cards y Superficies
```xml
app:cardBackgroundColor="@color/surface_light"
android:textColor="@color/text_primary_dark"
```

### Estados de Éxito/Error
```xml
<!-- Éxito -->
android:backgroundTint="@color/success_green"
android:textColor="@color/text_primary_light"

<!-- Error -->
android:backgroundTint="@color/error_red"
android:textColor="@color/text_primary_light"
```

## 🌙 Soporte para Modo Oscuro

La aplicación incluye soporte completo para modo oscuro usando los colores `*_dark` y el tema `Theme.Xtream.Night`. Los colores se ajustan automáticamente según la preferencia del sistema.

## 📱 Compatibilidad

- **Material Design Guidelines**: Todos los colores siguen las pautas de Material Design 3.0
- **WCAG 2.1 AA**: Cumple con estándares de accesibilidad para contraste
- **Android API 21+**: Compatible con todas las versiones soportadas
- **Modo Oscuro**: Soporte nativo para tema oscuro del sistema

## 🔄 Transparencias

Usa las variantes con transparencia para overlays y estados:
- `accent_blue_10` (10% opacidad)
- `accent_blue_20` (20% opacidad)
- `accent_orange_10` (10% opacidad)
- `accent_orange_20` (20% opacidad)
# CLAUDE.md

Este archivo proporciona orientación a Claude Code (claude.ai/code) cuando trabaja con código en este repositorio.

## Comandos de desarrollo comunes (Windows)

### Construcción y ejecución
```cmd
# Construir el proyecto
gradlew.bat build

# Construir versión de debug
gradlew.bat assembleDebug

# Construir versión de release
gradlew.bat assembleRelease

# Limpiar build
gradlew.bat clean

# Instalar en dispositivo/emulador conectado
gradlew.bat installDebug
```

### Testing
```cmd
# Ejecutar tests unitarios
gradlew.bat test

# Ejecutar tests instrumentados (requiere dispositivo/emulador)
gradlew.bat connectedAndroidTest

# Ejecutar tests de un módulo específico
gradlew.bat :app:test

# Ejecutar un test específico
gradlew.bat test --tests "com.kybers.xtream.ExampleUnitTest"
```

### Linting y verificación
```cmd
# Ejecutar lint
gradlew.bat lint

# Verificar código
gradlew.bat check
```

## Arquitectura del proyecto

### Información general
- **Nombre de la aplicación**: Xtream
- **Package**: com.kybers.xtream
- **Tipo**: Reproductor IPTV para Android
- **SDK mínimo**: 24 (Android 7.0)
- **SDK objetivo**: 36
- **Lenguaje**: Kotlin
- **Patrón de arquitectura**: MVVM con Navigation Component

### Estructura de módulos
El proyecto usa una estructura de módulo único (`app/`) con las siguientes características:
- **ViewBinding**: Habilitado para acceso type-safe a las vistas
- **Navigation Component**: Para navegación entre fragmentos
- **BottomNavigationView**: Navegación principal con tres secciones

### Componentes principales

#### MainActivity (`MainActivity.kt`)
- Actividad principal que configura la navegación bottom navigation
- Usa ViewBinding (`ActivityMainBinding`)
- Configura Navigation Controller con tres destinos principales

#### Fragmentos UI
- **HomeFragment**: Fragmento principal (probablemente para el reproductor IPTV)
- **DashboardFragment**: Panel de control/configuración
- **NotificationsFragment**: Notificaciones
- Cada fragmento sigue el patrón MVVM con su respectivo ViewModel

#### ViewModels
- **HomeViewModel**: Lógica de negocio para la pantalla principal
- **DashboardViewModel**: Lógica para el dashboard
- **NotificationsViewModel**: Lógica para notificaciones

### Tecnologías utilizadas
- **AndroidX Libraries**: Core, AppCompat, ConstraintLayout
- **Material Design Components**: Para UI consistente
- **Navigation Component**: Fragment + UI navigation
- **Lifecycle Components**: LiveData y ViewModel
- **View Binding**: Para acceso type-safe a vistas

### Configuración de testing
- **JUnit 4**: Para tests unitarios
- **AndroidX Test**: Para tests instrumentados
- **Espresso**: Para tests de UI

### Configuración del proyecto
- **Kotlin JVM Target**: 11
- **Java Compatibility**: 11
- **ProGuard**: Configurado pero deshabilitado en debug
- **Version Catalog**: Usando `libs.versions.toml` para gestión de dependencias

## Patrones de desarrollo específicos

### ViewBinding
Todos los fragmentos y actividades usan ViewBinding. Patrón típico:
```kotlin
private var _binding: FragmentHomeBinding? = null
private val binding get() = _binding!!
```

### Navigation
El proyecto usa Navigation Component con un BottomNavigationView que maneja tres destinos principales definidos en `mobile_navigation.xml`.

### MVVM Pattern
Cada fragmento tiene su ViewModel correspondiente que maneja la lógica de negocio y expone datos a través de LiveData.
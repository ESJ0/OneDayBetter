# OneDayBetter
Descripción del Proyecto
OneDayBetter es una aplicación móvil Android desarrollada en Kotlin con Jetpack Compose que permite a los usuarios crear, seguir y mantener hábitos saludables a largo plazo. La aplicación está diseñada para transformar pequeños actos diarios en importantes triunfos personales mediante un sistema de seguimiento visual, motivación y organización.

Navegación
El proyecto implementa Type-Safe Navigation utilizando las últimas APIs de Jetpack Navigation Compose:

Librería: androidx.navigation:navigation-compose:2.8.3
Serialización: kotlinx-serialization-json:1.6.3
Patrón: Destinos serializables con @Serializable

Librerías Externas
1. Coil - Carga de Imágenes

Versión: 2.6.0
Dependencia: io.coil-kt:coil-compose:2.6.0
Uso en el proyecto: Preparado para cargar imágenes de perfil, iconos personalizados y recursos visuales dinámicos
Características:

Optimizado para Jetpack Compose
Caché automático de imágenes
Soporte para placeholders y transformaciones



2. Jetpack Compose

BOM: androidx.compose:compose-bom:2024.09.00
Componentes utilizados:

Material3 Design System
Navigation Compose
UI Tooling (para previews)
Activity Compose

Servicios Externos (Planificados)
Autenticación
Firebase Authentication (a implementar):

Login con email/contraseña
Autenticación con Google
Autenticación con Apple ID
Gestión de sesiones

Base de Datos
Firebase Firestore (a implementar):

Sincronización de hábitos entre dispositivos
Backup automático en la nube
Actualización en tiempo real

Notificaciones
Firebase Cloud Messaging (a implementar):

Recordatorios automáticos programables
Notificaciones de motivación
Alertas de metas próximas a vencer

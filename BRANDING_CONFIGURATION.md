# Configuración Dinámica de Branding para Autofirma

Este documento explica cómo personalizar los logos y referencias de la aplicación usando variables de entorno, permitiendo adaptar el branding a cualquier organización sin modificar el código fuente.

## Resumen de Cambios

Se ha implementado un sistema de configuración dinámico que permite personalizar:

- **Logos**: Se cargan desde la carpeta `media/logos/` si existen, sino se usan los por defecto
- **Nombres de organización**: Configurables mediante variables de entorno
- **URLs y referencias**: Personalizables sin modificar el código

## Archivos Creados

1. **`.env.example`** (NUEVO - en la raíz del proyecto)
   - Archivo de ejemplo con todas las variables de entorno disponibles
   - Documentación completa del sistema de configuración
   - Ejemplos de uso para diferentes plataformas

2. **`afirma-simple/src/main/java/es/gob/afirma/standalone/config/BrandingConfig.java`** (NUEVO)
   - Clase singleton que lee variables de entorno
   - Proporciona valores de configuración con fallback a valores por defecto

3. **`media/logos/README.md`** (NUEVO)
   - Documentación específica sobre la personalización de logos

## Archivos Modificados

1. **`afirma-simple/src/main/java/es/gob/afirma/standalone/ui/ImageLoader.java`**
   - Modificado para buscar logos primero en `media/logos/`, luego en recursos

2. **`afirma-simple/src/main/java/es/gob/afirma/standalone/DesktopUtil.java`**
   - Modificado para usar `BrandingConfig` y `ImageLoader` para cargar logos personalizados

3. **`afirma-simple/src/main/java/es/gob/afirma/standalone/ui/AboutDialog.java`**
   - Modificado para usar `BrandingConfig` para el texto de copyright

4. **`afirma-simple/src/main/java/es/gob/afirma/standalone/updater/Updater.java`**
   - Modificado para usar `BrandingConfig` para la URL de actualizaciones

## Variables de Entorno

**Para ver todas las variables de entorno disponibles y ejemplos de configuración, consulta el archivo `.env.example` en la raíz del proyecto.**

Este archivo contiene:
- Todas las variables de entorno disponibles
- Descripción de cada variable
- Valores por defecto
- Ejemplos de configuración para Windows, Linux y macOS
- Instrucciones de uso detalladas

### Ejemplo de Configuración

```bash
# Organización
export AUTOFIRMA_ORGANIZATION_NAME="Mi Organización"
export AUTOFIRMA_ORGANIZATION_URL="https://www.mi-organizacion.com"
export AUTOFIRMA_ORGANIZATION_EMAIL="soporte@mi-organizacion.com"
export AUTOFIRMA_COPYRIGHT="Mi Organización"

# URLs
export AUTOFIRMA_UPDATE_SITE_URL="https://www.mi-organizacion.com/actualizaciones"

# Logos
export AUTOFIRMA_LOGO_CLIENTE="logo_organizacion_256.png"
export AUTOFIRMA_LOGO_ORGANIZATION="logo_organizacion_256.png"

# Directorio de media (opcional, por defecto: media/logos)
export AUTOFIRMA_MEDIA_DIR="/ruta/completa/a/media/logos"
```

## Estructura de Directorios

```
firma/
├── .env.example
├── media/
│   └── logos/
│       ├── README.md
│       ├── logo_organizacion_16.png
│       ├── logo_organizacion_24.png
│       ├── logo_organizacion_32.png
│       ├── logo_organizacion_48.png
│       ├── logo_organizacion_128.png
│       ├── logo_organizacion_256.png
│       └── logo_organizacion_512.png
└── ...
```

## Cómo Usar

### 1. Consultar la Documentación

Revisa el archivo `.env.example` en la raíz del proyecto para ver todas las opciones de configuración disponibles.

### 2. Preparar los Logos

Coloca los logos personalizados en `media/logos/` con los nombres especificados en las variables de entorno.

### 3. Configurar Variables de Entorno

Configura las variables de entorno antes de ejecutar la aplicación. Puedes usar el archivo `.env.example` como referencia:

**Windows (PowerShell):**
```powershell
$env:AUTOFIRMA_ORGANIZATION_NAME="Mi Organización"
$env:AUTOFIRMA_LOGO_CLIENTE="logo_organizacion_256.png"
```

**Windows (CMD):**
```cmd
set AUTOFIRMA_ORGANIZATION_NAME=Mi Organización
set AUTOFIRMA_LOGO_CLIENTE=logo_organizacion_256.png
```

**Linux/macOS:**
```bash
export AUTOFIRMA_ORGANIZATION_NAME="Mi Organización"
export AUTOFIRMA_LOGO_CLIENTE="logo_organizacion_256.png"
```

### 3. Compilar y Ejecutar

```bash
# Compilar
mvn install -Denv=install -DskipTests

# Ejecutar con variables de entorno configuradas
java -jar afirma-simple/target/autofirma.jar
```

## Comportamiento

- **Prioridad de carga de logos**: 
  1. Se busca primero en `media/logos/` (si `AUTOFIRMA_MEDIA_DIR` está configurado, se usa esa ruta)
  2. Si no se encuentra, se usa el logo por defecto de los recursos

- **Valores por defecto**: Si no se configuran variables de entorno, se usan valores genéricos que no están relacionados con ninguna institución específica

- **Fallback**: El sistema siempre tiene un fallback a los recursos por defecto para garantizar que la aplicación funcione

## Ventajas

✅ **Sin modificar código**: Todo se configura mediante variables de entorno
✅ **Fácil personalización**: Solo necesitas colocar logos y configurar variables
✅ **Mantenible**: Los cambios futuros del código no afectan la personalización
✅ **Flexible**: Funciona para cualquier organización

## Notas

- Los logos deben estar en formato PNG
- Se recomienda usar transparencia para mejor integración visual
- Los tamaños recomendados son: 16x16, 24x24, 32x32, 48x48, 128x128, 256x256, 512x512 píxeles
- Las variables de entorno deben estar configuradas cada vez que se ejecuta la aplicación (a menos que se configuren permanentemente en el sistema)

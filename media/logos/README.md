# Personalización de Logos

Este directorio contiene los logos personalizables de la aplicación Autofirma.

## Configuración mediante Variables de Entorno

Para personalizar los logos y referencias de la aplicación, consulta el archivo `.env.example` en la raíz del proyecto, que contiene todas las variables de entorno disponibles para la configuración completa del sistema de branding.

### Variables de Entorno Relacionadas con Logos

- **`AUTOFIRMA_LOGO_CLIENTE`**: Nombre del archivo del logo del cliente (por defecto: "logo_cliente_256.png")
- **`AUTOFIRMA_LOGO_ORGANIZATION`**: Nombre del archivo del logo de la organización (por defecto: "logo_cliente_256.png")
- **`AUTOFIRMA_MEDIA_DIR`**: Ruta completa del directorio donde se almacenan los logos personalizados (por defecto: "media/logos" relativo al directorio de instalación)

**Para ver todas las variables de entorno disponibles (organización, URLs, etc.), consulta el archivo `.env.example` en la raíz del proyecto.**

## Cómo Usar

### 1. Colocar Logos Personalizados

Coloca tus logos personalizados en este directorio (`media/logos/`). Los nombres de archivo deben coincidir con los especificados en las variables de entorno.

**Formatos recomendados:**
- PNG con transparencia
- Tamaños recomendados: 16x16, 24x24, 32x32, 48x48, 128x128, 256x256, 512x512 píxeles

**Nombres de archivo sugeridos:**
- `logo_cliente_16.png`
- `logo_cliente_24.png`
- `logo_cliente_32.png`
- `logo_cliente_48.png`
- `logo_cliente_128.png`
- `logo_cliente_256.png`
- `logo_cliente_512.png`

### 2. Configurar Variables de Entorno

Consulta el archivo `.env.example` en la raíz del proyecto para ver todas las variables de entorno disponibles y ejemplos de configuración para diferentes plataformas.

**Ejemplo rápido para logos:**
```bash
# Windows (PowerShell)
$env:AUTOFIRMA_LOGO_CLIENTE="logo_organizacion_256.png"
$env:AUTOFIRMA_MEDIA_DIR="D:\ruta\a\media\logos"

# Linux/macOS
export AUTOFIRMA_LOGO_CLIENTE="logo_organizacion_256.png"
export AUTOFIRMA_MEDIA_DIR="/ruta/a/media/logos"
```

### 3. Ejecutar la Aplicación

Una vez configuradas las variables de entorno, ejecuta la aplicación normalmente:

```bash
java -jar afirma-simple/target/autofirma.jar
```

## Ejemplo de Configuración

Para ver un ejemplo completo de configuración, consulta el archivo `.env.example` en la raíz del proyecto, que contiene todas las variables de entorno con valores de ejemplo genéricos.

## Notas Importantes

1. **Prioridad de carga**: Los logos se buscan primero en el directorio `media/logos/` especificado. Si no se encuentran, se usan los logos por defecto incluidos en los recursos de la aplicación.

2. **Rutas absolutas vs relativas**: Se recomienda usar rutas absolutas para `AUTOFIRMA_MEDIA_DIR` para evitar problemas de localización.

3. **Formato de imágenes**: Se recomienda usar PNG con transparencia para mejor compatibilidad visual.

4. **Persistencia**: Las variables de entorno deben estar configuradas cada vez que se ejecuta la aplicación, a menos que se configuren permanentemente en el sistema operativo.

## Solución de Problemas

- **Los logos no se cargan**: Verifica que:
  - Los archivos existan en el directorio especificado
  - Los nombres de archivo coincidan exactamente con los especificados en las variables de entorno
  - Los permisos de lectura estén correctos
  - La ruta en `AUTOFIRMA_MEDIA_DIR` sea correcta

- **Se siguen mostrando los logos por defecto**: Esto es normal si no se encuentran los logos personalizados. La aplicación usará los logos por defecto como fallback.

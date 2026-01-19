# Guía para Creación de Instalador de Producción

Esta guía describe el proceso completo para crear un paquete de instalación de Autofirma en formato autoextraible usando WinRAR.

## Paso 1: Compilar el Proyecto con Maven

### 1.1 Verificar Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java JDK**: Necesario para compilar el proyecto
  - Verificar instalación: Abrir una terminal y ejecutar `java -version`
  - Si no está instalado, descargar e instalar desde [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://openjdk.org/)

- **Apache Maven**: Herramienta de construcción del proyecto
  - Verificar instalación: Abrir una terminal y ejecutar `mvn -version`
  - Si no está instalado, descargar e instalar desde [Apache Maven](https://maven.apache.org/download.cgi)
  - Asegurarse de que Maven esté en el PATH del sistema

### 1.2 Ejecutar el Script de Compilación

1. **Navegar a la carpeta release**:
   - Abrir el explorador de archivos y navegar a la carpeta `release` del proyecto

2. **Ejecutar el script build.bat**:
   - Hacer doble clic en el archivo `build.bat`
   - O abrir una terminal en la carpeta `release` y ejecutar: `build.bat`

3. **Verificar la compilación**:
   - El script ejecutará: `mvn clean install -Denv=install -DskipTests`
   - Esperar a que termine el proceso de compilación
   - Verificar que no haya errores en la salida
   - El archivo `autofirma.jar` debe generarse en `afirma-simple\target\autofirma.jar`

4. **Si hay errores**:
   - Revisar que Java y Maven estén correctamente instalados y en el PATH
   - Verificar que todas las dependencias del proyecto estén disponibles
   - Revisar los mensajes de error en la consola

## Paso 2: Preparar Instalador de Autofirma

### 2.1 Crear Carpeta de Trabajo

- La carpeta `release` ya debe existir y ser el directorio de trabajo para todos los archivos del instalador
- El archivo de configuración `launch4j_config.xml` debe estar en la carpeta `release`

### 2.2 Convertir JAR a EXE con Launch4j

1. Abrir **Launch4j**
2. Configurar los siguientes parámetros:
   - **JAR**: `afirma-simple\target\autofirma.jar` (ruta relativa desde la raíz del proyecto)
   - **Output file**: `release\autofirma.exe` (ruta relativa desde la raíz del proyecto)
   - **Header type**: GUI (ya configurado)
3. **Agregar icono**: 
   - En la sección de icono, seleccionar un archivo `.ico` para el ejecutable
4. **Cargar configuración guardada**:
   - Al lado del botón de play (▶), hacer clic en el icono de la **tuerca (⚙)**
   - Seleccionar `release\launch4j_config.xml` para cargar la configuración guardada
   - **IMPORTANTE**: Antes de usar el archivo de configuración, abrirlo y reemplazar todas las instancias de `[Definir_ruta]` con la ruta completa real del proyecto (por ejemplo: `D:\develop\firma`)
5. **Generar el ejecutable**:
   - Hacer clic en el botón **Wrapper** o en el icono de **play (▶)**
   - Verificar que se haya generado correctamente `autofirma.exe` en la carpeta `release`

### 2.3 Agregar Logo Institucional (Opcional)

Si deseas personalizar la aplicación con un logo institucional:

1. **Preparar el logo**:
   - Crear la estructura de carpetas `release\media\logo\` si no existe
   - Copiar el archivo de logo (formato `.png` o `.jpg`) a `release\media\logo\logo.png`
   - El logo será cargado automáticamente por la aplicación cuando se ejecute el EXE
   - **Nota**: Si no se proporciona un logo, la aplicación utilizará el logo por defecto incluido en los recursos

2. **Verificar la ubicación**:
   - Asegurarse de que el logo esté en: `release\media\logo\logo.png`
   - La aplicación buscará el logo en esta ubicación relativa al directorio donde se ejecute el EXE

### 2.4 Agregar Certificado CA del SAT

Es necesario incluir el archivo de certificado CA del SAT:

1. **Preparar el certificado CA**:
   - Crear la estructura de carpetas `release\media\cert\` si no existe
   - Copiar el archivo `CA.pem` a `release\media\cert\CA.pem`
   - Este archivo contiene la cadena donde se unifican todos los certificados del SAT
   - **IMPORTANTE**: Este archivo es necesario para la validación de certificados del SAT

2. **Verificar la ubicación**:
   - Asegurarse de que el certificado esté en: `release\media\cert\CA.pem`
   - La aplicación buscará el certificado en esta ubicación relativa al directorio donde se ejecute el EXE

### 2.5 Agregar Carpeta bin con OpenSSL

La carpeta `bin` con los archivos de OpenSSL se agregará mediante medios externos al momento de hacer build:

1. **Preparar la carpeta bin**:
   - Crear la estructura de carpetas `release\bin\` si no existe
   - La carpeta `bin` debe contener los siguientes archivos:
     - `openssl.exe` - Ejecutable de OpenSSL
     - `libssl-3-x64.dll` - Biblioteca SSL de OpenSSL
     - `libcrypto-3-x64.dll` - Biblioteca criptográfica de OpenSSL
   - **IMPORTANTE**: Estos archivos se agregarán mediante medios externos durante el proceso de build
   - No se requiere instalación completa de OpenSSL, solo estos archivos portables

2. **Verificar la ubicación**:
   - Asegurarse de que la carpeta `bin` esté en: `release\bin\`
   - Los archivos de OpenSSL deben estar disponibles en esta ubicación relativa al directorio donde se ejecute el EXE

### 2.6 Crear Archivo Autoextraible con WinRAR

1. **Seleccionar archivos para empaquetar**:
   - Navegar a la carpeta `release`
   - Seleccionar `autofirma.exe`, la carpeta `media` (si existe) y la carpeta `bin` (si existe)
   - Hacer clic derecho sobre los archivos seleccionados

2. **Iniciar compresión**:
   - En el menú contextual de WinRAR, seleccionar **"Añadir al archivo..."**
   - **IMPORTANTE**: Asegurarse de incluir:
     - La carpeta `media` junto con el `autofirma.exe` para que el logo y el certificado CA estén disponibles
     - La carpeta `bin` con los archivos de OpenSSL para que la aplicación funcione correctamente

3. **Configurar opciones de compresión**:
   - En la ventana de opciones, marcar la casilla: **"Crear un archivo autoextraible"**
   - Cambiar el nombre del archivo a: `autofirma-installer.exe`

4. **Configurar opciones avanzadas**:
   - Ir a la pestaña **"Avanzado"**
   - Hacer clic en el botón **"Autoextraible"**

5. **Configurar carpeta de extracción**:
   - En la sección **"Carpeta de extracción"**, escribir: `Autofirma`
   - Esta será la carpeta donde se extraerá el contenido al ejecutar el autoextraible

6. **Crear acceso directo al escritorio**:
   - En la misma ventana de **"Avanzado"** → **"Autoextraible"**
   - En la sección de comandos o scripts, agregar:
     ```
     D, autofirma.exe
     ```
   - Este comando creará un acceso directo en el escritorio al ejecutable `autofirma.exe`

7. **Finalizar creación**:
   - Hacer clic en **"Aceptar"** en todas las ventanas hasta que se cree el archivo autoextraible
   - El resultado será un archivo ejecutable autoextraible: `autofirma-installer.exe`
   - **IMPORTANTE**: Asegurarse de que el archivo se guarde en la carpeta `release` y tenga exactamente el nombre `autofirma-installer.exe`

## Paso 3: Instrucciones de Uso del Instalador

### Instalación de Autofirma

Al ejecutar el autoextraible generado (`autofirma-installer.exe`):

1. Se iniciará automáticamente el proceso de extracción
2. **Solo es necesario**:
   - Hacer clic en **"Aceptar"** cuando se solicite
   - El contenido se extraerá en la carpeta `Autofirma` según se configuró
   - La carpeta `bin` con los archivos de OpenSSL se extraerá junto con la aplicación
   - La carpeta `media` con el logo y el certificado CA también se extraerá
3. **Verificar el acceso directo**:
   - Comprobar que se haya creado correctamente el acceso directo en el escritorio
   - El acceso directo debería apuntar a `autofirma.exe` dentro de la carpeta `Autofirma`

## Resumen de Archivos Generados

Al finalizar el proceso, deberías tener:

- `release\autofirma.exe` - Ejecutable generado con Launch4j
- `release\media\logo\logo.png` - Logo institucional (opcional)
- `release\media\cert\CA.pem` - Certificado CA del SAT (cadena unificada de certificados)
- `release\bin\openssl.exe` - Ejecutable de OpenSSL (agregado mediante medios externos)
- `release\bin\libssl-3-x64.dll` - Biblioteca SSL de OpenSSL (agregado mediante medios externos)
- `release\bin\libcrypto-3-x64.dll` - Biblioteca criptográfica de OpenSSL (agregado mediante medios externos)
- `release\autofirma-installer.exe` - **Archivo autoextraible final creado con WinRAR** (incluye `autofirma.exe`, carpeta `media` y carpeta `bin`)
- `release\launch4j_config.xml` - Archivo de configuración de Launch4j

## Notas Importantes

- **CRÍTICO**: Antes de usar el archivo de configuración de Launch4j, reemplazar todas las instancias de `[Definir_ruta]` con la ruta completa real del proyecto
- Verificar que el archivo `autofirma.jar` exista en `afirma-simple\target\`
- El icono `.ico` debe estar disponible antes de ejecutar Launch4j
- La carpeta `release` es el directorio de trabajo para todos los archivos del instalador
- Si se incluye un logo personalizado, asegurarse de empaquetarlo junto con el EXE en el autoextraible
- El logo debe estar en formato `.png` o `.jpg` y ubicado en `release\media\logo\logo.png`
- **IMPORTANTE**: El archivo `CA.pem` debe estar en `release\media\cert\CA.pem` y contiene la cadena unificada de certificados del SAT
- La carpeta `bin` con los archivos de OpenSSL (`openssl.exe`, `libssl-3-x64.dll`, `libcrypto-3-x64.dll`) se agregará mediante medios externos durante el proceso de build
- Asegurarse de incluir la carpeta `bin` en el autoextraible junto con `autofirma.exe` y la carpeta `media`
- No se requiere instalación completa de OpenSSL, solo los archivos portables en la carpeta `bin`
- El archivo final `autofirma-installer.exe` es el único instalador necesario
- Probar el autoextraible final en un sistema limpio antes de distribuirlo

# Guía para Creación de ZIP de Producción

Esta guía describe el proceso completo para crear un paquete de instalación de Autofirma en formato ZIP autoextraible.

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
- Los archivos de configuración (`afirma_installer.SED` y `launch4j_config.xml`) deben estar en la carpeta `release`

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

### 2.4 Crear Archivo Autoextraible con WinRAR

1. **Seleccionar archivos para empaquetar**:
   - Navegar a la carpeta `release`
   - Seleccionar tanto `autofirma.exe` como la carpeta `media` (si existe)
   - Hacer clic derecho sobre los archivos seleccionados

2. **Iniciar compresión**:
   - En el menú contextual de WinRAR, seleccionar **"Añadir al archivo..."**
   - **IMPORTANTE**: Asegurarse de incluir la carpeta `media` junto con el `autofirma.exe` para que el logo esté disponible cuando se extraiga el autoextraible

3. **Configurar opciones de compresión**:
   - En la ventana de opciones, marcar la casilla: **"Crear un archivo autoextraible"**
   - Cambiar el nombre del archivo a: `autofirma-installer.exe` (debe coincidir con el nombre esperado en el archivo SED)

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

## Paso 3: Agregar Instalador de OpenSSL

1. **Preparar el instalador de OpenSSL**:
   - Copiar el instalador de OpenSSL al directorio de trabajo (`release`)
   - Renombrar el archivo como: `openssl-installer.exe`
   - Asegurarse de que el archivo esté en la misma carpeta que `autofirma.exe`

## Paso 4: Modificar afirma_installer.SED

1. **Abrir el archivo** `release\afirma_installer.SED` en un editor de texto

2. **Modificar las rutas según el sistema**:
   - **IMPORTANTE**: Reemplazar todas las instancias de `[Definir_ruta]` con la ruta completa real donde se encuentra el proyecto
   - Por ejemplo, si el proyecto está en `D:\develop\firma`, reemplazar `[Definir_ruta]` con `D:\develop\firma`
   - Asegurarse de que las rutas apunten correctamente a:
     - `openssl-installer.exe` (renombrado anteriormente)
     - `autofirma-installer.exe` (el autoextraible creado con WinRAR)

3. **Verificar las siguientes líneas** (después de reemplazar `[Definir_ruta]`):
   ```
   TargetName=D:\develop\firma\autofirma_installer.EXE
   AppLaunched=openssl-installer.exe
   PostInstallCmd=autofirma-installer.exe
   FILE0="openssl-installer.exe"
   FILE1="autofirma-installer.exe"
   SourceFiles0=D:\develop\firma\release\
   ```

4. **Guardar los cambios**

## Paso 5: Generar el Wizard con IExpress

1. **Abrir IExpress**:
   - Presionar **Win + R** para abrir el cuadro de diálogo "Ejecutar"
   - Escribir `iexpress` y presionar Enter
   - Se abrirá el asistente de IExpress

2. **Cargar el archivo SED**:
   - En la primera pantalla, seleccionar la opción: **"Create a Self Extraction Directive file"** o **"Create new Self Extraction Directive file"**
   - En la siguiente pantalla, seleccionar: **"Extract files and run an installation command"**
   - Continuar hasta llegar a la opción de cargar un archivo SED existente
   - Alternativamente, desde el menú **File** → **Open**, cargar el archivo `release\afirma_installer.SED` modificado

3. **Generar el instalador**:
   - Si cargaste el archivo SED, revisar todas las configuraciones
   - Hacer clic en **"Next"** hasta llegar al final
   - En la última pantalla, hacer clic en **"Create Self Extraction Directive file"** para guardar el SED o **"Build"** para generar directamente el instalador
   - El resultado será un archivo ejecutable `autofirma_installer.EXE` en la ubicación especificada en `TargetName`

## Paso 6: Instrucciones de Uso del Instalador

### Instalación de OpenSSL

Al ejecutar el instalador generado (`autofirma_installer.EXE`):

1. Se iniciará automáticamente el instalador de OpenSSL
2. **Solo es necesario**:
   - Hacer clic en **"Aceptar"** en las ventanas del instalador
   - **Quitar la donación** si aparece alguna opción relacionada
   - Seguir el proceso de instalación estándar

### Instalación de Autofirma

1. Después de instalar OpenSSL, se ejecutará automáticamente el autoextraible de Autofirma
2. **Solo es necesario**:
   - Hacer clic en **"Aceptar"** cuando se solicite
   - El archivo se extraerá en la carpeta `Autofirma` según se configuró
3. **Verificar el acceso directo**:
   - Comprobar que se haya creado correctamente el acceso directo en el escritorio
   - El acceso directo debería apuntar a `autofirma.exe`

## Resumen de Archivos Generados

Al finalizar el proceso, deberías tener:

- `release\autofirma.exe` - Ejecutable generado con Launch4j
- `release\media\logo\logo.png` - Logo institucional (opcional)
- `release\openssl-installer.exe` - Instalador de OpenSSL renombrado
- `release\autofirma-installer.exe` - Archivo autoextraible creado con WinRAR (incluye `autofirma.exe` y carpeta `media` si existe)
- `autofirma_installer.EXE` - Instalador final generado con IExpress (ubicación según `TargetName` en el SED)
- `release\afirma_installer.SED` - Archivo de configuración de IExpress
- `release\launch4j_config.xml` - Archivo de configuración de Launch4j

## Notas Importantes

- **CRÍTICO**: Antes de usar los archivos de configuración, reemplazar todas las instancias de `[Definir_ruta]` con la ruta completa real del proyecto
- Los archivos con "_prod" en el nombre (como `afirma_installer_prod.SED`) contienen rutas personales y están ignorados por Git
- Asegurarse de que todas las rutas en `afirma_installer.SED` sean absolutas y correctas después de reemplazar `[Definir_ruta]`
- Verificar que el archivo `autofirma.jar` exista en `afirma-simple\target\`
- El icono `.ico` debe estar disponible antes de ejecutar Launch4j
- La carpeta `release` es el directorio de trabajo para todos los archivos del instalador
- Si se incluye un logo personalizado, asegurarse de empaquetarlo junto con el EXE en el autoextraible
- El logo debe estar en formato `.png` o `.jpg` y ubicado en `release\media\logo\logo.png`
- Probar el instalador final en un sistema limpio antes de distribuirlo

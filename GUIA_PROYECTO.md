# Guía del Proyecto Autofirma

## 📋 Versión General

**Autofirma** es una herramienta de firma electrónica desarrollada por el Gobierno de España como parte de la Suite @firma. Es una aplicación multiplataforma que permite realizar firmas electrónicas avanzadas en documentos digitales.

### Características Principales

- **Firma de múltiples formatos**: PDF (PAdES), XML (XAdES), documentos de oficina (ODF, OOXML), FacturaE, y más
- **Múltiples modos de ejecución**: 
  - Aplicación de escritorio (JAR ejecutable)
  - Integración web mediante JavaScript
  - Aplicación móvil
- **Soporte para diferentes almacenes de claves**: DNIe, tarjetas inteligentes, almacenes del sistema operativo, Mozilla Firefox
- **Firma trifásica**: Permite firmar documentos grandes mediante un servidor intermedio
- **Firma masiva**: Capacidad de firmar múltiples documentos en lote
- **Validación de firmas**: Verificación de integridad de documentos firmados

### Propósito

Autofirma proporciona a las Administraciones Públicas y ciudadanos una herramienta libre y gratuita para implementar la autenticación y firma electrónica avanzada de forma rápida y efectiva, cumpliendo con los estándares europeos de firma electrónica.

---

## 🔧 Versión Técnica

### Arquitectura del Proyecto

El proyecto está estructurado como un **proyecto Maven multi-módulo** con más de 50 módulos organizados por funcionalidad:

#### Módulos Core (Núcleo)
- **`afirma-core`**: Componentes principales y funcionalidades base
- **`afirma-core-keystores`**: Gestión de almacenes de claves de usuario
- **`afirma-core-prefs`**: Gestión de preferencias y configuración
- **`afirma-core-massive`**: Operaciones masivas de firma

#### Módulos Criptográficos
- **`afirma-crypto-core-pkcs7`**: Estructuras PKCS#7 básicas (CAdES, PAdES)
- **`afirma-crypto-core-xml`**: Estructuras XML básicas (XAdES, ODF, OOXML)
- **`afirma-crypto-cades`**: Generación de firmas CAdES
- **`afirma-crypto-cades-multi`**: Cofirmas y contrafirmas CAdES
- **`afirma-crypto-pdf`**: Generación de firmas PAdES (PDF)
- **`afirma-crypto-xades`**: Generación de firmas XAdES, ASiC-XAdES y FacturaE
- **`afirma-crypto-odf`**: Firma de documentos ODF (OpenDocument)
- **`afirma-crypto-ooxml`**: Firma de documentos OOXML (Office Open XML)
- **`afirma-crypto-xmlsignature`**: Firmas XMLdSig
- **`afirma-crypto-cms`**: Firmas CMS
- **`afirma-crypto-validation`**: Verificación de integridad de firmas

#### Módulos Cliente Trifásico
- **`afirma-crypto-cadestri-client`**: Cliente para firmas trifásicas CAdES
- **`afirma-crypto-padestri-client`**: Cliente para firmas trifásicas PAdES
- **`afirma-crypto-xadestri-client`**: Cliente para firmas trifásicas XAdES y FacturaE
- **`afirma-crypto-batch-client`**: Cliente para operaciones de firma de lote

#### Módulos de Almacenes de Claves
- **`afirma-keystores-filters`**: Filtros de certificados
- **`afirma-keystores-mozilla`**: Gestión del almacén de claves de Mozilla Firefox
- **`afirma-keystores-jmulticard-ui`**: Interfaz para tarjetas inteligentes

#### Módulos de Interfaz de Usuario
- **`afirma-ui-core-jse`**: Interfaces gráficas genéricas
- **`afirma-ui-core-jse-keystores`**: Diálogo de selección de certificados
- **`afirma-ui-utils`**: Utilidades de interfaz

#### Módulos de Aplicación
- **`afirma-simple`**: Aplicación principal Autofirma (JAR ejecutable)
- **`afirma-ui-simple-configurator`**: Configurador ejecutado durante la instalación
- **`afirma-simple-plugins`**: Sistema base de plugins
- **`afirma-simple-plugin-hash`**: Plugin para generación y validación de hashes
- **`afirma-simple-plugin-validatecerts`**: Plugin para validación de firmas

#### Módulos de Servidor
- **`afirma-server-triphase-signer`**: Servicio WAR para generación de firmas trifásicas
- **`afirma-server-triphase-signer-core`**: Funcionalidad básica de firma trifásica
- **`afirma-server-triphase-signer-cache`**: Interfaz de caché del servidor trifásico
- **`afirma-server-triphase-signer-document`**: Interfaz de guardado/recuperación de documentos
- **`afirma-signature-retriever`**: Servicio WAR de recuperación del servidor intermedio
- **`afirma-signature-storage`**: Servicio WAR de guardado del servidor intermedio

### Tecnologías Utilizadas

- **Lenguaje**: Java 1.7/1.8
- **Build Tool**: Apache Maven 3.x
- **Bibliotecas Criptográficas**:
  - SpongyCastle (fork de BouncyCastle para Android/JSE)
  - Apache Santuario XML Security
  - jXAdES
- **Procesamiento de PDF**: Apache PDFBox 2.0.25
- **Tarjetas Inteligentes**: jMulticard
- **Interfaz Gráfica**: Java Swing
- **Comunicación Web**: Java WebSocket
- **Gestión de Proxy**: Proxy Vole
- **Acceso Nativo**: JNA (Java Native Access)

### Flujo de Firma

1. **Selección de documento**: El usuario selecciona el archivo a firmar
2. **Selección de certificado**: Se muestra un diálogo para elegir el certificado del almacén de claves
3. **Configuración de firma**: Se configuran parámetros (formato, política, etc.)
4. **Proceso de firma**:
   - **Firma local**: Se calcula el hash y se firma localmente
   - **Firma trifásica**: Se envía el hash al servidor, se firma en el servidor y se recupera la firma
5. **Guardado**: Se guarda el documento firmado

---

## 🛠️ Herramientas Necesarias

### Requisitos del Sistema

#### Software Base
1. **Java Development Kit (JDK)**
   - **Versión mínima**: JDK 1.8 (Java 8)
   - **Nota**: Aunque algunos módulos son compatibles con Java 1.7, JUnit requiere Java 1.8+
   - **Descarga**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://openjdk.org/)

2. **Apache Maven**
   - **Versión recomendada**: 3.6.0 o superior
   - **Descarga**: [Apache Maven](https://maven.apache.org/download.cgi)
   - **Configuración**: Asegúrate de que `JAVA_HOME` y `MAVEN_HOME` estén configurados en las variables de entorno

#### Herramientas Opcionales (para desarrollo avanzado)
- **IDE**: IntelliJ IDEA, Eclipse, o NetBeans
- **Git**: Para control de versiones
- **Servidor de aplicaciones**: Tomcat, Jetty (para desplegar servicios WAR)

### Verificación de Instalación

```bash
# Verificar Java
java -version
# Debe mostrar versión 1.8 o superior

# Verificar Maven
mvn -version
# Debe mostrar versión 3.6.0 o superior
```

---

## 🏗️ Cómo Hacer Build

El proyecto utiliza perfiles de Maven para diferentes tipos de construcción. A continuación se detallan los comandos según el objetivo:

### Build Básico (Módulos Core)

Para compilar solo los módulos básicos del proyecto (sin aplicaciones finales):

```bash
mvn clean install
```

Este comando:
- Limpia compilaciones anteriores (`clean`)
- Compila todos los módulos básicos
- Ejecuta los tests (puedes omitirlos con `-DskipTests`)
- Instala los artefactos en el repositorio local Maven

**Omitir tests**:
```bash
mvn clean install -DskipTests
```

### Build Completo (Aplicaciones y Servicios)

Para construir las aplicaciones ejecutables y servicios WAR, usa el perfil `env-install`:

```bash
mvn clean install -Denv=install
```

Este comando genera:
- **`afirma-simple/target/autofirma.jar`**: Aplicación Autofirma ejecutable
- **`afirma-ui-simple-configurator/target/AutofirmaConfigurador.jar`**: Configurador de instalación
- **`afirma-server-triphase-signer/target/*.war`**: Servicio de firmas trifásicas
- **`afirma-signature-retriever/target/*.war`**: Servicio de recuperación
- **`afirma-signature-storage/target/*.war`**: Servicio de almacenamiento

**Omitir tests**:
```bash
mvn clean install -Denv=install -DskipTests
```

### Build para Despliegue

Para generar artefactos listos para desplegar en un repositorio (incluye código fuente, JavaDoc y firma):

```bash
mvn clean deploy -Denv=deploy
```

**Nota**: Este comando requiere configuración adicional de GPG y repositorio Maven.

### Build de un Módulo Específico

Para compilar solo un módulo específico:

```bash
# Ejemplo: compilar solo afirma-core
cd afirma-core
mvn clean install

# O desde la raíz del proyecto
mvn clean install -pl afirma-core -am
```

Donde:
- `-pl`: especifica el módulo a compilar
- `-am`: también compila las dependencias necesarias

---

## 🚀 Cómo Correr en Desarrollo

### Ejecutar la Aplicación Principal

#### Opción 1: Desde el JAR Compilado

1. **Compilar el proyecto**:
```bash
mvn clean install -Denv=install -DskipTests
```

2. **Ejecutar Autofirma**:
```bash
java -jar afirma-simple/target/autofirma.jar
```

#### Opción 2: Desde el IDE

1. **Importar el proyecto** en tu IDE (IntelliJ IDEA, Eclipse, etc.)
2. **Localizar la clase principal**: `es.gob.afirma.standalone.SimpleAfirma`
3. **Configurar la clase principal** en la configuración de ejecución
4. **Ejecutar** desde el IDE

#### Opción 3: Ejecutar desde Maven (desarrollo)

Para ejecutar durante el desarrollo sin generar el JAR final:

```bash
# Compilar el módulo
cd afirma-simple
mvn clean compile

# Ejecutar con la clase principal
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
     es.gob.afirma.standalone.SimpleAfirma
```

### Ejecutar Tests

Para ejecutar los tests de un módulo específico:

```bash
# Tests de un módulo
cd afirma-core
mvn test

# Tests de todos los módulos
mvn test

# Tests con información detallada
mvn test -X
```

### Ejecutar Servicios (WAR)

Los servicios WAR necesitan un servidor de aplicaciones:

1. **Compilar los servicios**:
```bash
mvn clean install -Denv=install -DskipTests
```

2. **Desplegar en Tomcat**:
   - Copiar el WAR a `$TOMCAT_HOME/webapps/`
   - Iniciar Tomcat
   - Acceder a `http://localhost:8080/[nombre-del-war]`

### Configuración de Desarrollo

#### Variables de Entorno Recomendadas

```bash
# Java
export JAVA_HOME=/ruta/a/jdk1.8
export PATH=$JAVA_HOME/bin:$PATH

# Maven
export MAVEN_HOME=/ruta/a/maven
export PATH=$MAVEN_HOME/bin:$PATH

# Opciones JVM para desarrollo
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
```

#### Configuración de Maven

Crea o edita `~/.m2/settings.xml` para configurar repositorios y proxies si es necesario:

```xml
<settings>
  <proxies>
    <!-- Configurar proxy si es necesario -->
  </proxies>
  <mirrors>
    <!-- Configurar mirrors si es necesario -->
  </mirrors>
</settings>
```

### Debugging

Para ejecutar en modo debug:

```bash
# Compilar con información de debug
mvn clean compile -Dmaven.compiler.debug=true

# Ejecutar con debug remoto
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -jar afirma-simple/target/autofirma.jar
```

Luego conecta tu IDE al puerto 5005.

---

## 📝 Notas Adicionales

### Perfiles de Maven Disponibles

- **`env-dev`** (por defecto): Compilación de desarrollo, solo módulos básicos
- **`env-install`**: Compilación completa con aplicaciones y servicios
- **`env-deploy`**: Compilación para despliegue en repositorio
- **`sonar`**: Para análisis con SonarQube
- **`minhap`**: Para despliegue en repositorio del MINHAP

### Estructura de Directorios

```
firma/
├── afirma-core/              # Módulo core
├── afirma-simple/            # Aplicación principal
├── afirma-server-*/          # Servicios servidor
├── afirma-crypto-*/          # Módulos criptográficos
├── afirma-ui-*/              # Módulos de interfaz
├── pom.xml                   # POM padre
└── README.md                 # Documentación original
```

### Solución de Problemas Comunes

1. **Error: "Unable to access jarfile afirma-simple/target/autofirma.jar"**
   - **Causa**: El JAR no existe porque el proyecto no se ha compilado con el perfil correcto
   - **Solución**: Compila el proyecto con el perfil `env-install`:
     ```bash
     mvn install -Denv=install -DskipTests
     ```
   - **Nota**: Si tienes problemas con `mvn clean`, omítelo y usa solo `mvn install`

2. **Error: "Could not find artifact org.java-websocket:Java-WebSocket:jar:1.6.1-SNAPSHOT"**
   - **Causa**: La versión SNAPSHOT no está disponible en Maven Central
   - **Solución**: Actualiza la versión en `afirma-simple/pom.xml` de `1.6.1-SNAPSHOT` a `1.5.3`

3. **Error de memoria**: Aumenta `MAVEN_OPTS`:
   ```bash
   export MAVEN_OPTS="-Xmx4096m"
   ```

4. **Tests fallan**: Omite tests durante desarrollo:
   ```bash
   mvn install -Denv=install -DskipTests
   ```

5. **Dependencias no encontradas**: Actualiza repositorios:
   ```bash
   mvn install -Denv=install -U -DskipTests
   ```

6. **Problemas de compilación**: Si `mvn clean` falla por archivos bloqueados, omítelo:
   ```bash
   mvn install -Denv=install -DskipTests
   ```

---

## 📚 Referencias

- **Repositorio**: [GitHub - clienteafirma](https://github.com/ctt-gob-es/clienteafirma)
- **Documentación oficial**: [CTT - Cliente @firma](https://administracionelectronica.gob.es/ctt/clienteafirma)
- **Licencia**: GPL 2+ y EUPL 1.1

---

*Última actualización: Basado en la versión 1.9 del proyecto*

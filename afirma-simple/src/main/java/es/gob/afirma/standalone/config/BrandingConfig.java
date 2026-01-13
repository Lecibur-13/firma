package es.gob.afirma.standalone.config;

import java.io.File;
import java.util.logging.Logger;

/**
 * Configuración de branding personalizable mediante variables de entorno.
 * Permite personalizar logos, nombres de organización y referencias sin modificar el código.
 */
public class BrandingConfig {

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	// Variables de entorno para configuración
	private static final String ENV_ORGANIZATION_NAME = "AUTOFIRMA_ORGANIZATION_NAME";
	private static final String ENV_ORGANIZATION_URL = "AUTOFIRMA_ORGANIZATION_URL";
	private static final String ENV_ORGANIZATION_EMAIL = "AUTOFIRMA_ORGANIZATION_EMAIL";
	private static final String ENV_UPDATE_SITE_URL = "AUTOFIRMA_UPDATE_SITE_URL";
	private static final String ENV_LOGO_CLIENTE = "AUTOFIRMA_LOGO_CLIENTE";
	private static final String ENV_LOGO_ORGANIZATION = "AUTOFIRMA_LOGO_ORGANIZATION";
	private static final String ENV_MEDIA_DIR = "AUTOFIRMA_MEDIA_DIR";
	private static final String ENV_COPYRIGHT = "AUTOFIRMA_COPYRIGHT";

	// Valores por defecto genéricos (no relacionados con ninguna institución específica)
	private static final String DEFAULT_ORGANIZATION_NAME = "Organización";
	private static final String DEFAULT_ORGANIZATION_URL = "https://ejemplo.com";
	private static final String DEFAULT_ORGANIZATION_EMAIL = "soporte@ejemplo.com";
	private static final String DEFAULT_UPDATE_SITE_URL = "https://ejemplo.com/actualizaciones";
	private static final String DEFAULT_LOGO_CLIENTE = "logo_cliente_256.png";
	private static final String DEFAULT_LOGO_ORGANIZATION = "logo_cliente_256.png";
	private static final String DEFAULT_COPYRIGHT = "Organización";

	// Directorio de media por defecto (relativo al directorio de instalación)
	private static final String DEFAULT_MEDIA_DIR = "media" + File.separator + "logos";

	// Instancia singleton
	private static BrandingConfig instance;

	// Valores de configuración
	private final String organizationName;
	private final String organizationUrl;
	private final String organizationEmail;
	private final String updateSiteUrl;
	private final String logoCliente;
	private final String logoOrganization;
	private final String mediaDir;
	private final String copyright;

	/**
	 * Constructor privado para singleton.
	 */
	private BrandingConfig() {
		// Leer variables de entorno o usar valores por defecto
		this.organizationName = getEnvOrDefault(ENV_ORGANIZATION_NAME, DEFAULT_ORGANIZATION_NAME);
		this.organizationUrl = getEnvOrDefault(ENV_ORGANIZATION_URL, DEFAULT_ORGANIZATION_URL);
		this.organizationEmail = getEnvOrDefault(ENV_ORGANIZATION_EMAIL, DEFAULT_ORGANIZATION_EMAIL);
		this.updateSiteUrl = getEnvOrDefault(ENV_UPDATE_SITE_URL, DEFAULT_UPDATE_SITE_URL);
		this.logoCliente = getEnvOrDefault(ENV_LOGO_CLIENTE, DEFAULT_LOGO_CLIENTE);
		this.logoOrganization = getEnvOrDefault(ENV_LOGO_ORGANIZATION, DEFAULT_LOGO_ORGANIZATION);
		this.copyright = getEnvOrDefault(ENV_COPYRIGHT, DEFAULT_COPYRIGHT);

		// Para el directorio de media, intentar obtenerlo de la variable de entorno
		// o construir la ruta relativa al directorio de instalación
		String mediaDirEnv = System.getenv(ENV_MEDIA_DIR);
		if (mediaDirEnv != null && !mediaDirEnv.isEmpty()) {
			this.mediaDir = mediaDirEnv;
		} else {
			// Intentar construir la ruta relativa al directorio de instalación
			String appHome = System.getProperty("user.dir"); //$NON-NLS-1$
			if (appHome != null) {
				this.mediaDir = appHome + File.separator + DEFAULT_MEDIA_DIR;
			} else {
				this.mediaDir = DEFAULT_MEDIA_DIR;
			}
		}

		LOGGER.info("BrandingConfig inicializado - Organización: " + this.organizationName); //$NON-NLS-1$
		LOGGER.info("Media directory: " + this.mediaDir); //$NON-NLS-1$
	}

	/**
	 * Obtiene la instancia singleton de BrandingConfig.
	 * 
	 * @return Instancia de BrandingConfig
	 */
	public static synchronized BrandingConfig getInstance() {
		if (instance == null) {
			instance = new BrandingConfig();
		}
		return instance;
	}

	/**
	 * Lee una variable de entorno o devuelve el valor por defecto.
	 * 
	 * @param envVar Nombre de la variable de entorno
	 * @param defaultValue Valor por defecto
	 * @return Valor de la variable de entorno o el valor por defecto
	 */
	private String getEnvOrDefault(final String envVar, final String defaultValue) {
		String value = System.getenv(envVar);
		if (value != null && !value.trim().isEmpty()) {
			return value.trim();
		}
		return defaultValue;
	}

	/**
	 * Obtiene el nombre de la organización.
	 * 
	 * @return Nombre de la organización
	 */
	public String getOrganizationName() {
		return this.organizationName;
	}

	/**
	 * Obtiene la URL de la organización.
	 * 
	 * @return URL de la organización
	 */
	public String getOrganizationUrl() {
		return this.organizationUrl;
	}

	/**
	 * Obtiene el email de soporte de la organización.
	 * 
	 * @return Email de soporte
	 */
	public String getOrganizationEmail() {
		return this.organizationEmail;
	}

	/**
	 * Obtiene la URL del sitio de actualizaciones.
	 * 
	 * @return URL del sitio de actualizaciones
	 */
	public String getUpdateSiteUrl() {
		return this.updateSiteUrl;
	}

	/**
	 * Obtiene el nombre del archivo del logo del cliente.
	 * 
	 * @return Nombre del archivo del logo
	 */
	public String getLogoCliente() {
		return this.logoCliente;
	}

	/**
	 * Obtiene el nombre del archivo del logo de la organización.
	 * 
	 * @return Nombre del archivo del logo
	 */
	public String getLogoOrganization() {
		return this.logoOrganization;
	}

	/**
	 * Obtiene el directorio donde se almacenan los logos personalizados.
	 * 
	 * @return Ruta del directorio de media
	 */
	public String getMediaDir() {
		return this.mediaDir;
	}

	/**
	 * Obtiene el texto de copyright.
	 * 
	 * @return Texto de copyright
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * Obtiene la ruta completa del archivo de logo, buscando primero en media,
	 * luego en recursos.
	 * 
	 * @param logoFilename Nombre del archivo de logo
	 * @return Ruta completa del logo o null si no se encuentra
	 */
	public String getLogoPath(final String logoFilename) {
		if (logoFilename == null || logoFilename.isEmpty()) {
			return null;
		}

		// Primero buscar en el directorio de media
		File mediaFile = new File(this.mediaDir, logoFilename);
		if (mediaFile.exists() && mediaFile.isFile()) {
			LOGGER.fine("Logo encontrado en media: " + mediaFile.getAbsolutePath()); //$NON-NLS-1$
			return mediaFile.getAbsolutePath();
		}

		// Si no se encuentra en media, devolver null para que se busque en recursos
		LOGGER.fine("Logo no encontrado en media, se usará recurso por defecto: " + logoFilename); //$NON-NLS-1$
		return null;
	}
}

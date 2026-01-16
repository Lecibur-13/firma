package es.gob.afirma.standalone.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import es.gob.afirma.standalone.DesktopUtil;

public class ImageLoader {

	public static final int XLARGE_ICON = 4;
	public static final int LARGE_ICON = 3;
	public static final int MEDIUM_ICON = 2;
	public static final int SMALL_ICON = 1;

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	public static ImageIcon loadIcon(final String filename) {

		ImageIcon icon = null;
		final Image image = loadImage(filename);
		if (image != null) {
			icon = new ImageIcon(image);
		}
		return icon;
	}

	public static ImageIcon loadIcon(final String filename, final int size) {

		ImageIcon icon = null;
		BufferedImage image = loadImage(filename);
		if (image != null) {
			final Dimension dim = getDimension(size);
			if (dim != null) {
				image = resize(image, dim);
			}
			icon = new ImageIcon(image);
		}
		return icon;
	}

	/**
	 * Carga una imagen desde los recursos por defecto.
	 * 
	 * @param filename Nombre del archivo de imagen
	 * @return BufferedImage cargada o null si no se encuentra
	 */
	public static BufferedImage loadImage(final String filename) {
		BufferedImage image = null;

		// Cargar desde recursos por defecto
		try (final InputStream is = ImageLoader.class.getResourceAsStream("/resources/" + filename)) { //$NON-NLS-1$
			if (is != null) {
				image = ImageIO.read(is);
				LOGGER.fine("Imagen cargada desde recursos: " + filename); //$NON-NLS-1$
			}
		}
		catch (final Exception e) {
			LOGGER.warning(
					"No se pudo cargar la imagen '" + filename + "': " + e);  //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		return image;
	}

	private static Dimension getDimension(final int size) {
		Dimension dim;
		switch (size) {
		case XLARGE_ICON:
			dim = new Dimension(256, 256);
			break;
		case LARGE_ICON:
			dim = new Dimension(128, 128);
			break;
		case MEDIUM_ICON:
			dim = new Dimension(64, 64);
			break;
		case SMALL_ICON:
			dim = new Dimension(32, 32);
			break;
		default:
			dim = null;
		}
		return dim;
	}

    private static BufferedImage resize(final BufferedImage img, final Dimension size) {
        final Image tmp = img.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_SMOOTH);
        final BufferedImage resized = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

	/**
	 * Carga el logo institucional, buscando primero en media/logo/ y luego en recursos.
	 * Busca archivos con extensiones .png o .jpg (sin importar mayúsculas/minúsculas).
	 * 
	 * @return ImageIcon del logo o null si no se encuentra
	 */
	public static ImageIcon loadInstitutionalLogo() {
		// Primero intentar cargar desde media/logo/
		// Buscar en varias ubicaciones posibles:
		// 1. Relativo al directorio de trabajo actual (user.dir)
		// 2. Relativo al directorio de la aplicación
		File mediaLogoDir = null;
		
		// Intentar desde el directorio de trabajo actual
		final String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
		if (userDir != null) {
			final File mediaDir = new File(userDir, "media" + File.separator + "logo"); //$NON-NLS-1$ //$NON-NLS-2$
			if (mediaDir.exists() && mediaDir.isDirectory()) {
				mediaLogoDir = mediaDir;
			}
		}
		
		// Si no se encontró, intentar desde el directorio de la aplicación
		if (mediaLogoDir == null) {
			final File appDir = DesktopUtil.getApplicationDirectory();
			if (appDir != null) {
				// Intentar en el mismo directorio que el JAR
				File testDir = new File(appDir, "media" + File.separator + "logo"); //$NON-NLS-1$ //$NON-NLS-2$
				if (!testDir.exists() && appDir.getParentFile() != null) {
					// Intentar en el directorio padre
					testDir = new File(appDir.getParentFile(), "media" + File.separator + "logo"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (testDir.exists() && testDir.isDirectory()) {
					mediaLogoDir = testDir;
				}
			}
		}
		
		if (mediaLogoDir != null && mediaLogoDir.exists() && mediaLogoDir.isDirectory()) {
			// Buscar archivos .png o .jpg en el directorio
			final File[] logoFiles = mediaLogoDir.listFiles((dir, name) -> {
				final String lowerName = name.toLowerCase();
				return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			});
			
			if (logoFiles != null && logoFiles.length > 0) {
				// Tomar el primer archivo encontrado
				final File logoFile = logoFiles[0];
				try (final FileInputStream fis = new FileInputStream(logoFile)) {
					final BufferedImage image = ImageIO.read(fis);
					if (image != null) {
						LOGGER.info("Logo institucional cargado desde: " + logoFile.getAbsolutePath()); //$NON-NLS-1$
						return new ImageIcon(image);
					}
				}
				catch (final Exception e) {
					LOGGER.warning("No se pudo cargar el logo desde media/logo/: " + e.getMessage()); //$NON-NLS-1$
				}
			}
		}
		
		// Si no se encontró en media/logo/, intentar cargar desde recursos por defecto
		// Intentar primero con logo_cliente_256.png
		ImageIcon logoIcon = loadIcon("logo_cliente_256.png"); //$NON-NLS-1$
		if (logoIcon == null) {
			// Si no existe, intentar con logo_cliente.png
			logoIcon = loadIcon("logo_cliente.png"); //$NON-NLS-1$
		}
		
		if (logoIcon != null) {
			LOGGER.info("Logo cargado desde recursos por defecto"); //$NON-NLS-1$
		} else {
			LOGGER.warning("No se encontró logo institucional ni en media/logo/ ni en recursos"); //$NON-NLS-1$
		}
		
		return logoIcon;
	}
}

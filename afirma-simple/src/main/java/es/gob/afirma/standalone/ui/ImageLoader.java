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

import es.gob.afirma.standalone.config.BrandingConfig;

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
	 * Carga una imagen, buscando primero en el directorio de media personalizado,
	 * y si no se encuentra, en los recursos por defecto.
	 * 
	 * @param filename Nombre del archivo de imagen
	 * @return BufferedImage cargada o null si no se encuentra
	 */
	public static BufferedImage loadImage(final String filename) {
		BufferedImage image = null;

		// Primero intentar cargar desde el directorio de media personalizado
		final BrandingConfig branding = BrandingConfig.getInstance();
		final String logoPath = branding.getLogoPath(filename);

		if (logoPath != null) {
			try (final FileInputStream fis = new FileInputStream(new File(logoPath))) {
				image = ImageIO.read(fis);
				LOGGER.fine("Imagen cargada desde media: " + logoPath); //$NON-NLS-1$
				return image;
			}
			catch (final Exception e) {
				LOGGER.warning("No se pudo cargar la imagen desde media '" + logoPath + "': " + e); //$NON-NLS-1$ //$NON-NLS-2$
				// Continuar para intentar cargar desde recursos
			}
		}

		// Si no se encontró en media, intentar cargar desde recursos por defecto
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
}

package es.gob.afirma.standalone.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.config.BrandingConfig;

/**
 * Di&aacute;logo con la informaci&oacute;n acerca de la aplicaci&oacute;n.
 */
public class AboutDialog {

	/** Codifica caracteres especiales HTML para evitar problemas de formato.
	 * @param text Texto a codificar.
	 * @return Texto codificado con entidades HTML. */
	private static String htmlEncode(final String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("<", "&lt;") //$NON-NLS-1$ //$NON-NLS-2$
				.replace(">", "&gt;") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("\"", "&quot;") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("'", "&#39;"); //$NON-NLS-1$ //$NON-NLS-2$
	}

    /** Muestra el di&aacute;logo "Acerca de...".
     * @param parentComponent Componente padre para la modalidad. */
    public static void showAbout(final Component parentComponent) {
    	final BrandingConfig branding = BrandingConfig.getInstance();
    	
    	// Obtener el mensaje traducible y reemplazar la organización con el branding
    	String aboutMessage = SimpleAfirmaMessages.getString("MainMenu.14", //$NON-NLS-1$
    			SimpleAfirma.getVersion(),
    			System.getProperty("java.version"), //$NON-NLS-1$
    			Platform.getJavaArch());
    	
    	// Reemplazar "Gobierno de España" con el copyright configurado si es diferente del valor por defecto
    	// Solo reemplazar si el branding está personalizado (no es el valor genérico por defecto)
    	if (!"Organización".equals(branding.getCopyright())) { //$NON-NLS-1$
    		// Codificar el copyright para HTML antes de insertarlo
    		final String encodedCopyright = htmlEncode(branding.getCopyright());
    		aboutMessage = aboutMessage.replace("Gobierno de Espa&ntilde;a", encodedCopyright); //$NON-NLS-1$
    		aboutMessage = aboutMessage.replace("Gobierno de España", encodedCopyright); //$NON-NLS-1$
    	}
    	
        AOUIFactory.showMessageDialog(
    		parentComponent,
			aboutMessage,
            SimpleAfirmaMessages.getString("MainMenu.15"), //$NON-NLS-1$
            JOptionPane.PLAIN_MESSAGE
        );
    }
}

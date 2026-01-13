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

    /** Muestra el di&aacute;logo "Acerca de...".
     * @param parentComponent Componente padre para la modalidad. */
    public static void showAbout(final Component parentComponent) {
    	final BrandingConfig branding = BrandingConfig.getInstance();
    	
    	// Construir el mensaje usando la configuración de branding
    	final String aboutMessage = String.format(
    		"<html><p align=\"center\">Autofirma %s<br>&copy; 2011-2025 %s</p><br>" +
    		"<p>Autofirma es Software Libre; puedes redistribuirlo y/o modificarlo bajo los términos de al menos<br>" +
    		"una de estas dos licencias: <ul><li>La \"GNU General Public License\" tal como es publicada por la Free Software Foundation;<br>" +
    		"versión 2 de la Licencia, o (a su elección) cualquier versión posterior.</li><li>La \"European Software License\"; versión 1.1 de la Licencia, o (a su elección) cualquier<br>" +
    		"versión posterior.</li></ul><p>Autofirma contiene, entre otros, los siguientes componentes de software libre:<br>" +
    		"iText 2.1.7 (MPL 1.1/LGPL), jMimeMagic (Apache License 2.0), JUniversalCharDet (MPL 1.1),<br>" +
    		"jXAdES (EUPL 1.1/GPLv3), SpongyCastle (MIT), Java WebSockets (MIT), Proxy Vole (Apache License 2.0), etc.</p>" +
    		"<p><br>Versión de Java actualmente en uso: %s (%s bits)</p></html>",
    		SimpleAfirma.getVersion(),
    		branding.getCopyright(),
    		System.getProperty("java.version"), //$NON-NLS-1$
    		Platform.getJavaArch()
    	);
    	
        AOUIFactory.showMessageDialog(
    		parentComponent,
			aboutMessage,
            SimpleAfirmaMessages.getString("MainMenu.15"), //$NON-NLS-1$
            JOptionPane.PLAIN_MESSAGE
        );
    }
}

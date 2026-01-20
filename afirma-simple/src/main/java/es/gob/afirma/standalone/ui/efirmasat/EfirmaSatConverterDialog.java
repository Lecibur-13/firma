/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * You may contact the copyright holder at: soporte.afirma@seap.minhap.es
 */

package es.gob.afirma.standalone.ui.efirmasat;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.gob.afirma.standalone.so.macos.MacUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;

/** Di&aacute;logo para convertir e.firma del SAT (.cer + .key) a PFX e instalarlo en Windows.
 * @author Cliente @Firma */
public final class EfirmaSatConverterDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$
	
	private final JTextField cerFileField = new JTextField(40);
	private final JTextField keyFileField = new JTextField(40);
	private final JPasswordField passwordField = new JPasswordField(20);
	
	private File selectedCerFile = null;
	private File selectedKeyFile = null;

	/** Constructor del di&aacute;logo.
	 * @param parent Componente padre para la modalidad */
	public EfirmaSatConverterDialog(final Frame parent) {
		super(parent);
		setTitle(SimpleAfirmaMessages.getString("EfirmaSatConverter.title")); //$NON-NLS-1$
		setModalityType(ModalityType.APPLICATION_MODAL);
		createUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}
	
	/** Crea la interfaz del di&aacute;logo. */
	private void createUI() {
		final JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		// Descripción
		final JLabel descLabel = new JLabel(
			"<html>" + SimpleAfirmaMessages.getString("EfirmaSatConverter.description") + "</html>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		);
		gbc.gridwidth = 3;
		mainPanel.add(descLabel, gbc);
		gbc.gridwidth = 1;
		
		// Archivo .cer
		gbc.gridy++;
		gbc.gridx = 0;
		final JLabel cerLabel = new JLabel(SimpleAfirmaMessages.getString("EfirmaSatConverter.cerFile")); //$NON-NLS-1$
		mainPanel.add(cerLabel, gbc);
		
		gbc.gridx = 1;
		this.cerFileField.setEditable(false);
		mainPanel.add(this.cerFileField, gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 0.0;
		final JButton browseCerButton = new JButton(SimpleAfirmaMessages.getString("EfirmaSatConverter.browse")); //$NON-NLS-1$
		browseCerButton.addActionListener(e -> browseCerFile());
		mainPanel.add(browseCerButton, gbc);
		
		// Archivo .key
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		final JLabel keyLabel = new JLabel(SimpleAfirmaMessages.getString("EfirmaSatConverter.keyFile")); //$NON-NLS-1$
		mainPanel.add(keyLabel, gbc);
		
		gbc.gridx = 1;
		this.keyFileField.setEditable(false);
		mainPanel.add(this.keyFileField, gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 0.0;
		final JButton browseKeyButton = new JButton(SimpleAfirmaMessages.getString("EfirmaSatConverter.browse")); //$NON-NLS-1$
		browseKeyButton.addActionListener(e -> browseKeyFile());
		mainPanel.add(browseKeyButton, gbc);
		
		// Contraseña
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		final JLabel passwordLabel = new JLabel(SimpleAfirmaMessages.getString("EfirmaSatConverter.password")); //$NON-NLS-1$
		mainPanel.add(passwordLabel, gbc);
		
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		mainPanel.add(this.passwordField, gbc);
		gbc.gridwidth = 1;
		
		// Botones
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.insets = new Insets(20, 5, 5, 5);
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		final JButton cancelButton = new JButton(SimpleAfirmaMessages.getString("EfirmaSatConverter.cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(e -> dispose());
		buttonPanel.add(cancelButton);
		
		final JButton convertButton = new JButton(SimpleAfirmaMessages.getString("EfirmaSatConverter.convert")); //$NON-NLS-1$
		convertButton.addActionListener(e -> convertAndInstall());
		buttonPanel.add(convertButton);
		
		mainPanel.add(buttonPanel, gbc);
		
		add(mainPanel);
	}
	
	/** Abre un di&aacute;logo para seleccionar el archivo .cer */
	private void browseCerFile() {
		try {
			final File[] files = AOUIFactory.getLoadFiles(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.selectCer"), //$NON-NLS-1$
				null,
				null,
				new String[] { "cer" }, //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.cerFiles"), //$NON-NLS-1$
				false,
				false,
				null,
				this
			);
			if (files != null && files.length > 0) {
				this.selectedCerFile = files[0];
				this.cerFileField.setText(files[0].getAbsolutePath());
			}
		}
		catch (final AOCancelledOperationException e) {
			// Usuario canceló
		}
	}
	
	/** Abre un di&aacute;logo para seleccionar el archivo .key */
	private void browseKeyFile() {
		try {
			final File[] files = AOUIFactory.getLoadFiles(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.selectKey"), //$NON-NLS-1$
				null,
				null,
				new String[] { "key" }, //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.keyFiles"), //$NON-NLS-1$
				false,
				false,
				null,
				this
			);
			if (files != null && files.length > 0) {
				this.selectedKeyFile = files[0];
				this.keyFileField.setText(files[0].getAbsolutePath());
			}
		}
		catch (final AOCancelledOperationException e) {
			// Usuario canceló
		}
	}
	
	/** Convierte los archivos .cer y .key a PFX e instala el certificado en Windows. */
	private void convertAndInstall() {
		// Verificar que OpenSSL esté disponible
		final String opensslPath = findOpenSsl();
		if (opensslPath == null) {
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorOpenSslNotFound"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				null
			);
			return;
		}
		
		// Validar campos
		if (this.selectedCerFile == null || !this.selectedCerFile.exists()) {
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorNoCer"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				null
			);
			return;
		}
		
		if (this.selectedKeyFile == null || !this.selectedKeyFile.exists()) {
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorNoKey"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				null
			);
			return;
		}
		
		final char[] password = this.passwordField.getPassword();
		if (password == null || password.length == 0) {
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorNoPassword"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				null
			);
			return;
		}
		
		// Crear directorio temporal
		final File tempDir;
		try {
			tempDir = File.createTempFile("efirma_sat_", ""); //$NON-NLS-1$ //$NON-NLS-2$
			tempDir.delete();
			tempDir.mkdir();
		}
		catch (final IOException e) {
			LOGGER.severe("Error al crear directorio temporal: " + e); //$NON-NLS-1$
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorTemp"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				e
			);
			return;
		}
		
		final File certPemFile = new File(tempDir, "cert.pem"); //$NON-NLS-1$
		final File rsaKeyPemFile = new File(tempDir, "rsa_key.pem"); //$NON-NLS-1$
		final File pfxFile = new File(tempDir, "certificado.pfx"); //$NON-NLS-1$
		
		try {
			// Paso 1: Convertir .cer a PEM
			LOGGER.info("Paso 1: Convirtiendo .cer a PEM..."); //$NON-NLS-1$
			executeOpenSslCommand(
				opensslPath,
				"x509", //$NON-NLS-1$
				"-inform", "DER", //$NON-NLS-1$ //$NON-NLS-2$
				"-in", this.selectedCerFile.getAbsolutePath(), //$NON-NLS-1$
				"-out", certPemFile.getAbsolutePath() //$NON-NLS-1$
			);
			
			if (!certPemFile.exists()) {
				throw new IOException("No se pudo crear el archivo cert.pem"); //$NON-NLS-1$
			}
			LOGGER.info("Certificado convertido a PEM exitosamente"); //$NON-NLS-1$
			
			// Paso 2: Convertir .key a RSA PEM
			// Intentamos primero con pkcs8 (formato común del SAT), si falla probamos con rsa
			LOGGER.info("Paso 2: Convirtiendo .key a RSA PEM..."); //$NON-NLS-1$
			final String passwordStr = new String(password);
			boolean conversionSuccess = false;
			
			// Intentar primero con pkcs8 (formato más común para claves del SAT)
			try {
				LOGGER.info("Intentando conversion con formato PKCS8..."); //$NON-NLS-1$
				executeOpenSslCommandWithPassword(
					passwordStr,
					opensslPath,
					"pkcs8", //$NON-NLS-1$
					"-in", this.selectedKeyFile.getAbsolutePath(), //$NON-NLS-1$
					"-out", rsaKeyPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-nocrypt", //$NON-NLS-1$
					"-passin", "pass:" + passwordStr //$NON-NLS-1$ //$NON-NLS-2$
				);
				if (rsaKeyPemFile.exists() && rsaKeyPemFile.length() > 0) {
					conversionSuccess = true;
					LOGGER.info("Conversion exitosa con formato PKCS8"); //$NON-NLS-1$
				}
			}
			catch (final IOException e) {
				LOGGER.warning("Fallo conversion con PKCS8, intentando con RSA: " + e.getMessage()); //$NON-NLS-1$
				// Si falla, intentar con formato RSA tradicional
				try {
					LOGGER.info("Intentando conversion con formato RSA..."); //$NON-NLS-1$
					if (rsaKeyPemFile.exists()) {
						rsaKeyPemFile.delete();
					}
					executeOpenSslCommandWithPassword(
						passwordStr,
						opensslPath,
						"rsa", //$NON-NLS-1$
						"-in", this.selectedKeyFile.getAbsolutePath(), //$NON-NLS-1$
						"-out", rsaKeyPemFile.getAbsolutePath(), //$NON-NLS-1$
						"-passin", "pass:" + passwordStr //$NON-NLS-1$ //$NON-NLS-2$
					);
					if (rsaKeyPemFile.exists() && rsaKeyPemFile.length() > 0) {
						conversionSuccess = true;
						LOGGER.info("Conversion exitosa con formato RSA"); //$NON-NLS-1$
					}
				}
				catch (final IOException e2) {
					LOGGER.severe("Fallo conversion con ambos formatos: " + e2.getMessage()); //$NON-NLS-1$
					throw new IOException("No se pudo convertir la clave privada. Verifique que la contraseña sea correcta y que el archivo .key sea valido.\n" + e2.getMessage(), e2); //$NON-NLS-1$
				}
			}
			
			if (!conversionSuccess || !rsaKeyPemFile.exists() || rsaKeyPemFile.length() == 0) {
				throw new IOException("No se pudo crear el archivo rsa_key.pem. Verifique que la contraseña sea correcta."); //$NON-NLS-1$
			}
			
			if (!rsaKeyPemFile.exists()) {
				throw new IOException("No se pudo crear el archivo rsa_key.pem"); //$NON-NLS-1$
			}
			LOGGER.info("Clave privada convertida a RSA PEM exitosamente"); //$NON-NLS-1$
			
			// Paso 3: Validar certificado contra CA.pem
			LOGGER.info("Paso 3: Validando certificado contra CA.pem..."); //$NON-NLS-1$
			final File caPemFile = findCaPemFile();
			if (caPemFile != null && caPemFile.exists()) {
				validateCertificateAgainstCA(opensslPath, certPemFile, caPemFile);
			} else {
				validateCertificateAgainstCA(opensslPath, certPemFile, null);
			}
			
			// Paso 4: Generar PFX con CA como certificado raíz si está disponible
			LOGGER.info("Paso 4: Generando archivo PFX..."); //$NON-NLS-1$
			if (caPemFile != null && caPemFile.exists()) {
				LOGGER.info("Incluyendo CA.pem como certificado raiz en el PFX para mayor fiabilidad"); //$NON-NLS-1$
				executeOpenSslCommandWithPassword(
					passwordStr,
					opensslPath,
					"pkcs12", //$NON-NLS-1$
					"-export", //$NON-NLS-1$
					"-passin", "pass:" + passwordStr, //$NON-NLS-1$ //$NON-NLS-2$
					"-passout", "pass:" + passwordStr, //$NON-NLS-1$ //$NON-NLS-2$
					"-inkey", rsaKeyPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-in", certPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-certfile", caPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-out", pfxFile.getAbsolutePath() //$NON-NLS-1$
				);
			} else {
				LOGGER.warning("CA.pem no disponible, generando PFX sin certificado raiz"); //$NON-NLS-1$
				executeOpenSslCommandWithPassword(
					passwordStr,
					opensslPath,
					"pkcs12", //$NON-NLS-1$
					"-export", //$NON-NLS-1$
					"-passin", "pass:" + passwordStr, //$NON-NLS-1$ //$NON-NLS-2$
					"-passout", "pass:" + passwordStr, //$NON-NLS-1$ //$NON-NLS-2$
					"-inkey", rsaKeyPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-in", certPemFile.getAbsolutePath(), //$NON-NLS-1$
					"-out", pfxFile.getAbsolutePath() //$NON-NLS-1$
				);
			}
			
			if (!pfxFile.exists()) {
				throw new IOException("No se pudo crear el archivo PFX"); //$NON-NLS-1$
			}
			LOGGER.info("Archivo PFX generado exitosamente: " + pfxFile.getAbsolutePath()); //$NON-NLS-1$
			
			// Paso 5: Abrir el PFX para instalarlo en Windows
			LOGGER.info("Paso 5: Abriendo PFX para instalacion en Windows..."); //$NON-NLS-1$
			
			// Verificar que el archivo existe
			if (!pfxFile.exists()) {
				throw new IOException("El archivo PFX no existe: " + pfxFile.getAbsolutePath()); //$NON-NLS-1$
			}
			
			// En Windows, usar múltiples métodos para asegurar que se abra el asistente
			if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
				// Método 1: Usar cmd /C start (más confiable en Windows)
				try {
					LOGGER.info("Abriendo archivo PFX con cmd /C start..."); //$NON-NLS-1$
					final ProcessBuilder pb = new ProcessBuilder(
						"cmd", "/C", "start", "\"\"", "\"" + pfxFile.getAbsolutePath() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					);
					pb.start();
					LOGGER.info("Comando ejecutado para abrir el archivo PFX"); //$NON-NLS-1$
				}
				catch (final IOException e) {
					LOGGER.warning("Error con cmd /C start, intentando con Desktop.getDesktop().open(): " + e.getMessage()); //$NON-NLS-1$
					// Método alternativo
					try {
						if (Desktop.isDesktopSupported()) {
							Desktop.getDesktop().open(pfxFile);
							LOGGER.info("Archivo abierto con Desktop.getDesktop().open()"); //$NON-NLS-1$
						}
					}
					catch (final IOException e2) {
						LOGGER.severe("Error al abrir el archivo PFX: " + e2.getMessage()); //$NON-NLS-1$
					}
				}
			}
			else {
				// En otros sistemas operativos, usar Desktop
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(pfxFile);
						LOGGER.info("Archivo abierto con Desktop.getDesktop().open()"); //$NON-NLS-1$
					}
					catch (final IOException e) {
						LOGGER.severe("Error al abrir el archivo PFX: " + e.getMessage()); //$NON-NLS-1$
					}
				}
			}
			
			// Esperar un momento para que el proceso se inicie
			try {
				Thread.sleep(1000);
			}
			catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			// Mostrar mensaje de éxito
			AOUIFactory.showMessageDialog(
				this,
				SimpleAfirmaMessages.getString("EfirmaSatConverter.success"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.successTitle"), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE,
				null
			);
			
			// Preguntar al usuario si ya completó la instalación para eliminar el archivo
			final int result = AOUIFactory.showConfirmDialog(
				this,
				SimpleAfirmaMessages.getString("EfirmaSatConverter.deletePfxQuestion"), //$NON-NLS-1$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.deletePfxTitle"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			);
			
			if (result == JOptionPane.YES_OPTION) {
				// Intentar eliminar la carpeta completa
				boolean deleted = deleteDirectory(tempDir);
				if (deleted) {
					LOGGER.info("Carpeta temporal eliminada exitosamente"); //$NON-NLS-1$
				}
				else {
					LOGGER.warning("No se pudo eliminar la carpeta temporal: " + tempDir.getAbsolutePath()); //$NON-NLS-1$
					// Marcar para eliminación al salir
					deleteDirectoryOnExit(tempDir);
				}
				
				// Reiniciar la aplicación
				restartApplication();
			}
			else {
				// Abrir el explorador de archivos en la ubicación del archivo PFX
				openFileLocation(pfxFile);
				// Marcar para eliminación al salir de la aplicación
				deleteDirectoryOnExit(tempDir);
			}
			
			dispose();
		}
		catch (final IOException e) {
			LOGGER.severe("Error durante la conversion: " + e); //$NON-NLS-1$
			String errorMessage = SimpleAfirmaMessages.getString("EfirmaSatConverter.errorConversion"); //$NON-NLS-1$
			if (e.getMessage() != null && e.getMessage().contains("CreateProcess")) { //$NON-NLS-1$
				errorMessage = SimpleAfirmaMessages.getString("EfirmaSatConverter.errorOpenSslNotFound"); //$NON-NLS-1$
			} else if (e.getMessage() != null && (e.getMessage().contains("wrong password") || e.getMessage().contains("bad decrypt") || e.getMessage().contains("Verifique que la contraseña"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				errorMessage = SimpleAfirmaMessages.getString("EfirmaSatConverter.errorWrongPassword"); //$NON-NLS-1$
			} else {
				errorMessage += "\n" + e.getMessage(); //$NON-NLS-1$
			}
			AOUIFactory.showErrorMessage(
				errorMessage,
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				e
			);
		}
		catch (final Exception e) {
			LOGGER.severe("Error durante la conversion: " + e); //$NON-NLS-1$
			AOUIFactory.showErrorMessage(
				SimpleAfirmaMessages.getString("EfirmaSatConverter.errorConversion") + "\n" + e.getMessage(), //$NON-NLS-1$ //$NON-NLS-2$
				SimpleAfirmaMessages.getString("EfirmaSatConverter.error"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE,
				e
			);
		}
		finally {
			// No eliminamos archivos aquí - se manejan según la respuesta del usuario
			// Si el usuario confirma la instalación, se elimina la carpeta completa
			// Si el usuario dice "no", se abre el explorador y se marca para eliminación al salir
		}
	}
	
	/** Ejecuta un comando OpenSSL.
	 * @param command Comando y argumentos
	 * @throws IOException Si hay error ejecutando el comando */
	private static void executeOpenSslCommand(final String... command) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		
		final Process process = pb.start();
		
		// Leer la salida
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				LOGGER.info("OpenSSL: " + line); //$NON-NLS-1$
			}
		}
		
		try {
			final int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new IOException("OpenSSL retorno codigo de error: " + exitCode); //$NON-NLS-1$
			}
		}
		catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Proceso interrumpido", e); //$NON-NLS-1$
		}
	}
	
	/** Ejecuta un comando OpenSSL que requiere contraseña.
	 * La contraseña se pasa mediante el parámetro -passin pass:password en el comando.
	 * @param password Contrase&ntilde;a a proporcionar (se usa para logging, pero la contraseña ya está en los argumentos)
	 * @param command Comando y argumentos (debe incluir -passin pass:password)
	 * @throws IOException Si hay error ejecutando el comando */
	private static void executeOpenSslCommandWithPassword(final String password, final String... command) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		
		final Process process = pb.start();
		
		// Leer la salida (la contraseña ya está en los argumentos del comando, no necesitamos enviarla por stdin)
		final StringBuilder output = new StringBuilder();
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				LOGGER.info("OpenSSL: " + line); //$NON-NLS-1$
				output.append(line).append("\n"); //$NON-NLS-1$
			}
		}
		
		try {
			final int exitCode = process.waitFor();
			if (exitCode != 0) {
				final String errorMsg = output.length() > 0 ? output.toString() : "Sin salida del proceso"; //$NON-NLS-1$
				throw new IOException("OpenSSL retorno codigo de error: " + exitCode + "\n" + errorMsg); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Proceso interrumpido", e); //$NON-NLS-1$
		}
	}
	
	/** Busca OpenSSL en el sistema.
	 * @return Ruta completa a openssl.exe o "openssl" si está en el PATH, o null si no se encuentra */
	private static String findOpenSsl() {
		// PRIORIDAD 1: Buscar en bin/openssl.exe relativo al directorio de trabajo actual (user.dir)
		final String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
		if (userDir != null) {
			final File opensslInBin = new File(userDir, "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$
			if (opensslInBin.exists() && isOpenSslAvailable(opensslInBin.getAbsolutePath())) {
				LOGGER.info("OpenSSL encontrado en bin/openssl.exe relativo al directorio de trabajo: " + opensslInBin.getAbsolutePath()); //$NON-NLS-1$
				return opensslInBin.getAbsolutePath();
			}
		}
		
		// PRIORIDAD 2: Buscar en bin/openssl.exe relativo al directorio de instalación de Autofirma
		final File appDir = DesktopUtil.getApplicationDirectory();
		if (appDir != null) {
			// Buscar en bin/openssl.exe (similar a como se busca media/logo/)
			final File opensslInBin = new File(appDir, "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$
			if (opensslInBin.exists() && isOpenSslAvailable(opensslInBin.getAbsolutePath())) {
				LOGGER.info("OpenSSL encontrado en bin/openssl.exe relativo al directorio de instalación: " + opensslInBin.getAbsolutePath()); //$NON-NLS-1$
				return opensslInBin.getAbsolutePath();
			}
			// También buscar en el directorio padre (similar a como se busca media/logo/)
			if (appDir.getParentFile() != null) {
				final File opensslInParentBin = new File(appDir.getParentFile(), "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$
				if (opensslInParentBin.exists() && isOpenSslAvailable(opensslInParentBin.getAbsolutePath())) {
					LOGGER.info("OpenSSL encontrado en bin/openssl.exe relativo al directorio padre: " + opensslInParentBin.getAbsolutePath()); //$NON-NLS-1$
					return opensslInParentBin.getAbsolutePath();
				}
			}
			// Buscar en openssl/bin/openssl.exe (estructura estándar de OpenSSL)
			final File opensslInApp = new File(appDir, "openssl" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (opensslInApp.exists() && isOpenSslAvailable(opensslInApp.getAbsolutePath())) {
				LOGGER.info("OpenSSL encontrado en el directorio de instalación: " + opensslInApp.getAbsolutePath()); //$NON-NLS-1$
				return opensslInApp.getAbsolutePath();
			}
			// También buscar directamente en el directorio de la aplicación (si está en la raíz)
			final File opensslDirect = new File(appDir, "openssl.exe"); //$NON-NLS-1$
			if (opensslDirect.exists() && isOpenSslAvailable(opensslDirect.getAbsolutePath())) {
				LOGGER.info("OpenSSL encontrado en el directorio de instalación: " + opensslDirect.getAbsolutePath()); //$NON-NLS-1$
				return opensslDirect.getAbsolutePath();
			}
		}
		
		// PRIORIDAD 3: Intentar con "openssl" directamente (si está en el PATH)
		if (isOpenSslAvailable("openssl")) { //$NON-NLS-1$
			LOGGER.info("OpenSSL encontrado en el PATH del sistema"); //$NON-NLS-1$
			return "openssl"; //$NON-NLS-1$
		}
		
		// PRIORIDAD 4: En Windows, buscar en ubicaciones comunes usando variables de entorno
		if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
			final List<String> commonPaths = new ArrayList<>();
			
			// Obtener variables de entorno de Windows dinámicamente
			final String programFiles = System.getenv("ProgramFiles"); //$NON-NLS-1$
			final String programFilesX86 = System.getenv("ProgramFiles(x86)"); //$NON-NLS-1$
			final String programData = System.getenv("ProgramData"); //$NON-NLS-1$
			final String localAppData = System.getenv("LOCALAPPDATA"); //$NON-NLS-1$
			final String commonProgramFiles = System.getenv("CommonProgramFiles"); //$NON-NLS-1$
			final String commonProgramFilesX86 = System.getenv("CommonProgramFiles(x86)"); //$NON-NLS-1$
			
			// Construir rutas dinámicamente usando variables de entorno
			if (programFiles != null) {
				commonPaths.add(programFiles + File.separator + "OpenSSL-Win64" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				commonPaths.add(programFiles + File.separator + "OpenSSL-Win32" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (programFilesX86 != null) {
				commonPaths.add(programFilesX86 + File.separator + "OpenSSL-Win32" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (programData != null) {
				commonPaths.add(programData + File.separator + "OpenSSL-Win64" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				commonPaths.add(programData + File.separator + "OpenSSL-Win32" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (localAppData != null) {
				commonPaths.add(localAppData + File.separator + "Programs" + File.separator + "OpenSSL" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			if (commonProgramFiles != null) {
				commonPaths.add(commonProgramFiles + File.separator + "OpenSSL-Win64" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (commonProgramFilesX86 != null) {
				commonPaths.add(commonProgramFilesX86 + File.separator + "OpenSSL-Win32" + File.separator + "bin" + File.separator + "openssl.exe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			// Buscar en las rutas construidas dinámicamente
			for (final String path : commonPaths) {
				if (path != null && new File(path).exists() && isOpenSslAvailable(path)) {
					LOGGER.info("OpenSSL encontrado en ubicación del sistema: " + path); //$NON-NLS-1$
					return path;
				}
			}
		}
		
		LOGGER.warning("OpenSSL no encontrado en ninguna ubicación conocida"); //$NON-NLS-1$
		return null;
	}
	
	/** Verifica si OpenSSL está disponible ejecutando un comando de prueba.
	 * @param opensslPath Ruta a openssl o "openssl" si está en el PATH
	 * @return true si OpenSSL está disponible, false en caso contrario */
	private static boolean isOpenSslAvailable(final String opensslPath) {
		try {
			final ProcessBuilder pb = new ProcessBuilder(opensslPath, "version"); //$NON-NLS-1$
			pb.redirectErrorStream(true);
			final Process process = pb.start();
			
			// Leer la salida
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				final String line = reader.readLine();
				if (line != null && line.contains("OpenSSL")) { //$NON-NLS-1$
					final int exitCode = process.waitFor();
					return exitCode == 0;
				}
			}
		}
		catch (final Exception e) {
			LOGGER.fine("OpenSSL no disponible en: " + opensslPath + " - " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return false;
	}
	
	/** Elimina un directorio y todo su contenido de forma recursiva.
	 * @param directory Directorio a eliminar
	 * @return true si se eliminó correctamente, false en caso contrario */
	private static boolean deleteDirectory(final File directory) {
		if (!directory.exists()) {
			return true;
		}
		
		// Primero eliminar todos los archivos dentro del directorio
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isDirectory()) {
					if (!deleteDirectory(file)) {
						return false;
					}
				}
				else {
					boolean deleted = false;
					int attempts = 0;
					while (!deleted && attempts < 5) {
						try {
							deleted = file.delete();
							if (!deleted) {
								attempts++;
								Thread.sleep(500);
							}
						}
						catch (final InterruptedException e) {
							Thread.currentThread().interrupt();
							return false;
						}
					}
					if (!deleted) {
						LOGGER.warning("No se pudo eliminar el archivo: " + file.getAbsolutePath()); //$NON-NLS-1$
						return false;
					}
				}
			}
		}
		
		// Luego eliminar el directorio mismo
		boolean deleted = false;
		int attempts = 0;
		while (!deleted && attempts < 5) {
			try {
				deleted = directory.delete();
				if (!deleted) {
					attempts++;
					Thread.sleep(500);
				}
			}
			catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		
		return deleted;
	}
	
	/** Marca un directorio y todo su contenido para eliminación al salir de la aplicación.
	 * @param directory Directorio a marcar */
	private static void deleteDirectoryOnExit(final File directory) {
		if (!directory.exists()) {
			return;
		}
		
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isDirectory()) {
					deleteDirectoryOnExit(file);
				}
				else {
					file.deleteOnExit();
				}
			}
		}
		directory.deleteOnExit();
	}
	
	/** Abre el explorador de archivos en la ubicación del archivo especificado.
	 * @param file Archivo cuya ubicación se mostrará */
	private static void openFileLocation(final File file) {
		if (!file.exists()) {
			LOGGER.warning("No se puede abrir la ubicación: el archivo no existe: " + file.getAbsolutePath()); //$NON-NLS-1$
			return;
		}
		
		try {
			if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
				// En Windows, usar explorer /select para seleccionar el archivo
				final ProcessBuilder pb = new ProcessBuilder(
					"explorer", "/select,", "\"" + file.getAbsolutePath() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				);
				pb.start();
				LOGGER.info("Explorador de Windows abierto en la ubicación del archivo"); //$NON-NLS-1$
			}
			else if (Platform.OS.MACOSX.equals(Platform.getOS())) {
				// En macOS, usar open -R para revelar el archivo en Finder
				final ProcessBuilder pb = new ProcessBuilder(
					"open", "-R", file.getAbsolutePath() //$NON-NLS-1$ //$NON-NLS-2$
				);
				pb.start();
				LOGGER.info("Finder abierto en la ubicación del archivo"); //$NON-NLS-1$
			}
			else {
				// En Linux y otros sistemas, abrir el directorio padre
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(file.getParentFile());
						LOGGER.info("Gestor de archivos abierto en la ubicación del archivo"); //$NON-NLS-1$
					}
					catch (final IOException e) {
						LOGGER.warning("Error al abrir el gestor de archivos: " + e.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}
		catch (final IOException e) {
			LOGGER.severe("Error al abrir la ubicación del archivo: " + e.getMessage()); //$NON-NLS-1$
		}
	}
	
	/** Reinicia la aplicación. */
	private static void restartApplication() {
		File currentFile;
		try {
			currentFile = new File(EfirmaSatConverterDialog.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		}
		catch (final Exception e) {
			LOGGER.warning("No se ha podido identificar el fichero ejecutable: " + e.getMessage()); //$NON-NLS-1$
			return;
		}
		
		// Compone el comando necesario para arrancar la aplicación
		final List<String> command = getRestartCommand(currentFile);
		
		// Ejecutamos una nueva instancia de la aplicación
		if (command != null) {
			try {
				new ProcessBuilder(command).start();
				LOGGER.info("Nueva instancia de la aplicación iniciada"); //$NON-NLS-1$
			}
			catch (final Exception e) {
				LOGGER.warning("No se ha podido arrancar la nueva instancia de la aplicación: " + e.getMessage()); //$NON-NLS-1$
				return;
			}
			
			// Salimos de la aplicación antes de que se llegue a cargar la nueva instancia
			try {
				Thread.sleep(500); // Dar tiempo para que se inicie el nuevo proceso
			}
			catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			System.exit(0);
		}
		else {
			LOGGER.warning("No se pudo construir el comando para reiniciar la aplicación"); //$NON-NLS-1$
		}
	}
	
	/** Devuelve el comando necesario para ejecutar la aplicación o null si no hay una forma efectiva de ejecutarla.
	 * @param currentFile Fichero o directorio con la aplicación.
	 * @return Parámetros para la ejecución de la aplicación. */
	private static List<String> getRestartCommand(final File currentFile) {
		// La aplicación se ejecuta desde clases Java. No va a poder ejecutarse sin las
		// dependencias, por lo que se omite
		if (currentFile.isDirectory()) {
			return null;
		}
		
		// La aplicación se ejecuta desde un JAR
		List<String> command;
		if (currentFile.getName().toLowerCase().endsWith(".jar")) { //$NON-NLS-1$
			// Si ese JAR forma parte de un ejecutable macOS, usamos el ejecutable
			final File appMac = MacUtils.getMacApp(currentFile);
			if (appMac != null && appMac.isFile()) {
				command = new ArrayList<>();
				command.add(appMac.getAbsolutePath());
			}
			else {
				final String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				command = new ArrayList<>();
				command.add(java);
				command.add("-jar"); //$NON-NLS-1$
				command.add(currentFile.getPath());
			}
		}
		// La aplicación es un ejecutable de Windows
		else if (currentFile.getName().toLowerCase().endsWith(".exe")) { //$NON-NLS-1$
			command = new ArrayList<>();
			command.add(currentFile.getPath());
		}
		// En cualquier otro caso, no reiniciamos
		else {
			command = null;
		}
		
		return command;
	}
	
	/** Busca el archivo CA.pem en media/cert.
	 * @return Archivo CA.pem o null si no se encuentra */
	private static File findCaPemFile() {
		// Buscar en varias ubicaciones posibles:
		// 1. Relativo al directorio de trabajo actual (user.dir)
		final String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
		if (userDir != null) {
			final File caFile = new File(userDir, "media" + File.separator + "cert" + File.separator + "CA.pem"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (caFile.exists() && caFile.isFile()) {
				LOGGER.info("CA.pem encontrado en: " + caFile.getAbsolutePath()); //$NON-NLS-1$
				return caFile;
			}
		}
		
		// 2. Relativo al directorio de la aplicación
		final File appDir = DesktopUtil.getApplicationDirectory();
		if (appDir != null) {
			// Intentar en el mismo directorio que el JAR
			File caFile = new File(appDir, "media" + File.separator + "cert" + File.separator + "CA.pem"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (caFile.exists() && caFile.isFile()) {
				LOGGER.info("CA.pem encontrado en: " + caFile.getAbsolutePath()); //$NON-NLS-1$
				return caFile;
			}
			// Intentar en el directorio padre
			if (appDir.getParentFile() != null) {
				caFile = new File(appDir.getParentFile(), "media" + File.separator + "cert" + File.separator + "CA.pem"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (caFile.exists() && caFile.isFile()) {
					LOGGER.info("CA.pem encontrado en: " + caFile.getAbsolutePath()); //$NON-NLS-1$
					return caFile;
				}
			}
		}
		
		LOGGER.warning("CA.pem no encontrado en ninguna ubicacion conocida"); //$NON-NLS-1$
		return null;
	}
	
	/** Valida el certificado PEM contra el CA.pem usando OpenSSL.
	 * @param opensslPath Ruta a OpenSSL
	 * @param certPemFile Archivo del certificado en formato PEM
	 * @param caPemFile Archivo CA.pem (puede ser null si no existe)
	 * @throws IOException Si la validación falla o el CA.pem no existe */
	private static void validateCertificateAgainstCA(final String opensslPath, final File certPemFile, final File caPemFile) throws IOException {
		if (caPemFile == null || !caPemFile.exists()) {
			final String warningMessage = "ADVERTENCIA: No se encontro el archivo CA.pem en media/cert.\n" + //$NON-NLS-1$
					"El certificado generado puede no ser valido sin la cadena de certificados completa.\n" + //$NON-NLS-1$
					"Se recomienda tener el archivo CA.pem en media/cert para garantizar la validez del certificado."; //$NON-NLS-1$
			LOGGER.warning(warningMessage); //$NON-NLS-1$
			
			// Mostrar advertencia al usuario
			final int result = AOUIFactory.showConfirmDialog(
				null,
				warningMessage + "\n\n¿Desea continuar con la conversion de todas formas?", //$NON-NLS-1$
				"Advertencia: CA.pem no encontrado", //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			
			if (result != JOptionPane.YES_OPTION) {
				throw new IOException("Conversion cancelada por el usuario debido a la ausencia del archivo CA.pem"); //$NON-NLS-1$
			}
			return; // Continuar sin validación
		}
		
		// Validar el certificado contra el CA usando OpenSSL
		LOGGER.info("Validando certificado contra CA.pem: " + caPemFile.getAbsolutePath()); //$NON-NLS-1$
		
		try {
			final ProcessBuilder pb = new ProcessBuilder(
				opensslPath,
				"verify", //$NON-NLS-1$
				"-CAfile", caPemFile.getAbsolutePath(), //$NON-NLS-1$
				certPemFile.getAbsolutePath() //$NON-NLS-1$
			);
			pb.redirectErrorStream(true);
			final Process process = pb.start();
			
			final StringBuilder output = new StringBuilder();
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					LOGGER.info("OpenSSL verify: " + line); //$NON-NLS-1$
					output.append(line).append("\n"); //$NON-NLS-1$
				}
			}
			
			final int exitCode = process.waitFor();
			final String outputStr = output.toString();
			
			if (exitCode == 0) {
				// Verificar que la salida contenga "OK"
				if (outputStr.contains("OK") || outputStr.contains(": OK")) { //$NON-NLS-1$ //$NON-NLS-2$
					LOGGER.info("Certificado validado exitosamente contra CA.pem"); //$NON-NLS-1$
					return;
				}
			}
			
			// Si llegamos aquí, la validación falló
			final String errorMessage = "El certificado no es valido contra el CA.pem.\n" + //$NON-NLS-1$
					"Salida de OpenSSL:\n" + outputStr; //$NON-NLS-1$
			LOGGER.severe(errorMessage); //$NON-NLS-1$
			
			// Mostrar error al usuario
			final int result = AOUIFactory.showConfirmDialog(
				null,
				errorMessage + "\n\n¿Desea continuar con la conversion de todas formas?\n" + //$NON-NLS-1$
				"ADVERTENCIA: El certificado generado puede no ser valido.", //$NON-NLS-1$
				"Error: Certificado no valido", //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE
			);
			
			if (result != JOptionPane.YES_OPTION) {
				throw new IOException("Conversion cancelada por el usuario: certificado no valido contra CA.pem"); //$NON-NLS-1$
			}
			LOGGER.warning("Usuario decidio continuar a pesar de la validacion fallida"); //$NON-NLS-1$
		}
		catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Proceso de validacion interrumpido", e); //$NON-NLS-1$
		}
		catch (final Exception e) {
			LOGGER.severe("Error durante la validacion del certificado: " + e.getMessage()); //$NON-NLS-1$
			throw new IOException("Error al validar el certificado contra CA.pem: " + e.getMessage(), e); //$NON-NLS-1$
		}
	}
	
	/** Muestra el di&aacute;logo. */
	public void showDialog() {
		setVisible(true);
	}
}

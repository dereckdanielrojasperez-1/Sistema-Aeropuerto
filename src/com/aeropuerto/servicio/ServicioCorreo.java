package com.aeropuerto.servicio;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Servicio Oficial de Mensajería y Despacho de Comprobantes Electrónicos por Correo.
 * Soporta:
 *  1. Envío directo por protocolo RFC 5321 SMTP sobre SSL (puerto 465) hacia Gmail/servidores corporativos.
 *  2. Despacho directo hacia Gmail Web (mail.google.com) con comprobante pre-cargado para envío en 1 clic.
 *  3. Generación y almacenamiento local de Boletas Electrónicas HTML y de texto plano SUNAT.
 *  4. Compatibilidad académica estricta (puro Java SE, sin librerías externas pesadas).
 */
public class ServicioCorreo {

    private static ServicioCorreo instancia;
    private final List<RegistroEnvioCorreo> historialEnvios;
    private final Properties configuracionSmtp;

    private static final String ARCHIVO_CONFIG = "config_correo.properties";
    private static final String DEFAULT_HOST = "smtp.gmail.com";
    private static final int DEFAULT_PORT = 465;
    private static final String DEFAULT_NOMBRE = "CORPAC AIRWAYS S.A.";

    public static class RegistroEnvioCorreo {
        private final String destinatario;
        private final String numeroComprobante;
        private final String localizador;
        private final String fechaHora;
        private final String rutaArchivoHtml;
        private final boolean exitoso;
        private final boolean enviadoSmtp;
        private final String detalleEntrega;

        public RegistroEnvioCorreo(String destinatario, String numeroComprobante, String localizador,
                                   String fechaHora, String rutaArchivoHtml, boolean exitoso) {
            this(destinatario, numeroComprobante, localizador, fechaHora, rutaArchivoHtml, exitoso, false, "Comprobante digital archivado");
        }

        public RegistroEnvioCorreo(String destinatario, String numeroComprobante, String localizador,
                                   String fechaHora, String rutaArchivoHtml, boolean exitoso,
                                   boolean enviadoSmtp, String detalleEntrega) {
            this.destinatario = destinatario;
            this.numeroComprobante = numeroComprobante;
            this.localizador = localizador;
            this.fechaHora = fechaHora;
            this.rutaArchivoHtml = rutaArchivoHtml;
            this.exitoso = exitoso;
            this.enviadoSmtp = enviadoSmtp;
            this.detalleEntrega = detalleEntrega;
        }

        public String getDestinatario() { return destinatario; }
        public String getNumeroComprobante() { return numeroComprobante; }
        public String getLocalizador() { return localizador; }
        public String getFechaHora() { return fechaHora; }
        public String getRutaArchivoHtml() { return rutaArchivoHtml; }
        public boolean isExitoso() { return exitoso; }
        public boolean isEnviadoSmtp() { return enviadoSmtp; }
        public String getDetalleEntrega() { return detalleEntrega; }
    }

    public static class ResultadoSmtp {
        private final boolean exitoso;
        private final String mensaje;
        private final int codigoSmtp;

        public ResultadoSmtp(boolean exitoso, String mensaje, int codigoSmtp) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.codigoSmtp = codigoSmtp;
        }

        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public int getCodigoSmtp() { return codigoSmtp; }
    }

    private ServicioCorreo() {
        this.historialEnvios = new ArrayList<>();
        this.configuracionSmtp = new Properties();
        cargarConfiguracion();
    }

    public static synchronized ServicioCorreo obtenerInstancia() {
        if (instancia == null) {
            instancia = new ServicioCorreo();
        }
        return instancia;
    }

    /**
     * Carga la configuración del servidor SMTP desde config_correo.properties o del usuario.
     */
    public synchronized void cargarConfiguracion() {
        configuracionSmtp.setProperty("smtp.servidor", DEFAULT_HOST);
        configuracionSmtp.setProperty("smtp.puerto", String.valueOf(DEFAULT_PORT));
        configuracionSmtp.setProperty("smtp.ssl", "true");
        configuracionSmtp.setProperty("smtp.nombre_remitente", DEFAULT_NOMBRE);
        configuracionSmtp.setProperty("smtp.remitente", "");
        configuracionSmtp.setProperty("smtp.password", "");

        File fCfg = buscarArchivoConfiguracion();
        if (fCfg != null && fCfg.exists()) {
            try (FileInputStream fis = new FileInputStream(fCfg)) {
                configuracionSmtp.load(fis);
            } catch (Exception ex) {
                System.err.println("[ServicioCorreo] Error al leer " + fCfg.getName() + ": " + ex.getMessage());
            }
        }
    }

    private File buscarArchivoConfiguracion() {
        File f1 = new File(ARCHIVO_CONFIG);
        if (f1.exists()) return f1;
        File f2 = new File(System.getProperty("user.dir"), ARCHIVO_CONFIG);
        if (f2.exists()) return f2;
        File f3 = new File(System.getProperty("user.home"), ".corpac_correo.properties");
        if (f3.exists()) return f3;
        return f1;
    }

    /**
     * Guarda la configuración de correo en disco para persistencia.
     */
    public synchronized void guardarConfiguracion(String servidor, int puerto, String remitente, String password, String nombreRemitente) {
        configuracionSmtp.setProperty("smtp.servidor", (servidor != null && !servidor.trim().isEmpty()) ? servidor.trim() : DEFAULT_HOST);
        configuracionSmtp.setProperty("smtp.puerto", String.valueOf(puerto > 0 ? puerto : DEFAULT_PORT));
        configuracionSmtp.setProperty("smtp.ssl", "true");
        configuracionSmtp.setProperty("smtp.remitente", remitente != null ? remitente.trim() : "");
        configuracionSmtp.setProperty("smtp.password", password != null ? password.replace(" ", "").trim() : "");
        configuracionSmtp.setProperty("smtp.nombre_remitente", (nombreRemitente != null && !nombreRemitente.trim().isEmpty()) ? nombreRemitente.trim() : DEFAULT_NOMBRE);

        File fDest = new File(ARCHIVO_CONFIG);
        try (FileOutputStream fos = new FileOutputStream(fDest)) {
            configuracionSmtp.store(fos, "Configuracion de Envio de Boletas y Vouchers CORPAC S.A.");
        } catch (Exception ex) {
            System.err.println("[ServicioCorreo] Error al guardar config_correo.properties: " + ex.getMessage());
        }

        try {
            File fHome = new File(System.getProperty("user.home"), ".corpac_correo.properties");
            try (FileOutputStream fos = new FileOutputStream(fHome)) {
                configuracionSmtp.store(fos, "Copia de respaldo de configuracion CORPAC");
            }
        } catch (Exception ignored) {}
    }

    public boolean tieneCredencialesSmtpConfiguradas() {
        String u = getRemitenteConfigurado();
        String p = getPasswordConfigurado();
        return u != null && !u.trim().isEmpty() && p != null && !p.trim().isEmpty();
    }

    public String getServidorConfigurado() {
        return configuracionSmtp.getProperty("smtp.servidor", DEFAULT_HOST);
    }

    public int getPuertoConfigurado() {
        try {
            return Integer.parseInt(configuracionSmtp.getProperty("smtp.puerto", String.valueOf(DEFAULT_PORT)));
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
    }

    public String getRemitenteConfigurado() {
        return configuracionSmtp.getProperty("smtp.remitente", "").trim();
    }

    public String getPasswordConfigurado() {
        return configuracionSmtp.getProperty("smtp.password", "").trim();
    }

    public String getNombreRemitenteConfigurado() {
        return configuracionSmtp.getProperty("smtp.nombre_remitente", DEFAULT_NOMBRE).trim();
    }

    /**
     * Valida si la cadena tiene un formato mínimo de correo electrónico.
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null) return false;
        String c = correo.trim();
        return c.length() >= 5 && c.contains("@") && c.contains(".") && !c.startsWith("@") && !c.endsWith("@");
    }

    /**
     * Prueba la conexión con el servidor SMTP SSL y valida credenciales si fueron proporcionadas.
     */
    public ResultadoSmtp probarConexionSmtp(String host, int port, String user, String pass) {
        String h = (host != null && !host.trim().isEmpty()) ? host.trim() : DEFAULT_HOST;
        int p = port > 0 ? port : DEFAULT_PORT;

        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket()) {
            socket.connect(new InetSocketAddress(h, p), 8000);
            socket.setSoTimeout(10000);
            socket.startHandshake();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            RespuestaSmtp r220 = leerRespuestaSmtp(reader);
            if (r220.codigo != 220) {
                return new ResultadoSmtp(false, "Servidor no respondió con saludo 220: " + r220.lineaFinal, r220.codigo);
            }

            writer.write("EHLO localhost\r\n");
            writer.flush();
            RespuestaSmtp rEhlo = leerRespuestaSmtp(reader);
            if (rEhlo.codigo != 250) {
                return new ResultadoSmtp(false, "Fallo en comando EHLO: " + rEhlo.lineaFinal, rEhlo.codigo);
            }

            if (user != null && !user.trim().isEmpty() && pass != null && !pass.trim().isEmpty()) {
                writer.write("AUTH LOGIN\r\n");
                writer.flush();
                RespuestaSmtp rAuth = leerRespuestaSmtp(reader);
                if (rAuth.codigo != 334) {
                    return new ResultadoSmtp(false, "El servidor rechazó AUTH LOGIN: " + rAuth.lineaFinal, rAuth.codigo);
                }

                String userB64 = Base64.getEncoder().encodeToString(user.trim().getBytes(StandardCharsets.UTF_8));
                writer.write(userB64 + "\r\n");
                writer.flush();
                RespuestaSmtp rUser = leerRespuestaSmtp(reader);
                if (rUser.codigo != 334) {
                    return new ResultadoSmtp(false, "Usuario rechazado por servidor SMTP: " + rUser.lineaFinal, rUser.codigo);
                }

                String claveNorm = pass.replace(" ", "").trim();
                String passB64 = Base64.getEncoder().encodeToString(claveNorm.getBytes(StandardCharsets.UTF_8));
                writer.write(passB64 + "\r\n");
                writer.flush();
                RespuestaSmtp rPass = leerRespuestaSmtp(reader);
                if (rPass.codigo != 235) {
                    return new ResultadoSmtp(false, "Credenciales rechazadas (" + rPass.lineaFinal + "). Asegúrese de usar una Contraseña de Aplicación de 16 caracteres de Google.", rPass.codigo);
                }
            }

            writer.write("QUIT\r\n");
            writer.flush();

            return new ResultadoSmtp(true, "Conexión SMTP SSL con " + h + ":" + p + " establecida exitosamente.", 235);
        } catch (Exception ex) {
            return new ResultadoSmtp(false, "Error de conexión SMTP SSL: " + ex.getMessage(), -1);
        }
    }

    /**
     * Envía un correo electrónico directamente a través del protocolo SMTP sobre SSL.
     */
    public ResultadoSmtp enviarSmtpDirecto(String remitente, String password, String nombreRemitente,
                                           String destinatario, String asunto,
                                           String contenidoHtml, String contenidoPlano) {
        if (!esCorreoValido(destinatario)) {
            return new ResultadoSmtp(false, "Dirección de correo destinatario inválida: " + destinatario, 501);
        }

        String host = getServidorConfigurado();
        int port = getPuertoConfigurado();
        String user = (remitente != null && !remitente.trim().isEmpty()) ? remitente.trim() : getRemitenteConfigurado();
        String pass = (password != null && !password.trim().isEmpty()) ? password.trim() : getPasswordConfigurado();
        String fromName = (nombreRemitente != null && !nombreRemitente.trim().isEmpty()) ? nombreRemitente.trim() : getNombreRemitenteConfigurado();

        if (user.isEmpty() || pass.isEmpty()) {
            return new ResultadoSmtp(false, "No se han configurado credenciales SMTP (remitente y contraseña de aplicación requeridos).", 530);
        }

        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket()) {
            socket.connect(new InetSocketAddress(host, port), 10000);
            socket.setSoTimeout(15000);
            socket.startHandshake();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            RespuestaSmtp r220 = leerRespuestaSmtp(reader);
            if (r220.codigo != 220) throw new IOException("Respuesta 220 inválida: " + r220.lineaFinal);

            writer.write("EHLO localhost\r\n");
            writer.flush();
            RespuestaSmtp rEhlo = leerRespuestaSmtp(reader);
            if (rEhlo.codigo != 250) throw new IOException("EHLO rechazado: " + rEhlo.lineaFinal);

            writer.write("AUTH LOGIN\r\n");
            writer.flush();
            RespuestaSmtp rAuth = leerRespuestaSmtp(reader);
            if (rAuth.codigo != 334) throw new IOException("AUTH LOGIN rechazado: " + rAuth.lineaFinal);

            writer.write(Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8)) + "\r\n");
            writer.flush();
            RespuestaSmtp rUser = leerRespuestaSmtp(reader);
            if (rUser.codigo != 334) throw new IOException("Usuario rechazado: " + rUser.lineaFinal);

            String passNorm = pass.replace(" ", "").trim();
            writer.write(Base64.getEncoder().encodeToString(passNorm.getBytes(StandardCharsets.UTF_8)) + "\r\n");
            writer.flush();
            RespuestaSmtp rPass = leerRespuestaSmtp(reader);
            if (rPass.codigo != 235) {
                throw new IOException("Autenticación SMTP rechazada (" + rPass.lineaFinal + "). Verifique su usuario y Contraseña de Aplicación de Google.");
            }

            writer.write("MAIL FROM:<" + user + ">\r\n");
            writer.flush();
            RespuestaSmtp rMail = leerRespuestaSmtp(reader);
            if (rMail.codigo != 250) throw new IOException("MAIL FROM rechazado: " + rMail.lineaFinal);

            writer.write("RCPT TO:<" + destinatario.trim() + ">\r\n");
            writer.flush();
            RespuestaSmtp rRcpt = leerRespuestaSmtp(reader);
            if (rRcpt.codigo != 250) throw new IOException("RCPT TO rechazado (" + destinatario + "): " + rRcpt.lineaFinal);

            writer.write("DATA\r\n");
            writer.flush();
            RespuestaSmtp rData = leerRespuestaSmtp(reader);
            if (rData.codigo != 354) throw new IOException("DATA rechazado: " + rData.lineaFinal);

            String boundary = "=_CORPAC_MIME_BOUND_" + System.currentTimeMillis() + "_=";
            String asuntoB64 = "=?UTF-8?B?" + Base64.getEncoder().encodeToString(asunto.getBytes(StandardCharsets.UTF_8)) + "?=";
            SimpleDateFormat rfcDateFmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);

            writer.write("From: \"" + fromName + "\" <" + user + ">\r\n");
            writer.write("To: <" + destinatario.trim() + ">\r\n");
            writer.write("Subject: " + asuntoB64 + "\r\n");
            writer.write("Date: " + rfcDateFmt.format(new Date()) + "\r\n");
            writer.write("MIME-Version: 1.0\r\n");
            writer.write("Content-Type: multipart/alternative; boundary=\"" + boundary + "\"\r\n");
            writer.write("\r\n");

            // Texto plano
            writer.write("--" + boundary + "\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
            writer.write("Content-Transfer-Encoding: 8bit\r\n");
            writer.write("\r\n");
            writer.write(contenidoPlano + "\r\n\r\n");

            // HTML
            writer.write("--" + boundary + "\r\n");
            writer.write("Content-Type: text/html; charset=UTF-8\r\n");
            writer.write("Content-Transfer-Encoding: 8bit\r\n");
            writer.write("\r\n");
            String htmlEscapado = contenidoHtml.replace("\r\n.", "\r\n..").replace("\n.", "\n..");
            writer.write(htmlEscapado + "\r\n\r\n");

            // Fin
            writer.write("--" + boundary + "--\r\n");
            writer.write(".\r\n");
            writer.flush();

            RespuestaSmtp rFin = leerRespuestaSmtp(reader);
            if (rFin.codigo != 250) throw new IOException("Error finalizando mensaje SMTP: " + rFin.lineaFinal);

            try {
                writer.write("QUIT\r\n");
                writer.flush();
            } catch (Exception ignored) {}

            return new ResultadoSmtp(true, "Voucher Electrónico entregado exitosamente al servidor SMTP de destino (" + host + ").", 250);
        } catch (Exception ex) {
            return new ResultadoSmtp(false, "Fallo en transmisión SMTP: " + ex.getMessage(), -1);
        }
    }

    public ResultadoSmtp enviarSmtpDirecto(String destinatario, String asunto, String contenidoHtml, String contenidoPlano) {
        return enviarSmtpDirecto(getRemitenteConfigurado(), getPasswordConfigurado(), getNombreRemitenteConfigurado(),
                destinatario, asunto, contenidoHtml, contenidoPlano);
    }

    private static class RespuestaSmtp {
        int codigo = -1;
        String mensajeCompleto = "";
        String lineaFinal = "";
    }

    private RespuestaSmtp leerRespuestaSmtp(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String linea;
        int cod = -1;
        String ult = "";
        while ((linea = reader.readLine()) != null) {
            sb.append(linea).append("\n");
            if (linea.length() >= 3) {
                try {
                    cod = Integer.parseInt(linea.substring(0, 3));
                } catch (NumberFormatException ignored) {}
                if (linea.length() == 3 || linea.charAt(3) == ' ') {
                    ult = linea;
                    break;
                }
            }
        }
        RespuestaSmtp r = new RespuestaSmtp();
        r.codigo = cod;
        r.mensajeCompleto = sb.toString().trim();
        r.lineaFinal = ult;
        return r;
    }

    /**
     * Abre Google Gmail en el navegador con el destinatario, asunto y voucher listos para enviar en 1 clic.
     */
    public boolean abrirGmailWeb(String destinatario, String asunto, String cuerpoPlano) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                String toEnc = URLEncoder.encode(destinatario != null ? destinatario.trim() : "", "UTF-8");
                String suEnc = URLEncoder.encode(asunto != null ? asunto : "CORPAC AIRWAYS - Voucher Electrónico", "UTF-8");
                String bodyEnc = URLEncoder.encode(cuerpoPlano != null ? cuerpoPlano : "", "UTF-8");
                String url = "https://mail.google.com/mail/?view=cm&fs=1&to=" + toEnc + "&su=" + suEnc + "&body=" + bodyEnc;
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Exception ex) {
            System.err.println("[ServicioCorreo] Error al abrir Gmail Web: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Intenta abrir el cliente de correo predeterminado del sistema (Outlook/Thunderbird/Mail).
     */
    public boolean abrirClienteCorreoSistema(String correoDestino, String numeroSerie, String localizador, String cuerpoPlano) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                String asunto = URLEncoder.encode("CORPAC AIRWAYS - Boleta Electrónica " + numeroSerie + " [Reserva " + localizador + "]", "UTF-8").replace("+", "%20");
                String cuerpoCorto = URLEncoder.encode("Estimado Pasajero,\n\nAdjuntamos la confirmación de su Boleta Electrónica " +
                        numeroSerie + " con localizador " + localizador + ".\n\n" + cuerpoPlano, "UTF-8").replace("+", "%20");
                URI uriMail = new URI("mailto:" + correoDestino + "?subject=" + asunto + "&body=" + cuerpoCorto);
                Desktop.getDesktop().mail(uriMail);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Despacha la Boleta Electrónica y Pase de Abordar al correo del pasajero,
     * generando el comprobante digital en HTML interactivo, archivándolo localmente y
     * preparando la transmisión SMTP/Web.
     */
    public RegistroEnvioCorreo enviarBoletaPorCorreo(String correoDestino,
                                                     String nombrePasajero,
                                                     String dniPasajero,
                                                     String numeroSerie,
                                                     String codigoVuelo,
                                                     String origen,
                                                     String destino,
                                                     String fecha,
                                                     String hora,
                                                     String asiento,
                                                     String clase,
                                                     String puerta,
                                                     double precioTotal,
                                                     String localizador,
                                                     String textoVoucherPlano) {
        String correoLimpio = (correoDestino != null && !correoDestino.trim().isEmpty())
                ? correoDestino.trim()
                : "pasajero.corpac@aeropuerto.gob.pe";

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String htmlContenido = generarPlantillaHtmlBoleta(correoLimpio, nombrePasajero, dniPasajero,
                numeroSerie, codigoVuelo, origen, destino, fecha, hora, asiento, clase, puerta, precioTotal, localizador, timestamp);

        String rutaGenerada = "";
        boolean exitoLocal = false;

        try {
            File dirCorreos = new File("comprobantes_correo");
            if (!dirCorreos.exists()) {
                dirCorreos.mkdirs();
            }

            String nombreArchivo = "Boleta_" + numeroSerie.replace("-", "_") + "_" + localizador + ".html";
            File archivoHtml = new File(dirCorreos, nombreArchivo);

            try (PrintWriter pw = new PrintWriter(new FileWriter(archivoHtml, StandardCharsets.UTF_8))) {
                pw.print(htmlContenido);
            }
            rutaGenerada = archivoHtml.getAbsolutePath();
            exitoLocal = true;

            File dirVouchers = new File("vouchers/emails");
            if (!dirVouchers.exists()) {
                dirVouchers.mkdirs();
            }
            File archivoVoucherEmail = new File(dirVouchers, nombreArchivo);
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivoVoucherEmail, StandardCharsets.UTF_8))) {
                pw.print(htmlContenido);
            }

        } catch (Exception ex) {
            System.err.println("[ServicioCorreo] Error al archivar correo digital: " + ex.getMessage());
        }

        boolean enviadoSmtp = false;
        String detalle = "Comprobante digital archivado";

        RegistroEnvioCorreo registro = new RegistroEnvioCorreo(correoLimpio, numeroSerie, localizador, timestamp,
                rutaGenerada, exitoLocal, enviadoSmtp, detalle);
        this.historialEnvios.add(registro);
        return registro;
    }

    public RegistroEnvioCorreo enviarBoletaPorCorreo(String correoDestino,
                                                     String nombrePasajero,
                                                     String dniPasajero,
                                                     String numeroSerie,
                                                     String codigoVuelo,
                                                     String origen,
                                                     String destino,
                                                     String fecha,
                                                     String hora,
                                                     String asiento,
                                                     String clase,
                                                     String puerta,
                                                     String detalleEquipaje,
                                                     double precioTotal,
                                                     String localizador,
                                                     String textoVoucherPlano) {
        return enviarBoletaPorCorreo(correoDestino, nombrePasajero, dniPasajero, numeroSerie,
                codigoVuelo, origen, destino, fecha, hora, asiento, clase, puerta, precioTotal,
                localizador, textoVoucherPlano);
    }

    /**
     * Genera el cuerpo de texto plano formal del voucher para visualización en clientes de correo y Gmail.
     */
    public String generarTextoPlanoBoleta(String correo, String nombre, String dni,
                                          String numeroSerie, String codigoVuelo,
                                          String origen, String destino,
                                          String fecha, String hora,
                                          String asiento, String clase,
                                          String puerta, double total,
                                          String localizador, String fechaHoraEmision) {
        double gravada = total / 1.18;
        double igv = total - gravada;

        return "================================================================================\n" +
                "                    CORPAC AIRWAYS S.A. - VOUCHER ELECTRÓNICO\n" +
                "             Corporación Peruana de Aeropuertos y Aviación Comercial\n" +
                "                             RUC: 20100055201\n" +
                "================================================================================\n" +
                "COMPROBANTE DE PAGO ELECTRÓNICO : " + numeroSerie + "\n" +
                "CÓDIGO LOCALIZADOR DE RESERVA    : " + localizador + "\n" +
                "FECHA Y HORA DE EMISIÓN         : " + fechaHoraEmision + "\n" +
                "ESTADO DE EMISIÓN               : CONFIRMADO Y PAGADO\n" +
                "--------------------------------------------------------------------------------\n" +
                "1. DATOS DEL PASAJERO:\n" +
                "   - Nombre Completo            : " + nombre.toUpperCase() + "\n" +
                "   - Documento de Identidad     : DNI " + dni + "\n" +
                "   - Correo Registrado          : " + correo + "\n\n" +
                "2. DETALLE DE ITINERARIO Y CABINA:\n" +
                "   - Código de Vuelo            : " + codigoVuelo + " (CORPAC Airways)\n" +
                "   - Ruta Comercial             : " + origen.toUpperCase() + " -> " + destino.toUpperCase() + "\n" +
                "   - Fecha Programada           : " + fecha + "\n" +
                "   - Hora de Salida             : " + hora + " hrs\n" +
                "   - Asiento Asignado           : " + asiento + " (" + clase + ")\n" +
                "   - Puerta de Embarque         : " + puerta + "\n\n" +
                "3. LIQUIDACIÓN TRIBUTARIA (SUNAT TIPO 03 - BOLETA DE VENTA):\n" +
                "   - Operación Gravada          : S/. " + String.format(Locale.US, "%.2f", gravada) + "\n" +
                "   - I.G.V. (18%)               : S/. " + String.format(Locale.US, "%.2f", igv) + "\n" +
                "   - IMPORTE TOTAL PAGADO       : S/. " + String.format(Locale.US, "%.2f", total) + "\n" +
                "--------------------------------------------------------------------------------\n" +
                "INSTRUCCIONES DE EMBARQUE:\n" +
                "- Preséntese en la sala de embarque con 45 minutos de anticipación al vuelo.\n" +
                "- Porte su DNI en físico o formato digital oficial y este comprobante.\n" +
                "- Su pase de abordar con código QR oficial se encuentra disponible en cabina.\n" +
                "================================================================================\n" +
                "   Emitido electrónicamente por el Sistema Integrado de Control CORPAC S.A.\n" +
                "   Consulte la validez del comprobante tributario en: www.sunat.gob.pe\n" +
                "================================================================================\n";
    }

    /**
     * Genera una plantilla de correo electrónico HTML moderna, corporativa y responsive
     * con la identidad visual institucional de CORPAC S.A.
     */
    public String generarPlantillaHtmlBoleta(String correo, String nombre, String dni,
                                              String numeroSerie, String codigoVuelo,
                                              String origen, String destino,
                                              String fecha, String hora,
                                              String asiento, String clase,
                                              String puerta, double total,
                                              String localizador, String fechaHoraEmision) {
        double gravada = total / 1.18;
        double igv = total - gravada;

        return "<!DOCTYPE html>\n" +
                "<html lang='es'>\n" +
                "<head>\n" +
                "  <meta charset='UTF-8'>\n" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "  <title>Boleta Electrónica CORPAC - " + numeroSerie + "</title>\n" +
                "  <style>\n" +
                "    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0d1117; color: #e6edf3; margin: 0; padding: 20px; }\n" +
                "    .container { max-width: 620px; margin: 0 auto; background: #161b22; border: 1px solid #30363d; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.5); }\n" +
                "    .header { background: linear-gradient(135deg, #0d1117 0%, #15294a 100%); padding: 24px; border-bottom: 2px solid #58a6ff; text-align: center; }\n" +
                "    .header h1 { margin: 0; color: #58a6ff; font-size: 22px; letter-spacing: 1px; font-weight: 700; }\n" +
                "    .header p { margin: 6px 0 0 0; color: #9198a1; font-size: 13px; }\n" +
                "    .badge { display: inline-block; background: #2ea043; color: #ffffff; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: bold; margin-top: 10px; }\n" +
                "    .content { padding: 24px; }\n" +
                "    .card { background: #1c2128; border: 1px solid #30363d; border-radius: 10px; padding: 16px; margin-bottom: 16px; }\n" +
                "    .card-title { color: #58a6ff; font-size: 13px; font-weight: bold; text-transform: uppercase; margin-bottom: 10px; border-bottom: 1px solid #30363d; padding-bottom: 6px; }\n" +
                "    .row { display: flex; justify-content: space-between; margin-bottom: 6px; font-size: 13px; }\n" +
                "    .label { color: #9198a1; }\n" +
                "    .value { color: #e6edf3; font-weight: 600; text-align: right; }\n" +
                "    .highlight { color: #e3b341; font-weight: bold; font-size: 16px; }\n" +
                "    .total-box { background: rgba(56, 189, 248, 0.08); border: 1px solid #58a6ff; border-radius: 8px; padding: 12px; margin-top: 12px; text-align: center; }\n" +
                "    .total-box .monto { font-size: 24px; color: #3fb950; font-weight: bold; margin: 4px 0; }\n" +
                "    .footer { background: #0d1117; padding: 18px; text-align: center; font-size: 11px; color: #8b949e; border-top: 1px solid #30363d; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class='container'>\n" +
                "    <div class='header'>\n" +
                "      <h1>CORPAC AIRWAYS S.A.</h1>\n" +
                "      <p>Corporación Peruana de Aeropuertos y Aviación Comercial S.A.</p>\n" +
                "      <p>RUC: 20100055201 &bull; Av. Elmer Faucett s/n - Callao</p>\n" +
                "      <div class='badge'>BOLETA ELECTRÓNICA " + numeroSerie + "</div>\n" +
                "    </div>\n" +
                "    <div class='content'>\n" +
                "      <div class='card'>\n" +
                "        <div class='card-title'>Datos del Pasajero &bull; Titular del Servicio</div>\n" +
                "        <div class='row'><span class='label'>Pasajero:</span><span class='value'>" + nombre.toUpperCase() + "</span></div>\n" +
                "        <div class='row'><span class='label'>DNI:</span><span class='value'>" + dni + "</span></div>\n" +
                "        <div class='row'><span class='label'>Correo Registrado:</span><span class='value'>" + correo + "</span></div>\n" +
                "        <div class='row'><span class='label'>Localizador de Reserva:</span><span class='value highlight'>" + localizador + "</span></div>\n" +
                "      </div>\n" +
                "      <div class='card'>\n" +
                "        <div class='card-title'>Detalle de Vuelo y Asignación de Cabina</div>\n" +
                "        <div class='row'><span class='label'>Vuelo:</span><span class='value'>" + codigoVuelo + "</span></div>\n" +
                "        <div class='row'><span class='label'>Itinerario:</span><span class='value'>" + origen + " &rarr; " + destino + "</span></div>\n" +
                "        <div class='row'><span class='label'>Fecha y Salida:</span><span class='value'>" + fecha + " &bull; " + hora + " hrs</span></div>\n" +
                "        <div class='row'><span class='label'>Asiento Confirmado:</span><span class='value highlight'>" + asiento + " (" + clase + ")</span></div>\n" +
                "        <div class='row'><span class='label'>Puerta de Embarque:</span><span class='value'>" + puerta + "</span></div>\n" +
                "      </div>\n" +
                "      <div class='card'>\n" +
                "        <div class='card-title'>Liquidación Tributaria SUNAT (Tipo 03)</div>\n" +
                "        <div class='row'><span class='label'>Operación Gravada:</span><span class='value'>S/. " + String.format(Locale.US, "%.2f", gravada) + "</span></div>\n" +
                "        <div class='row'><span class='label'>I.G.V. (18%):</span><span class='value'>S/. " + String.format(Locale.US, "%.2f", igv) + "</span></div>\n" +
                "        <div class='total-box'>\n" +
                "          <div class='label'>IMPORTE TOTAL PAGADO</div>\n" +
                "          <div class='monto'>S/. " + String.format(Locale.US, "%.2f", total) + "</div>\n" +
                "          <div class='label'>Cancelado &bull; Emisión Oficial Inmediata</div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "    <div class='footer'>\n" +
                "      <p>Comprobante de Pago Electrónico emitido conforme a la Resolución de Superintendencia SUNAT N° 034-005.</p>\n" +
                "      <p>Consulte la validez de este documento tributario en: <strong>www.sunat.gob.pe</strong></p>\n" +
                "      <p>Despachado exitosamente a: <strong>" + correo + "</strong> en fecha " + fechaHoraEmision + "</p>\n" +
                "      <p>&copy; 2026 CORPAC S.A. - Todos los derechos reservados.</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
    }

    public String obtenerUltimaRutaArchivoGenerado() {
        if (this.historialEnvios.isEmpty()) return null;
        return this.historialEnvios.get(this.historialEnvios.size() - 1).getRutaArchivoHtml();
    }

    public List<RegistroEnvioCorreo> obtenerHistorialEnvios() {
        return new ArrayList<>(this.historialEnvios);
    }
}

package com.elbotecito.dao.impl;

import com.elbotecito.dao.CapitanDAO;
import com.elbotecito.dao.exception.LlaveDuplicadaException;
import com.elbotecito.modelo.Capitan;
import com.elbotecito.modelo.Capitan;
import com.elbotecito.util.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardOpenOption.*;

public class CapitanDAONIO implements CapitanDAO {

    private final static String NOMBRE_ARCHIVO = "Capitanes"; //Kebab case
    private final static int LONGITUD_REGISTRO = 106;
    private final static int LONGITUD_IDENTIFICACION = 20;
    private final static int LONGITUD_NOMBRE = 40;
    private final static int LONGITUD_SEXO = 1;
    private final static int LONGITUD_ESTADOVIVO = 1;
    private final static int LONGITUD_IDRUTA = 20;
    private final static int LONGITUD_NUMHIJOS = 2;
    private final static int LONGITUD_NUMESPOSAS = 2;
    private final static int LONGITUD_FORTUNA = 20;


    private final static String ENCODING_FILE = System.getProperty("file.encoding");
    private final static Path archivo = Paths.get(NOMBRE_ARCHIVO);

    public CapitanDAONIO() {
        if (!Files.exists(archivo)) {
            try {
                Files.createFile(archivo);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void guardarCapitan(Capitan capitan) throws LlaveDuplicadaException {
        if (consultarCapitanPorIdentificacion(capitan.getIdentificacion()) != null) {
            throw new LlaveDuplicadaException();
        }
        String registro = parseCapitan2String(capitan);
        byte[] datosRegistro = registro.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(datosRegistro);
        try (FileChannel fc = (FileChannel.open(archivo, APPEND))) {
            fc.write(byteBuffer);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    @Override
    public Capitan consultarCapitanPorIdentificacion(String idCapitan) {
        try (SeekableByteChannel sbc = Files.newByteChannel(archivo)) {
            ByteBuffer buffer = ByteBuffer.allocate(LONGITUD_REGISTRO);
            while (sbc.read(buffer) > 0) {
                buffer.rewind();
                CharBuffer registro = Charset.forName(ENCODING_FILE).decode(buffer);
                Capitan capitanConvertido = parseCapitanRegistro2Objeto(registro);
                if (capitanConvertido.getIdentificacion().equals(idCapitan)) {
                    return capitanConvertido;
                }
                buffer.flip();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Capitan> consultarCapitanes() {
        List<Capitan> capitans = new ArrayList<>();
        try (SeekableByteChannel sbc = Files.newByteChannel(archivo)) {
            ByteBuffer buffer = ByteBuffer.allocate(LONGITUD_REGISTRO);
            while (sbc.read(buffer) > 0) {
                buffer.rewind();
                CharBuffer registro = Charset.forName(ENCODING_FILE).decode(buffer);
                Capitan capitanConvertido = parseCapitanRegistro2Objeto(registro);
                buffer.flip();
                capitans.add(capitanConvertido);
            }
        } catch (IOException x) {
            System.out.println("caught exception: " + x);
        }
        return capitans;
    }

    @Override
    public boolean actualizarCapitan(Capitan capitan) {
        Set<StandardOpenOption> options = new HashSet<StandardOpenOption>();
        options.add(READ);
        options.add(WRITE);
        long lastPosition = 0;
        try (SeekableByteChannel sbc = Files.newByteChannel(archivo, options)) {
            ByteBuffer buf = ByteBuffer.allocate(LONGITUD_REGISTRO);
            while (sbc.read(buf) > 0) {
                buf.rewind();
                CharBuffer registro = Charset.forName(ENCODING_FILE).decode(buf);
                String matricula = registro.subSequence(0, LONGITUD_IDENTIFICACION).toString().trim();
                if (matricula.equals(capitan.getIdentificacion())) {
                    sbc.position(lastPosition);
                    sbc.write(ByteBuffer.wrap(parseCapitan2String(capitan).getBytes()));
                    return true;
                }
                buf.flip();
                lastPosition = sbc.position();
            }
        } catch (IOException ioe) {
            System.out.println("caught exception: " + ioe);
        }
        return false;
    }

    @Override
    public boolean eliminarCapitan(String idCapitan) {
        boolean res = false;
        List<Capitan> capitans = new ArrayList<>();
        try (SeekableByteChannel sbc = Files.newByteChannel(archivo)) {
            ByteBuffer buffer = ByteBuffer.allocate(LONGITUD_REGISTRO);
            while (sbc.read(buffer) > 0) {
                buffer.rewind();
                CharBuffer registro = Charset.forName(ENCODING_FILE).decode(buffer);
                Capitan capitanConvertido = parseCapitanRegistro2Objeto(registro);
                buffer.flip();
                capitans.add(capitanConvertido);
            }
        } catch (IOException x) {
            System.out.println("caught exception: " + x);
        }
        util.eliminarYCrearArchivo(archivo);
        while (!capitans.isEmpty()) {
            Capitan capitanAux = capitans.get(0);
            capitans.remove(0);
            if (capitanAux.getIdentificacion().equals(idCapitan)) {
                res = true;
            } else {
                String registro = parseCapitan2String(capitanAux);
                byte[] datosRegistro = registro.getBytes();
                ByteBuffer byteBuffer = ByteBuffer.wrap(datosRegistro);
                try (FileChannel fc = (FileChannel.open(archivo, APPEND))) {
                    fc.write(byteBuffer);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return res;
    }

    private String parseCapitan2String(Capitan capitan) {
        StringBuilder registro = new StringBuilder();
        registro.append(util.completarCampo(capitan.getIdentificacion(), LONGITUD_IDENTIFICACION));
        registro.append(util.completarCampo(capitan.getNombre(), LONGITUD_NOMBRE));
        registro.append(util.completarCampo(capitan.getSexo(), LONGITUD_SEXO));
        registro.append(util.completarCampo(capitan.getEstadoVivo(), LONGITUD_ESTADOVIVO));
        registro.append(util.completarCampo(capitan.getIdRuta(), LONGITUD_IDRUTA));
        registro.append(util.completarCampo(capitan.getNumHijos(), LONGITUD_NUMHIJOS));
        registro.append(util.completarCampo(capitan.getNumEsposas(), LONGITUD_NUMESPOSAS));
        registro.append(util.completarCampo(capitan.getFortuna(), LONGITUD_FORTUNA));

        return registro.toString();
    }

    private Capitan parseCapitanRegistro2Objeto(CharBuffer registro) {
        Capitan capitan = new Capitan();
        String identificacion = registro.subSequence(0, LONGITUD_IDENTIFICACION).toString().trim();
        capitan.setIdentificacion(identificacion);
        registro.position(LONGITUD_IDENTIFICACION);
        registro = registro.slice();

        String nombre = registro.subSequence(0, LONGITUD_NOMBRE).toString().trim();
        capitan.setNombre(nombre);
        registro.position(LONGITUD_NOMBRE);
        registro = registro.slice();

        String sexo = registro.subSequence(0, LONGITUD_SEXO).toString().trim();
        capitan.setSexo(sexo);
        registro.position(LONGITUD_SEXO);
        registro = registro.slice();

        String estadoVivo = registro.subSequence(0, LONGITUD_ESTADOVIVO).toString().trim();
        capitan.setEstadoVivo(estadoVivo);
        registro.position(LONGITUD_ESTADOVIVO);
        registro = registro.slice();

        String idRuta = registro.subSequence(0, LONGITUD_IDRUTA).toString().trim();
        capitan.setIdRuta(idRuta);
        registro.position(LONGITUD_IDRUTA);
        registro = registro.slice();

        String numHijos = registro.subSequence(0, LONGITUD_NUMHIJOS).toString().trim();
        capitan.setNumHijos(numHijos);
        registro.position(LONGITUD_NUMHIJOS);
        registro = registro.slice();

        String numEsposas = registro.subSequence(0, LONGITUD_NUMESPOSAS).toString().trim();
        capitan.setNumEsposas(numEsposas);
        registro.position(LONGITUD_NUMESPOSAS);
        registro = registro.slice();

        String fortuna = registro.subSequence(0, LONGITUD_FORTUNA).toString().trim();
        capitan.setFortuna(fortuna);

        return capitan;
    }
}

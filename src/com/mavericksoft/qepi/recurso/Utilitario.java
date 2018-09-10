/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.recurso;

import com.mavericksoft.qepi.cliente.FXMLDocumentController;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 *
 * @author lsegovia
 */
public class Utilitario {
    private static final String RECURSOS = "/com/mavericksoft/qepi/recurso/recursos.xml";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM HH:mm:ss");
    private List<Personaje> personajes;
    private List<Escenario> escenarios;
    private RecursoConexion recursoConexion;
    private static RecursoConfiguracion recursoConfiguracion;

    public RecursoConfiguracion getRecursoConfiguracion() {
        return Utilitario.recursoConfiguracion;
    }

    public void setRecursoConfiguracion(RecursoConfiguracion recursoConfiguracion) {
        Utilitario.recursoConfiguracion = recursoConfiguracion;
    }

    public RecursoConexion getRecursoConexion() {
        return recursoConexion;
    }

    public void setRecursoConexion(RecursoConexion recursoConexion) {
        this.recursoConexion = recursoConexion;
    }

    public List<Personaje> getPersonajes() {
        return personajes;
    }

    public void setPersonajes(List<Personaje> personajes) {
        this.personajes = personajes;
    }

    public List<Escenario> getEscenarios() {
        return escenarios;
    }

    public void setEscenarios(List<Escenario> escenarios) {
        this.escenarios = escenarios;
    }
    
    private void processConfigurations(){
        BufferedReader br = null;
        Document doc = null;
        String linea="";
        String contenidoXML="";
        try {
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(Utilitario.RECURSOS)));
            //fr = new FileReader(FXMLDocumentController.RECURSOS);
            //br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(/archivo.xml)));
            // From ClassLoader, all paths are "absolute" already - there's no context
            // from which they could be relative. Therefore you don't need a leading slash.
            //InputStream in = this.getClass().getClassLoader().getResourceAsStream("SomeTextFile.txt");
            // From Class, the path is relative to the package of the class unless
            // you include a leading slash, so if you don't want to use the current
            // package, include a slash like this:
            //InputStream in = this.getClass().getResourceAsStream("/SomeTextFile.txt");
            //br = new BufferedReader(fr);
            while ( (linea = br.readLine()) != null ) {                
                contenidoXML+="\n"+linea;
            }
            doc = DocumentHelper.parseText(contenidoXML);
            Node nodoConexion = doc.selectSingleNode("//conexion[@default='true']");
            recursoConexion = new RecursoConexion(
                    nodoConexion.valueOf("@id"), 
                    nodoConexion.selectSingleNode("servidor").getText(), 
                    Integer.parseInt(nodoConexion.selectSingleNode("puerto").getText()) );
            
            List<Node> nodosUniverso = doc.selectNodes("//universo");
            personajes = new ArrayList<>();
            escenarios = new ArrayList<>();
            nodosUniverso.forEach((nodoUniverso) -> {
                List<Node> nodosPersonaje = nodoUniverso.selectNodes("personaje");
                nodosPersonaje.forEach((nodoPersonaje) -> {
                    personajes.add(new Personaje(
                            nodoPersonaje.valueOf("@id"), 
                            nodoPersonaje.getText(), 
                            new Universo(nodoUniverso.valueOf("@id"), nodoUniverso.valueOf("@nombre"))));
                });
                List<Node> nodosEscenario = nodoUniverso.selectNodes("escenario");
                nodosEscenario.forEach((nodoEscenario) -> {
                    escenarios.add(new Escenario(
                            nodoEscenario.valueOf("@id"), 
                            nodoEscenario.getText(),
                            new Universo(nodoUniverso.valueOf("@id"), nodoUniverso.valueOf("@nombre"))));
                });
            });
            
            Node nodoConfiguracion = doc.selectSingleNode("//configuracion");
            recursoConfiguracion = new RecursoConfiguracion(
                    Boolean.parseBoolean(nodoConfiguracion.selectSingleNode("debug").getText()) );
            
        } catch (IOException | NumberFormatException | DocumentException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
    }

    public Utilitario() {
        processConfigurations();
    }
    
    public Personaje getPersonaje(String idPersonaje){
        Personaje personaje = null;
        for (Personaje personajeTmp : personajes) {
            if(personajeTmp.getId().equalsIgnoreCase(idPersonaje)){
                personaje = personajeTmp;
                break;
            }
        }
        return personaje;
    }
    
    public Escenario getEscenario(String idEscenario){
        Escenario escenario = null;
        for (Escenario escenarioTmp : escenarios) {
            escenario = escenarioTmp;
            break;
        }
        return escenario;
    }
    
    public static void imprimir(String mensaje){
        if(Utilitario.recursoConfiguracion.isDebug()){
            Calendar esteInstante = Calendar.getInstance();
            System.out.println("["+Utilitario.SDF.format(esteInstante.getTime())+"] "+mensaje);
        }
    }
    
    public static void enviarMensaje(DataOutputStream out, String mensaje) throws Exception{
        out.writeUTF(TripleDES.encripta(TripleDES.CLAVE, mensaje));
    }
    
    public static String recibirMensaje(DataInputStream in) throws Exception{
        return TripleDES.desencripta(TripleDES.CLAVE, in.readUTF());
    }
    
    public static String rellenarEspaciosDerecha(String cadena, int longitud){
        return String.format("%1$-" + longitud + "s", cadena);  
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.servidor;

import com.mavericksoft.qepi.recurso.Protocolo;
import com.mavericksoft.qepi.recurso.Utilitario;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lsegovia
 */
public class AtencionCliente extends Thread{
    
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean conectado = true;
    private Utilitario util = new Utilitario();


    public AtencionCliente(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(AtencionCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while(conectado){
            try {
                procesarPeticionCliente();    
            } catch (Exception ex) {
                if(ex instanceof IOException){
                    conectado = false;
                    Utilitario.imprimir("No se puede responder al cliente: "+ex.getMessage());
                }else{
                    Utilitario.imprimir("No se puede cifrar/descifrar: "+ex.getMessage());
                }
            }  
        }
    } 

    private void procesarPeticionCliente() throws IOException, Exception {
        /*
        En el lado del cliente, si no existe la necesidad de enviar un campo, Ej para el ACTION_CHAT_ROOM
        no existe la necesidad de enviar el idPersonajeDestino, debido a que se chatea contra el Escenario,
        entonces desde el cliente en esta posición se enviará vacio, respetando la posicion y cantidad de caracteres
        del campo.
        */
        String mensajeEntrada = Utilitario.recibirMensaje(in);
        Utilitario.imprimir("-->"+mensajeEntrada+"<--");
        String flujo = mensajeEntrada.substring(0, 1);
        String accion = mensajeEntrada.substring(1, 4);
        String idPersonaje = mensajeEntrada.substring(4, 7).trim();
        String idEscenario = mensajeEntrada.substring(7, 10).trim();
        String idPersonajeDestino = mensajeEntrada.substring(10, 13).trim();
        String mensaje = mensajeEntrada.substring(13);
        switch(accion){
            case Protocolo.ACTION_CONNECT:
                accionConectarPersonaje(idPersonaje);
                break;
            case Protocolo.ACTION_DISCONNECT:
                accionDesconectarPersonaje(idPersonaje);
                break;
            case Protocolo.ACTION_ADD_ALIAS_CHAT_ROOM:
                accionAgregarPersonajeAlEscenarioChat(idPersonaje,idEscenario,idPersonajeDestino);
                break;
            case Protocolo.ACTION_CLOSE_CHAT_ALIAS_ROOM:
                accionCerrarEscenarioChat(idPersonaje,idEscenario);
                break;
            case Protocolo.ACTION_CHAT_ROOM:
                accionChatearEnEscenario(idPersonaje,idEscenario,mensaje);
                break;
            default:
                accionNoSoportada(idPersonaje);
                break;
        }
       
    }

    private void accionConectarPersonaje(String idPersonaje) throws Exception {
        String mensajeSalida = Protocolo.SEND_TO_CLIENT + Protocolo.ACTION_CONNECT;
        if(!ServidorChat.mapaPersonajesConectados.containsKey(idPersonaje)){
            DatosCliente datosClienteOrigen = new DatosCliente(idPersonaje, null, in, out);
            ServidorChat.mapaPersonajesConectados.put(idPersonaje, datosClienteOrigen);
            //Notificar personajes conectados
            mensajeSalida += Protocolo.RESPONSE_SUCESSFULL;
            mensajeSalida = ServidorChat.mapaPersonajesConectados.keySet().stream()
                    .map((key) -> key+" ").reduce(mensajeSalida, String::concat);
            for (DatosCliente personajeConectado : ServidorChat.mapaPersonajesConectados.values()) {
                Utilitario.enviarMensaje(personajeConectado.getOut(), mensajeSalida);
            }
        }else{
            mensajeSalida += Protocolo.RESPONSE_ERROR+"Personaje ya registrado, intente otro";
            Utilitario.enviarMensaje(out, mensajeSalida);
            conectado = false;
        }
    }

    private void accionDesconectarPersonaje(String idPersonaje) throws Exception {
        String mensajeSalida = Protocolo.SEND_TO_CLIENT + Protocolo.ACTION_DISCONNECT;
        if(ServidorChat.mapaPersonajesConectados.containsKey(idPersonaje)){
            ServidorChat.mapaPersonajesConectados.remove(idPersonaje);
            mensajeSalida += Protocolo.RESPONSE_SUCESSFULL;
            Utilitario.enviarMensaje(out, mensajeSalida);
            //Notificar personajes conectados (osea los que quedan)
            mensajeSalida = ServidorChat.mapaPersonajesConectados.keySet().stream()
                    .map((key) -> key+" ").reduce(mensajeSalida, String::concat);
            for (DatosCliente personajeConectado : ServidorChat.mapaPersonajesConectados.values()) {
                Utilitario.enviarMensaje(personajeConectado.getOut(), mensajeSalida);
            }
        }
        conectado = false;
    }

    private void accionAgregarPersonajeAlEscenarioChat(String idPersonaje, String idEscenario, String idPersonajeDestino) throws Exception {
        List<DatosCliente> datosClientes = new ArrayList<>();
        DatosCliente datosClienteOrigen = ServidorChat.mapaPersonajesConectados.get(idPersonaje);
        datosClienteOrigen.setIdEscenario(idEscenario);
        ServidorChat.mapaPersonajesConectados.put(idPersonaje, datosClienteOrigen);
        DatosCliente datosClienteDestino = ServidorChat.mapaPersonajesConectados.get(idPersonajeDestino);
        datosClienteDestino.setIdEscenario(idEscenario);
        ServidorChat.mapaPersonajesConectados.put(idPersonajeDestino, datosClienteDestino);
        if(!ServidorChat.mapaEscenariosActivos.containsKey(idEscenario)){
            datosClientes.add(datosClienteOrigen);
        }else{
            datosClientes = ServidorChat.mapaEscenariosActivos.get(idEscenario);    
        }
        datosClientes.add(datosClienteDestino);
        ServidorChat.mapaEscenariosActivos.put(idEscenario, datosClientes);
        //no olvidar las respuestas al cliente, para que se hagan las acciones en javafx
        String mensajeSalida = Protocolo.SEND_TO_CLIENT + Protocolo.ACTION_ADD_ALIAS_CHAT_ROOM + Protocolo.RESPONSE_SUCESSFULL + idEscenario;
        for (DatosCliente personajeConectado : ServidorChat.mapaPersonajesConectados.values()) {
            Utilitario.enviarMensaje(personajeConectado.getOut(), mensajeSalida);
        }
    }
    

    private void accionCerrarEscenarioChat(String idPersonaje, String idEscenario) throws Exception {
        if(ServidorChat.mapaEscenariosActivos.containsKey(idEscenario)){
            List<DatosCliente> datosClientes = ServidorChat.mapaEscenariosActivos.get(idEscenario);
            for (DatosCliente datosClienteTmp : datosClientes) {
                if(datosClienteTmp.getIdPersonaje().equalsIgnoreCase(idPersonaje)){
                    datosClientes.remove(datosClienteTmp);
                    break;
                }
            }
            if(datosClientes.size()>0){
                ServidorChat.mapaEscenariosActivos.put(idEscenario, datosClientes);
                String mensaje = util.getPersonaje(idPersonaje).getNombre()+" salio";
                String mensajeSalida = Protocolo.SEND_TO_CLIENT + Protocolo.ACTION_CLOSE_CHAT_ALIAS_ROOM 
                    + Protocolo.RESPONSE_SUCESSFULL + idEscenario + idPersonaje + mensaje;
                for (DatosCliente datosClienteTmp : datosClientes) {
                    Utilitario.enviarMensaje(datosClienteTmp.getOut(), mensajeSalida);
                }
            }else{
                ServidorChat.mapaEscenariosActivos.remove(idEscenario);
            }    
        }
    }

    private void accionChatearEnEscenario(String idPersonaje, String idEscenario, String mensaje) throws Exception {
        if(ServidorChat.mapaEscenariosActivos.containsKey(idEscenario)){
            List<DatosCliente> datosClientes = ServidorChat.mapaEscenariosActivos.get(idEscenario);
            String mensajeSalida = Protocolo.SEND_TO_CLIENT + Protocolo.ACTION_CHAT_ROOM 
                + Protocolo.RESPONSE_SUCESSFULL + idEscenario + idPersonaje + mensaje;
            for (DatosCliente datosClienteTmp : datosClientes) {
                if(!datosClienteTmp.getIdPersonaje().equals(idPersonaje)){
                    Utilitario.enviarMensaje(datosClienteTmp.getOut(), mensajeSalida);
                }
            }
        }
    }

    private void accionNoSoportada(String idPersonaje) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

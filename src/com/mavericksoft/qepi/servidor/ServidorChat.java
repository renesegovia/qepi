/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.servidor;

import com.mavericksoft.qepi.recurso.Utilitario;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author lsegovia
 */
public class ServidorChat {

    public static Map<String, DatosCliente> mapaPersonajesConectados = new HashMap<String, DatosCliente>();
    public static Map<String, List<DatosCliente>> mapaEscenariosActivos = new HashMap<String, List<DatosCliente>>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Utilitario util = new Utilitario();
        ServerSocket serverSocket;
        Socket clientSocket;
        try {
            serverSocket = new ServerSocket(util.getRecursoConexion().getPuerto());
            Utilitario.imprimir("Servidor "+serverSocket.getInetAddress().getHostAddress()
                    +" levantado sobre el puerto: "+serverSocket.getLocalPort());
            while (true) {                
                clientSocket = serverSocket.accept();
                AtencionCliente atencion = new AtencionCliente(clientSocket);
                atencion.start();
            }
        } catch (IOException ex) {
            Utilitario.imprimir("No se puede levantar el servidor "+ex.getMessage());
        }               
                
    }

}

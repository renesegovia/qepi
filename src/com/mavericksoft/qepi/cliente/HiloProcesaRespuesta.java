/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.cliente;

import com.mavericksoft.qepi.recurso.Utilitario;
import com.mavericksoft.qepi.servidor.DatosCliente;
import java.io.IOException;

/**
 *
 * @author lsegovia
 */
public class HiloProcesaRespuesta extends Thread{
    private DatosCliente datosCliente;
    private boolean leerRespuestaServidor;

    public HiloProcesaRespuesta(DatosCliente datosCliente) {
        this.datosCliente = datosCliente;
        this.leerRespuestaServidor = true;
    }

    @Override
    public void run() {
        while(leerRespuestaServidor){
            try {
                procesarRespuestaServidor();    
            } catch (Exception ex) {
                if(ex instanceof IOException){
                    leerRespuestaServidor = false;
                    Utilitario.imprimir("No se puede responder al cliente: "+ex.getMessage());
                }else{
                    Utilitario.imprimir("No se puede cifrar/descifrar: "+ex.getMessage());
                }
            }
        }
    }

    private void procesarRespuestaServidor() {
        
    }
    
    
    
}

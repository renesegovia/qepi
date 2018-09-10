/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author lsegovia
 */
public class DatosCliente {
    private String idPersonaje;
    private String idEscenario;
    private DataInputStream in;
    private DataOutputStream out;

    public String getIdPersonaje() {
        return idPersonaje;
    }

    public void setIdPersonaje(String idPersonaje) {
        this.idPersonaje = idPersonaje;
    }

    public String getIdEscenario() {
        return idEscenario;
    }

    public void setIdEscenario(String idEscenario) {
        this.idEscenario = idEscenario;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public DatosCliente(String idPersonaje, String idEscenario, DataInputStream in, DataOutputStream out) {
        this.idPersonaje = idPersonaje;
        this.idEscenario = idEscenario;
        this.in = in;
        this.out = out;
    }
    
    
    
}

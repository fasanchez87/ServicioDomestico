package com.elements.beya.beans;

/**
 * Created by FABiO on 08/04/2016.
 */
public class SolicitudServicio
{

    private String codigoSolicitudServicio;
    private String codigoClienteSolicitudServicio;
    private String FechaSolicitudServicio;


    private String HoraSolicitudServicio;
    private String UbicacionSolicitudServicio;
    private String LugarSolicitudServicio;
    private String esAtendida;




    public SolicitudServicio()
    {

    }

    public SolicitudServicio(String codigoSolicitudServicio , String FechaSolicitudServicio, String UbicacionSolicitudServicio, String codigoClienteSolicitudServicio)
    {
        this.codigoSolicitudServicio = codigoSolicitudServicio;
        this.codigoClienteSolicitudServicio = codigoClienteSolicitudServicio;
        this.FechaSolicitudServicio = FechaSolicitudServicio;
        this.UbicacionSolicitudServicio = UbicacionSolicitudServicio;
    }


    public String getCodigoSolicitudServicio() {
        return codigoSolicitudServicio;
    }

    public void setCodigoSolicitudServicio(String codigoSolicitudServicio) {
        this.codigoSolicitudServicio = codigoSolicitudServicio;
    }

    public String getFechaSolicitudServicio() {
        return FechaSolicitudServicio;
    }

    public void setFechaSolicitudServicio(String fechaSolicitudServicio) {
        FechaSolicitudServicio = fechaSolicitudServicio;
    }

    public String getUbicacionSolicitudServicio() {
        return UbicacionSolicitudServicio;
    }

    public void setUbicacionSolicitudServicio(String ubicacionSolicitudServicio) {
        UbicacionSolicitudServicio = ubicacionSolicitudServicio;
    }

    public String getCodigoClienteSolicitudServicio() {
        return codigoClienteSolicitudServicio;
    }

    public void setCodigoClienteSolicitudServicio(String codigoClienteSolicitudServicio) {
        this.codigoClienteSolicitudServicio = codigoClienteSolicitudServicio;
    }

    public String getLugarSolicitudServicio() {
        return LugarSolicitudServicio;
    }

    public void setLugarSolicitudServicio(String lugarSolicitudServicio) {
        LugarSolicitudServicio = lugarSolicitudServicio;
    }

    public String getEsAtendida() {
        return esAtendida;
    }

    public void setEsAtendida(String esAtendida) {
        this.esAtendida = esAtendida;
    }

    public String getHoraSolicitudServicio() {
        return HoraSolicitudServicio;
    }

    public void setHoraSolicitudServicio(String horaSolicitudServicio) {
        HoraSolicitudServicio = horaSolicitudServicio;
    }




}

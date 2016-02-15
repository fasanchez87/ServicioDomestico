package com.elements.beya.beans;

/**
 * Created by FABiO on 05/02/2016.
 */
public class Servicio
{

    private String nombreServicio;
    private String descripcionServicio;
    private String valorServicio;
    private String id;



    private boolean isSelected;

    public Servicio()
    {
    }

    public Servicio(String id, String nombreServicio, String descripcionServicio, String valorServicio)
    {
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.valorServicio = valorServicio;
        this.id = id;

    }

    public Servicio(String id, String nombreServicio, String descripcionServicio, String valorServicio, boolean isSelected)
    {
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.valorServicio = valorServicio;
        this.id = id;
        this.isSelected = isSelected;

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNombreServicio()
    {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio)
    {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcionServicio()
    {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio)
    {
        this.descripcionServicio = descripcionServicio;
    }
    public String getValorServicio()
    {
        return valorServicio;
    }

    public void setValorServicio(String valorServicio)
    {
        this.valorServicio = valorServicio;
    }
}

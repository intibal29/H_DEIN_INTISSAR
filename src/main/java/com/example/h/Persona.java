package com.example.h;

import java.util.Objects;

/**
 * La clase {@code Persona} representa a una persona con un nombre, apellidos y edad.
 * Esta clase incluye métodos para acceder y modificar los atributos, así como
 * métodos para comparar instancias de {@code Persona} basados en sus valores.
 */
public class Persona {

    // Atributos de la clase
    private String nombre;
    private String apellidos;
    private int edad;

    /**
     * Constructor de la clase {@code Persona}.
     *
     * @param nombre    El nombre de la persona.
     * @param apellidos Los apellidos de la persona.
     * @param edad      La edad de la persona.
     */
    public Persona(String nombre, String apellidos, int edad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
    }

    /**
     * Obtiene el nombre de la persona.
     *
     * @return El nombre de la persona.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la persona.
     *
     * @param nombre El nuevo nombre de la persona.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos de la persona.
     *
     * @return Los apellidos de la persona.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos de la persona.
     *
     * @param apellidos Los nuevos apellidos de la persona.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene la edad de la persona.
     *
     * @return La edad de la persona.
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Establece la edad de la persona.
     *
     * @param edad La nueva edad de la persona.
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Compara este objeto con otro para determinar si son iguales.
     * Dos personas se consideran iguales si tienen el mismo nombre, apellidos y edad.
     *
     * @param o El objeto con el que comparar.
     * @return {@code true} si ambos objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Verifica si son la misma instancia
        if (o == null || getClass() != o.getClass()) return false; // Verifica que no sea nulo y que sean del mismo tipo
        Persona persona = (Persona) o; // Realiza el casting
        return edad == persona.edad && // Compara edad
                Objects.equals(nombre, persona.nombre) && // Compara nombre
                Objects.equals(apellidos, persona.apellidos); // Compara apellidos
    }

    /**
     * Devuelve un código hash para este objeto, que se basa en el nombre, apellidos y edad.
     *
     * @return Un código hash para la persona.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, edad); // Genera un código hash usando los atributos
    }
}

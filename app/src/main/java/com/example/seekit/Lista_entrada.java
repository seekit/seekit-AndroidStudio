package com.example.seekit;


/** Handler para listado.
 * @author Ramon Invarato Menndez
 * www.jarroba.es
 */
public class Lista_entrada {
	private int idImagen; 
	private String textoEncima; 
	private String textoDebajo;
	private String idTri; 
	
	  
	public Lista_entrada (int idImagen, String textoEncima, String textoDebajo, String idTri) { 
	    this.idImagen = idImagen; 
	    this.textoEncima = textoEncima; 
	    this.textoDebajo = textoDebajo; 
	    this.idTri=idTri;
	}
	

	public String get_IdTri() {
		return idTri;
	}





	public String get_textoEncima() { 
	    return textoEncima; 
	}
	
	public String get_textoDebajo() { 
	    return textoDebajo; 
	}
	
	public int get_idImagen() {
	    return idImagen; 
	} 
}

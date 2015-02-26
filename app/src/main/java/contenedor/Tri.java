package contenedor;

public class Tri {
	private String idTri;
	private String identificador;
	private String nombre;
	private String foto;
	private String activo;
	private String latitud;
    private String longitud;
	private String perdido;
	private String compartido;
    private String descripcion;


	public Tri(String idTri, String identificador, String nombre, String foto, String activo,
			String latitud,String longitud, String perdido, String compartido, String descripcion) {
		super();
		this.idTri=idTri;
		this.identificador = identificador;
		this.nombre = nombre;
		this.foto = foto;
		this.activo = activo;
		this.latitud = latitud;
        this.longitud=longitud;
		this.perdido = perdido;
		this.compartido = compartido;
        this.descripcion=descripcion;
	}


	public String getIdTri() {
		return idTri;
	}


	public void setIdTri(String idTri) {
		this.idTri = idTri;
	}


	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public String getPerdido() {
		return perdido;
	}

	public void setPerdido(String perdido) {
		this.perdido = perdido;
	}

	public String getCompartido() {
		return compartido;
	}

	public void setCompartido(String compartido) {
		this.compartido = compartido;
	}

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }
}

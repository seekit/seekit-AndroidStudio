package contenedor;

public class TriCompartido {
	private String idTri = null;
	private String identificador = null;
	private String nombre = null;
	private String foto = null;
	private String activo = null;
	private String localizacion = null;
	private String perdido = null;
	private String compartido = null;
	private String habilitado = null;
    private String descripcion;
	
	//valores para el usuario propietario.
	private String idUsuarioPropietario=null;
	private String nombrePropetario=null;
	private String apellidoPropietario=null;
	private String mailPropietario=null;

	public TriCompartido() {
		super();
	}
	

	public String getIdUsuarioPropietario() {
		return idUsuarioPropietario;
	}


	public void setIdUsuarioPropietario(String idUsuarioPropietario) {
		this.idUsuarioPropietario = idUsuarioPropietario;
	}


	public String getNombrePropetario() {
		return nombrePropetario;
	}


	public void setNombrePropetario(String nombrePropetario) {
		this.nombrePropetario = nombrePropetario;
	}


	public String getApellidoPropietario() {
		return apellidoPropietario;
	}


	public void setApellidoPropietario(String apellidoPropietario) {
		this.apellidoPropietario = apellidoPropietario;
	}


	public String getMailPropietario() {
		return mailPropietario;
	}


	public void setMailPropietario(String mailPropietario) {
		this.mailPropietario = mailPropietario;
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

	public String getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
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

	public String getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(String habilitado) {
		this.habilitado = habilitado;
	}

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

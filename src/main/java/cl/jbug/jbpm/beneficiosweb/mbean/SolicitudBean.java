/*
 * Copyright (C) 2015  Pablo Sepúlveda P. (psep_AT_ti-nova_dot_cl)
 * 
 * This file is part of the beneficiosweb.
 * beneficiosweb is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * beneficiosweb is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with beneficiosweb.  If not, see <http://www.gnu.org/licenses/>.
 */
package cl.jbug.jbpm.beneficiosweb.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.omnifaces.util.Messages;

import cl.jbug.jbpm.beneficios.Solicitante;
import cl.jbug.jbpm.beneficiosweb.dao.JbpmDAO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;

@ManagedBean(name = "solicitudBean")
@SessionScoped
public class SolicitudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SolicitudBean.class);
	private Solicitante solicitante;
	private Date fechaNacimiento;
	private List<SelectItem> estados;
	private List<SelectItem> generos;
	
	@Inject
	private JbpmDAO jbpmDAO;
	
	@PostConstruct
	private void init() {
		GenericUtils.removeManagedBean("bandejaTareasBean");
		this.solicitante = new Solicitante();
		this.loadEstadosCiviles();
		this.loadGeneros();
	}
	
	/**
	 * 
	 */
	private void loadGeneros() {
		this.solicitante.setSexo(1);
		
		this.generos = new ArrayList<SelectItem>();
		this.generos.add(new SelectItem(1, "Hombre"));
		this.generos.add(new SelectItem(2, "Mujer"));
		this.generos.add(new SelectItem(3, "Otro"));
	}
	
	/**
	 * 
	 */
	private void loadEstadosCiviles() {
		this.solicitante.setEstadoCivil(1);
		
		this.estados = new ArrayList<SelectItem>();
		this.estados.add(new SelectItem(1, "Soltero"));
		this.estados.add(new SelectItem(2, "Casado"));
		this.estados.add(new SelectItem(3, "Separado"));
		this.estados.add(new SelectItem(4, "Viudo"));
		this.estados.add(new SelectItem(5, "Otro"));
	}
	
	/**
	 * 
	 */
	public void aceptarAction() {
		logger.info("aceptar");

		try {
			this.solicitante.setEdad(GenericUtils.getEdad(this.fechaNacimiento));
			
			Long processId = this.jbpmDAO.startProcess(this.solicitante);
			
			Messages.addGlobalInfo("Su requerimiento ha sido asignado con el ID: " + processId, "");
			
		} catch (Exception e) {
			logger.error(e, e);
			Messages.addGlobalError(e.getMessage(), "");
		}
		
		this.solicitante = new Solicitante();
	}
	
	public void cancelarAction() {
		this.solicitante = new Solicitante();
	}

	public List<SelectItem> getEstados() {
		return estados;
	}

	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}

	public List<SelectItem> getGeneros() {
		return generos;
	}

	public void setGeneros(List<SelectItem> generos) {
		this.generos = generos;
	}

	public Solicitante getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Solicitante solicitante) {
		this.solicitante = solicitante;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

}

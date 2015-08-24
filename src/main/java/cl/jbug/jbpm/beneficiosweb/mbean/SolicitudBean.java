/*
 * Copyright (C) 2015  Pablo Sepúlveda P. (psep_AT_ti-nova_dot_cl)
 * 
 * This file is part of the jBPMClient.
 * jBPMClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * jBPMClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jBPMClient.  If not, see <http://www.gnu.org/licenses/>.
 */
package cl.jbug.jbpm.beneficiosweb.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.omnifaces.util.Messages;

import cl.jbug.jbpm.beneficiosweb.dao.JbpmDAO;
import cl.jbug.jbpm.beneficiosweb.to.BeneficiarioTO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;

@ManagedBean(name = "solicitudBean")
@SessionScoped
public class SolicitudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SolicitudBean.class);
	private BeneficiarioTO beneficiario;
	private List<SelectItem> estados;
	private List<SelectItem> generos;
	
	@Inject
	private JbpmDAO jbpmDAO;
	
	@PostConstruct
	private void init() {
		GenericUtils.removeManagedBean("bandejaTareasBean");
		this.beneficiario = new BeneficiarioTO();
		this.loadEstadosCiviles();
		this.loadGeneros();
	}
	
	/**
	 * 
	 */
	private void loadGeneros() {
		this.generos = new ArrayList<SelectItem>();
		this.generos.add(new SelectItem(1, "Hombre"));
		this.generos.add(new SelectItem(2, "Mujer"));
		this.generos.add(new SelectItem(3, "Otro"));
	}
	
	/**
	 * 
	 */
	private void loadEstadosCiviles() {
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
			logger.info("hijos: " + this.beneficiario.getNumHijos());
			logger.info("nombre: " + this.beneficiario.getNombre());
			logger.info("nac: " + this.beneficiario.getFechaNacimiento());
			
			Long processId = this.jbpmDAO.startProcess(this.beneficiario);
			
			Messages.addGlobalInfo("Su requerimiento ha sido asignado con el ID: " + processId, "");
			
		} catch (Exception e) {
			logger.error(e, e);
			Messages.addGlobalError(e.getMessage(), "");
		}
		
		this.beneficiario = new BeneficiarioTO();
	}
	
	public void cancelarAction() {
		this.beneficiario = new BeneficiarioTO();
	}

	public List<SelectItem> getEstados() {
		return estados;
	}

	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}

	public BeneficiarioTO getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(BeneficiarioTO beneficiario) {
		this.beneficiario = beneficiario;
	}

	public List<SelectItem> getGeneros() {
		return generos;
	}

	public void setGeneros(List<SelectItem> generos) {
		this.generos = generos;
	}

}

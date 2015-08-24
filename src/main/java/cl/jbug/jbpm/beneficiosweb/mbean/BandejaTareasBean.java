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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.kie.api.task.model.TaskSummary;

import cl.jbug.jbpm.beneficios.Solicitante;
import cl.jbug.jbpm.beneficiosweb.dao.JbpmDAO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;

/**
 * @author psep
 *
 */
@ManagedBean(name = "bandejaTareasBean")
@SessionScoped
public class BandejaTareasBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(BandejaTareasBean.class);

	@Inject
	private JbpmDAO jbpmDAO;

	private List<TaskSummary> tasks;
	private TaskSummary tareaGestionada;
	private boolean panelGestion;
	private boolean panelObservaciones;
	private Solicitante solicitante;
	private String observaciones;

	@PostConstruct
	private void init() {
		logger.info("postconstruct");
		GenericUtils.removeManagedBean("solicitudBean");
		this.panelGestion = false;
		this.panelObservaciones = false;
		this.observaciones = null;
		this.tasks = this.jbpmDAO.listTasksByPotencialOwner();
	}

	/**
	 * @param task
	 */
	public void gestionarAction(TaskSummary task) {
		this.tareaGestionada = task;
		
		Solicitante s = this.jbpmDAO.getSolicitante(task.getProcessInstanceId());
		
		if (s == null) {
			this.solicitante = new Solicitante();
		} else {
			this.solicitante = s;
		}

		this.panelGestion = true;
	}

	/**
	 * 
	 */
	public void aceptarAction() {
		logger.info("aceptar");

		try {
			this.completeEvaluacion(true);
			this.disabledPaneles();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * 
	 */
	public void rechazarAction() {
		logger.info("rechazar");

		try {
			this.completeEvaluacion(false);
			this.tasks.remove(this.tareaGestionada);

			List<TaskSummary> _tasks = this.jbpmDAO.listTasksByPotencialOwner();
			
			logger.info(_tasks.size());
			
			Iterator<TaskSummary> it = _tasks.iterator();

			while (it.hasNext()) {
				TaskSummary task = it.next();

				if (task.getProcessInstanceId().equals(
						this.tareaGestionada.getProcessInstanceId())) {
					this.tareaGestionada = task;
					logger.info("nueva id: " + this.tareaGestionada.getId());
					break;
				}
			}

			this.panelGestion = false;
			this.panelObservaciones = true;
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * 
	 */
	public void cancelarAction() {
		this.tareaGestionada = null;
		this.panelGestion = false;
		this.solicitante = null;
	}

	/**
	 * 
	 */
	public void enviarObservaciones() {
		try {
			logger.info("obs: " + this.observaciones);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("_observaciones", this.observaciones);
			this.jbpmDAO.completeTask(this.tareaGestionada.getId(), params);

			this.panelObservaciones = false;
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * @param evaluacion
	 * @throws MalformedURLException
	 */
	private void completeEvaluacion(boolean evaluacion)
			throws MalformedURLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("_evaluacion", evaluacion);

		this.jbpmDAO.completeTask(this.tareaGestionada.getId(), params);
	}

	/**
	 * 
	 */
	private void disabledPaneles() {
		this.tasks.remove(this.tareaGestionada);
		this.cancelarAction();
	}

	public boolean isPanelGestion() {
		return panelGestion;
	}

	public void setPanelGestion(boolean panelGestion) {
		this.panelGestion = panelGestion;
	}

	public boolean isPanelObservaciones() {
		return panelObservaciones;
	}

	public void setPanelObservaciones(boolean panelObservaciones) {
		this.panelObservaciones = panelObservaciones;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<TaskSummary> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskSummary> tasks) {
		this.tasks = tasks;
	}

	public Solicitante getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Solicitante solicitante) {
		this.solicitante = solicitante;
	}

}

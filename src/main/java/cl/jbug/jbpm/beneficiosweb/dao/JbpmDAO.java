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
package cl.jbug.jbpm.beneficiosweb.dao;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;

import cl.jbug.jbpm.beneficiosweb.to.BeneficiarioTO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;
import cl.jbug.jbpm.client.JBPMClient;

/**
 * @author psep
 *
 */
public class JbpmDAO {

	private static final Logger logger = Logger.getLogger(JbpmDAO.class);
	private static final String DEPLOYMENT_ID = "cl.jbug.jbpm:beneficios:1.0";
	private static final String PROCESS_DEF = "beneficios.IngresoSolicitud";
	private static final String actorId = "pepe";
	private static final String password = "qwerty123";

	/**
	 * @param userId
	 * @param passwd
	 * @return
	 */
	private JBPMClient getJBPMClient() {
		JBPMClient client = new JBPMClient("localhost", "8080", actorId,
				password);

		return client;
	}

	/**
	 * @param taskId
	 * @param params
	 * @throws MalformedURLException
	 */
	public void completeTask(long taskId, Map<String, Object> params)
			throws MalformedURLException {
		if (params == null) {
			params = new HashMap<String, Object>();
		}

		this.getJBPMClient().completeTask(DEPLOYMENT_ID, taskId, params);
	}

	/**
	 * @param processId
	 * @return
	 */
	public Map<String, Object> getVariables(long processId) {
		try {
			return this.getJBPMClient().getVariables(DEPLOYMENT_ID, processId);
		} catch (MalformedURLException e) {
			logger.error(e, e);
		}

		return null;
	}

	/**
	 * @param processId
	 * @return
	 */
	public String getRespuesta(long processId) {
		try {
			Map<String, Object> variables = this.getVariables(processId);
			String respuesta = (String) variables.get("resultado");

			if (respuesta != null) {
				respuesta = respuesta.replace("<br/>", "\n");

				return respuesta;
			}

		} catch (Exception e) {
			logger.error(e, e);
		}

		return null;
	}

	/**
	 * @param beneficiario
	 * @throws Exception
	 */
	public Long startProcess(BeneficiarioTO beneficiario) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("edad",
				GenericUtils.getEdad(beneficiario.getFechaNacimiento()));
		params.put("estadoCivil", beneficiario.getEstadoCivil());
		params.put("nombre", beneficiario.getNombre());
		params.put("numHijos", beneficiario.getNumHijos());
		params.put("run", beneficiario.getRun());
		params.put("sueldo", beneficiario.getSueldo());
		params.put("sexo", beneficiario.getGenero());

		ProcessInstance instance = this.getJBPMClient().startProcess(
				DEPLOYMENT_ID, PROCESS_DEF, params);
		
		return instance.getId();
	}

	/**
	 * @param processId
	 * @return
	 */
	public BeneficiarioTO getBeneficiarioByProcess(long processId) {
		try {
			BeneficiarioTO beneficiario = new BeneficiarioTO();
			Map<String, Object> variables = this.getVariables(processId);

			beneficiario
					.setRun(Integer.parseInt((String) variables.get("run")));
			beneficiario.setNombre((String) variables.get("nombre"));
			beneficiario.setEstadoCivil(Integer.parseInt((String) variables
					.get("estadoCivil")));
			beneficiario.setGenero(Integer.parseInt((String) variables
					.get("sexo")));
			beneficiario.setNumHijos(Integer.parseInt((String) variables
					.get("numHijos")));
			beneficiario.setSueldo(Integer.parseInt((String) variables
					.get("sueldo")));

			return beneficiario;

		} catch (Exception e) {
			logger.error(e, e);
		}

		return null;
	}

	/**
	 * @return
	 */
	public List<TaskSummary> listTasksByActor() {
		List<TaskSummary> tasks = new ArrayList<TaskSummary>();

		try {
			tasks = this.getJBPMClient().listTasksByPotencialOwner(
					DEPLOYMENT_ID);

		} catch (MalformedURLException e) {
			logger.error(e, e);
		}

		return tasks;
	}

}

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
package cl.jbug.jbpm.beneficiosweb.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;

import com.google.gson.Gson;

import cl.jbug.jbpm.beneficios.Solicitante;
import cl.jbug.jbpm.beneficiosweb.to.UserTO;
import cl.jbug.jbpm.beneficiosweb.utils.ServletUtils;

/**
 * @author psep
 *
 */
public class JbpmDAO {

	private static final Logger logger = Logger.getLogger(JbpmDAO.class);
	private static final String DEPLOYMENT_ID = "cl.jbug.jbpm:beneficios:1.1";
	private static final String PROCESS_DEF = "beneficios.IngresoSolicitud";

	private static final String LOCALE = "en-UK";
	private static final String BASE_URL = "http://localhost:8080/jbpm-console/";

	private UserTO user;

	@PostConstruct
	private void init() {
		this.user = ServletUtils.getUser();
	}

	/**
	 * @param taskId
	 * @param params
	 * @throws MalformedURLException
	 */
	public void completeTask(long taskId, Map<String, Object> params) throws MalformedURLException {
		startTask(DEPLOYMENT_ID, taskId);
		endTask(DEPLOYMENT_ID, taskId, params);
	}

	/**
	 * @param params
	 * @return
	 */
	public Long startProcess(Solicitante solicitante) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("solicitante", solicitante);

			ProcessInstance instance = initProcess(DEPLOYMENT_ID, PROCESS_DEF, params);

			return instance.getId();

		} catch (Exception e) {
			logger.error(e, e);
		}

		return null;
	}

	/**
	 * @param processId
	 * @return
	 */
	public Solicitante getSolicitante(long processInstanceId) {
		try {
			Map<String, Object> variables = this.getVariables(processInstanceId);
			String json = (String) variables.get("resultado");

			Solicitante solicitante = new Gson().fromJson(json, Solicitante.class);
			solicitante.setMensaje(solicitante.getMensaje().replace("<br/>", "\n"));

			return solicitante;

		} catch (Exception e) {
			logger.error(e, e);
		}

		return null;
	}

	/**
	 * @return
	 */
	public List<TaskSummary> listTasksByPotencialOwner() {
		List<TaskSummary> tasks = new ArrayList<TaskSummary>();

		try {
			tasks = getTaskService(DEPLOYMENT_ID).getTasksAssignedAsPotentialOwner(this.user.getUsername(), LOCALE);

		} catch (Exception e) {
			logger.error(e, e);
		}

		return tasks;
	}

	/**
	 * @param processInstanceId
	 * @return
	 * @throws MalformedURLException
	 */
	public Map<String, Object> getVariables(long processInstanceId) throws MalformedURLException {
		Map<String, Object> variables = new HashMap<String, Object>();
		RuntimeEngine engine = getRuntimeEngine(DEPLOYMENT_ID);

		AuditService auditService = engine.getAuditService();

		List<? extends VariableInstanceLog> variablesLog = auditService.findVariableInstances(processInstanceId);

		for (VariableInstanceLog v : variablesLog) {
			logger.info(v.getVariableId() + ": " + v.getValue());
			variables.put(v.getVariableId(), v.getValue());
		}

		return variables;
	}

	public void startTask(String deploymentId, long taskId) throws MalformedURLException {
		TaskService taskService = getTaskService(deploymentId);
		taskService.start(taskId, this.user.getUsername());
	}

	public void endTask(String deploymentId, long taskId, Map<String, Object> params) throws MalformedURLException {
		TaskService taskService = getTaskService(deploymentId);
		taskService.complete(taskId, this.user.getUsername(), params);
	}

	public TaskService getTaskService(String deploymentId) throws MalformedURLException {
		RuntimeEngine engine = getRuntimeEngine(deploymentId);

		TaskService taskService = engine.getTaskService();
		taskService = engine.getTaskService();

		return taskService;
	}

	public ProcessInstance initProcess(String deploymentId, String processDef, Map<String, Object> params)
			throws MalformedURLException {
		KieSession ksession = getRuntimeEngine(deploymentId).getKieSession();
		ProcessInstance processInstance = null;

		if (params == null) {
			processInstance = ksession.startProcess(processDef);
		} else {
			processInstance = ksession.startProcess(processDef, params);
		}

		long procId = processInstance.getId();

		logger.info("Proceso creado ID: " + procId);

		return processInstance;
	}

	private RuntimeEngine getRuntimeEngine(String deploymentId) throws MalformedURLException {
		URL baseUrl = new URL(BASE_URL);
		return RemoteRuntimeEngineFactory.newRestBuilder().addUrl(baseUrl).addUserName(this.user.getUsername())
				.addPassword(this.user.getPassword()).addDeploymentId(deploymentId).build();
	}

}

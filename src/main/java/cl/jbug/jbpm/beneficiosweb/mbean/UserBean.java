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
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;

import cl.jbug.jbpm.beneficiosweb.to.UserTO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;
import cl.jbug.jbpm.beneficiosweb.utils.PropertiesUtils;
import cl.jbug.jbpm.beneficiosweb.utils.ServletUtils;

/**
 * @author psep
 *
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UserBean.class);
	private UserTO user;
	private boolean esOperador;

	@PostConstruct
	private void init() {
		this.esOperador = false;
		this.user = new UserTO();
		ServletUtils.setUser(null);
	}

	public String loginAction() {
		GenericUtils.removeManagedBean("bandejaTareasBean");
		GenericUtils.removeManagedBean("solicitudBean");

		if (this.auth() && !this.esOperador) {
			return "main?faces-redirect=true";
		} else if (this.auth() && this.esOperador) {
			return "ingreso?faces-redirect=true";
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Error: Los datos no son correctos",
					"Error: Los datos no son correctos");
			context.addMessage(null, message);
			
			ServletUtils.setUser(null);

			return "login";
		}
	}
	
	public String logoutAction() {
		this.init();
		
		return "login";
	}

	public boolean auth() {
		try {
			String passwd = PropertiesUtils.getProperty(
					this.user.getUsername(), PropertiesUtils.USERS_FILE);

			if (passwd != null && passwd.equals(this.user.getPassword())) {
				UserTO _user = new UserTO();
				_user.setUsername(this.user.getUsername());
				_user.setPassword(passwd);
				
				String roles = PropertiesUtils.getProperty(_user.getUsername(),
						PropertiesUtils.ROLES_FILE);
				String[] rolesArr = roles.split(",");
				
				_user.setRoles(Arrays.asList(rolesArr));
				
				ServletUtils.setUser(_user);
				
				for (String rol : rolesArr) {
					if (rol.equals("operador")) {
						this.esOperador = true;
						break;
					}
				}

				return true;
			}

		} catch (Exception e) {
			logger.error("Usuario no encontrado");
		}

		return false;
	}

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public boolean isEsOperador() {
		return esOperador;
	}

	public void setEsOperador(boolean esOperador) {
		this.esOperador = esOperador;
	}

}

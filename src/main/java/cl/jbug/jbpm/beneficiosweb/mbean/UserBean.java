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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.jboss.logging.Logger;

import cl.jbug.jbpm.beneficiosweb.to.UserTO;
import cl.jbug.jbpm.beneficiosweb.utils.GenericUtils;

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
	
	@PostConstruct
	private void init() {
		this.user = new UserTO();
	}

	public String loginAction() {
		GenericUtils.removeManagedBean("bandejaTareasBean");
		GenericUtils.removeManagedBean("solicitudBean");
		logger.info("user: " + this.user.getUsername());

		return "main?faces-redirect=true";
	}

	public UserTO getUser() {
		return user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

}

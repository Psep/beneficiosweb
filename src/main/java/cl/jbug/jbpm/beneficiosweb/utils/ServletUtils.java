package cl.jbug.jbpm.beneficiosweb.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cl.jbug.jbpm.beneficiosweb.to.UserTO;

/**
 * @author psep
 *
 */
public final class ServletUtils {
	
	private static final String USER = "user";
	
	/**
	 * @param user
	 */
	public static final void setUser(UserTO user) {
		getSession().setAttribute(USER, user);
	}
	
	/**
	 * @return
	 */
	public static final UserTO getUser() {
		return (UserTO) getSession().getAttribute(USER);
	}
	
	/**
	 * @return
	 */
	public static final HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}

	/**
	 * @return
	 */
	public static final HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
	}

}

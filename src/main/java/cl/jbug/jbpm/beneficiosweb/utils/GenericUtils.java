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
package cl.jbug.jbpm.beneficiosweb.utils;

import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;

/**
 * @author psep
 *
 */
public final class GenericUtils {
	
	/**
	 * @param mbean
	 */
	public static final void removeManagedBean(String mbean) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put(mbean, null);
	}

	/**
	 * @param fechaNacimiento
	 * @return
	 */
	public static final int getEdad(Date fechaNacimiento) throws Exception{
		Calendar fechaActual = Calendar.getInstance();
		Calendar fechaNac = Calendar.getInstance();
		fechaNac.setTime(fechaNacimiento);

		int anios = fechaActual.get(Calendar.YEAR)
				- fechaNac.get(Calendar.YEAR);
		int meses = fechaActual.get(Calendar.MONTH)
				- fechaNac.get(Calendar.MONTH);
		int dias = fechaActual.get(Calendar.DAY_OF_MONTH)
				- fechaNac.get(Calendar.DAY_OF_MONTH);

		if (meses < 0 || (meses == 0 && dias < 0)) {

			anios--;
		}

		return anios;
	}

}

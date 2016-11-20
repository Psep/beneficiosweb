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
package cl.jbug.jbpm.beneficiosweb.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author psep
 *
 */
public final class PropertiesUtils {
	
	private static final String CONF_BASE = "/opt/jboss/wildfly/standalone/configuration/";
	public static final String USERS_FILE = CONF_BASE + "jbpm-users.properties";
	public static final String ROLES_FILE = CONF_BASE + "jbpm-roles.properties";
	
	public static final String getProperty(String key, String file) throws IOException {
		InputStream input = new FileInputStream(file);
		
		Properties prop = new Properties();
		prop.load(input);
		
		String value = prop.getProperty(key);
		
		input.close();
		
		return value;
	}

}

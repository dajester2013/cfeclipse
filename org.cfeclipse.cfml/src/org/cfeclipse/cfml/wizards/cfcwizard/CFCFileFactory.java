package org.cfeclipse.cfml.wizards.cfcwizard;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CFCFileFactory {

	private static DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

	public static StringBuffer getString(CFCBean appCFCBean) {
	
		StringBuffer sb = new StringBuffer();

		if (appCFCBean.getAutodoc()) {
			sb	.append("<!---\n")
				.append("  --- ").append(appCFCBean.getName()).append("\n  --- ")
				;
			
			for (int r = 0; r < appCFCBean.getName().length(); r++)
				sb.append("-");
			
			sb.append("\n");
	
			if (appCFCBean.getHint().trim().length() > 0)
				sb.append("  --- \n  --- ").append(appCFCBean.getHint().trim().replaceAll("(\n|\r)+", "\n  --- ")).append("\n  --- \n");
			else
				sb.append("  --- \n");
				
			sb	.append("  --- author: ").append(System.getProperty("user.name")).append("\n")
				.append("  --- date:   ").append(dateformat.format(new Date())).append("\n")
				.append("  --->\n")
				;
		}
	
		sb.append("<cfcomponent");
		
		if(appCFCBean.getDisplayName().trim().length() > 0)
		sb.append(" displayname=\"" + appCFCBean.getDisplayName().trim() + "\"");
		
		if(appCFCBean.getHint().trim().length() > 0)
			sb.append(" hint=\"" + appCFCBean.getHint().trim().replaceAll("(\n|\r)+", " ") + "\"");
		
		if(appCFCBean.getExtendCFC().trim().length() > 0)
			sb.append(" extends=\"" + appCFCBean.getExtendCFC().trim() + "\"");
			
		if (appCFCBean.getAccessors().trim().length() > 0)
			sb.append(" accessors=\"" + appCFCBean.getAccessors().trim() + "\"");

		if(appCFCBean.getOutput().trim().length() > 0)
			sb.append(" output=\"" + appCFCBean.getOutput().trim() + "\"");
			
		if (appCFCBean.getPersistent().trim().length() > 0)
			sb.append(" persistent=\"" + appCFCBean.getPersistent().trim() + "\"");

		sb.append(">");
		
		sb.append("\n\n");
		
		if(appCFCBean.hasProperties()) {
			sb.append(getPropertiesAsTags(appCFCBean.getPropertyBeans()));
			sb.append(getPropertyGettersAndSetters(appCFCBean.getPropertyBeans()));
		}
		
		//do any functions
		if(appCFCBean.hasFunctions()){
			sb.append(getFunctionTags(appCFCBean.getFunctionBeans()));
		}
		sb.append("</cfcomponent>");
		
		return sb;
	}

	public static StringBuffer getScriptString(CFCBean appCFCBean) {

		StringBuffer sb = new StringBuffer();
		
		/* 06/01/2013 jesse.shaffer Added doc comment block to all CFCs. Moved hint to the doc comment. */
		if (appCFCBean.getAutodoc()) {
			sb	.append("/**\n")
				.append(" * ")
				;
			
			if (appCFCBean.getHint().trim().length() > 0) {
				String[] lines = appCFCBean.getHint().split("\n");
				for (int l = 0; l < lines.length; l++) {
					if (!lines[l].startsWith("@") && lines[l].trim().length() > 0) {
						sb.append(lines[l]).append("\n * ");
					}
				}
				sb.append("\n").append(" * ");
				for (int l = 0; l < lines.length; l++) {
					if (lines[l].startsWith("@")) {
						sb.append(lines[l]).append("\n").append(" * ");
					}
				}
				//sb.append(appCFCBean.getHint().replaceAll("\n", "\n * ")).append("\n");
			} else if (appCFCBean.getDisplayName().trim().length() > 0)
				sb.append(appCFCBean.getDisplayName()).append("\n").append(" * \n * ");
			else
				sb.append(appCFCBean.getName()).append("\n").append(" * \n * ");
			
			if (!appCFCBean.getHint().contains("@author "))
				sb.append("@author ").append(System.getProperty("user.name")).append("\n * ");
			
			sb	.append("@date ").append(dateformat.format(new Date())).append("\n")
				.append(" **/\n")
				;
		}
		
		sb.append("component");

		if (appCFCBean.getDisplayName().trim().length() > 0)
			sb.append(" displayname=\"" + appCFCBean.getDisplayName().trim() + "\"");

		if (!appCFCBean.getAutodoc() && appCFCBean.getHint().trim().length() > 0)
			sb.append(" hint=\"" + appCFCBean.getHint().trim().replaceAll("(\r|\n)+", " ") + "\"");

		if (appCFCBean.getExtendCFC().trim().length() > 0)
			sb.append(" extends=\"" + appCFCBean.getExtendCFC().trim() + "\"");

		if (appCFCBean.getAccessors().trim().length() > 0)
			sb.append(" accessors=" + appCFCBean.getAccessors().trim());

		if (appCFCBean.getOutput().trim().length() > 0)
			sb.append(" output=" + appCFCBean.getOutput().trim());

		if (appCFCBean.getPersistent().trim().length() > 0)
			sb.append(" persistent=" + appCFCBean.getPersistent().trim());

		sb.append(" {");

		sb.append("\n\n");

		if (appCFCBean.hasProperties()) {
			sb.append(getPropertiesAsCFScript(appCFCBean.getPropertyBeans()));
			sb.append(getPropertyGettersAndSettersAsCFScript(appCFCBean.getPropertyBeans()));
		}

		// do any functions
		if (appCFCBean.hasFunctions()) {
			sb.append(getFunctionsAsCFScript(appCFCBean.getFunctionBeans()));
		}
		sb.append("}");

		return sb;
	}

	private static String getPropertiesAsTags(List propertyBeans)
	{
		StringBuffer sb = new StringBuffer();
			
		for (Iterator iter = propertyBeans.iterator(); iter.hasNext();)
		{
			CFCPropertyBean bean = (CFCPropertyBean)iter.next();
			
			sb.append("\t");
			sb.append("<cfproperty name=\"");
			sb.append(bean.getName() + "\"");
			
			if(bean.getDisplayName().length() > 0)
			sb.append(" displayname=\"" + bean.getDisplayName() + "\"");
			
			if(bean.getHint().length() > 0)
			sb.append(" hint=\"" + bean.getHint() + "\"");
			
			if(bean.getType().length() > 0)
			sb.append(" type=\"" + bean.getType() + "\"");
			
			if(bean.getDefaultVal().length() > 0)
			sb.append(" default=\"" + bean.getDefaultVal() + "\"");
			
			sb.append(" />");
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	private static String getPropertiesAsCFScript(List propertyBeans) {
		StringBuffer sb = new StringBuffer();

		for (Iterator iter = propertyBeans.iterator(); iter.hasNext();) {
			CFCPropertyBean bean = (CFCPropertyBean) iter.next();

			sb.append("\t");
			sb.append("property name=\"");
			sb.append(bean.getName() + "\"");

			if (bean.getDisplayName().length() > 0)
				sb.append(" displayname=\"" + bean.getDisplayName() + "\"");

			if (bean.getHint().length() > 0)
				sb.append(" hint=\"" + bean.getHint() + "\"");

			if (bean.getType().length() > 0)
				sb.append(" type=\"" + bean.getType() + "\"");

			if (bean.getDefaultVal().length() > 0)
				sb.append(" default=\"" + bean.getDefaultVal() + "\"");

			sb.append(";");
			sb.append("\n");
		}

		return sb.toString();
	}

	private static String getPropertyGettersAndSetters(List propertyBeans)
	{
		StringBuffer sb = new StringBuffer();
			
		for (Iterator iter = propertyBeans.iterator(); iter.hasNext();)
		{
			CFCPropertyBean bean = (CFCPropertyBean)iter.next();
			
			
			//Get the actual name of the variable and the scope if there is any
			String propName = "";
			
			
			if(bean.getName().split("\\.").length == 1){
				propName = bean.getName();
			}
			
			else if(bean.getName().split("\\.").length > 1){
				String[] strings = bean.getName().split("\\.");
				propName = strings[strings.length-1];
			}
			
			if (bean.shouldWriteGetter()) {
			    sb.append("\n\t");
			    sb.append("<cffunction name=\"get");
			    
			    
			    sb.append(propName.substring(0,1).toUpperCase());
			    sb.append(propName.substring(1));
			    
			    
			    sb.append("\" access=\"");
			    sb.append(bean.getGetterAccess());
			    sb.append("\"");
			    sb.append(" output=\"false\"");
			    sb.append(" returntype=\"");
			    sb.append(bean.getType());
			    sb.append("\"");
			    sb.append(">");
			    sb.append("\n\t\t");
			    sb.append("<cfreturn ");
			    sb.append(bean.getName());
			    sb.append(" />");
			    sb.append("\n\t");
			    sb.append("</cffunction>");
			    sb.append("\n");
			}
			
			if (bean.shouldWriteSetter()) {
			    sb.append("\n\t");
			    sb.append("<cffunction name=\"set");
			    sb.append(propName.substring(0,1).toUpperCase());
			    sb.append(propName.substring(1));
			    sb.append("\" access=\"");
			    sb.append(bean.getSetterAccess());
			    sb.append("\"");
			    sb.append(" output=\"false\"");
			    sb.append(" returntype=\"void\"");
			    sb.append(">");
			    sb.append("\n\t\t");
			    sb.append("<cfargument name=\"");
			    sb.append(propName);
			    sb.append("\" type=\"");
			    sb.append(bean.getType());
			    sb.append("\" required=\"true\" />");
			    sb.append("\n\t\t");
			    sb.append("<cfset ");
			    sb.append(bean.getName());
			    sb.append(" = arguments.");
			    sb.append(propName);
			    sb.append(" />");
			    //sb.append("\n\t\t");
			   // sb.append("<cfreturn />");
			    sb.append("\n\t");
			    sb.append("</cffunction>");
			    sb.append("\n");
			}
			
		}
		
		return sb.toString();
	}
	
	private static String getPropertyGettersAndSettersAsCFScript(List propertyBeans) {
		StringBuffer sb = new StringBuffer();

		for (Iterator iter = propertyBeans.iterator(); iter.hasNext();) {
			CFCPropertyBean bean = (CFCPropertyBean) iter.next();

			// Get the actual name of the variable and the scope if there is any
			String propName = "";

			if (bean.getName().split("\\.").length == 1) {
				propName = bean.getName();
			}

			else if (bean.getName().split("\\.").length > 1) {
				String[] strings = bean.getName().split("\\.");
				propName = strings[strings.length - 1];
			}

			if (bean.shouldWriteGetter()) {
				sb.append("\n\t");
				sb.append(bean.getGetterAccess());
				sb.append(" ");
				sb.append(bean.getType());
				sb.append(" function get");

				sb.append(propName.substring(0, 1).toUpperCase());
				sb.append(propName.substring(1));
				sb.append("() output=\"false\"");
				sb.append(" {");
				sb.append("\n\t\t");
				sb.append("return ");
				sb.append(bean.getName());
				sb.append(";");
				sb.append("\n\t");
				sb.append("}");
				sb.append("\n");
			}

			if (bean.shouldWriteSetter()) {
				sb.append("\n\t");
				sb.append(bean.getGetterAccess());
				sb.append(" ");
				sb.append(bean.getType());
				sb.append(" function set");
				sb.append(propName.substring(0, 1).toUpperCase());
				sb.append(propName.substring(1));
				sb.append("(required ");
				sb.append(bean.getType());
				sb.append(" ");
				sb.append(propName);
				sb.append(") output=\"false\"");
				sb.append(" {");
				sb.append("\n\t\t");
				sb.append(bean.getName());
				sb.append(" = arguments.");
				sb.append(propName);
				sb.append(";");
				// sb.append("\n\t\t");
				// sb.append("<cfreturn />");
				sb.append("\n\t");
				sb.append("}");
				sb.append("\n");
			}

		}

		return sb.toString();
	}

	private static String getFunctionTags(List functionBeans)
	{
		StringBuffer sb = new StringBuffer();
		
		for(Iterator iter = functionBeans.iterator(); iter.hasNext();)
		{
			CFCFunctionBean bean = (CFCFunctionBean)iter.next();
			
			sb.append("\n\t");
			sb.append("<cffunction name=\"" + bean.getName() + "\"");
			
			if(bean.getDisplayName().length() > 0)
			sb.append(" displayname=\"" + bean.getDisplayName() + "\"");
			
			if(bean.getHint().length() > 0)
			sb.append(" hint=\"" + bean.getHint() + "\"");
			
			if(bean.getAccess().length() > 0)
			sb.append(" access=\"" + bean.getAccess() + "\"");
			
			
			if(bean.isOutput())
				sb.append(" output=\"true\"");
			else
				sb.append(" output=\"false\"");
			
			if(bean.getReturnType().length() > 0)
			sb.append(" returntype=\"" + bean.getReturnType() + "\"");
			
			if(bean.getRoles().length() > 0)
			sb.append(" roles=\"" + bean.getRoles() + "\"");
			
			sb.append(">");
			
			//now see if there are arguments to this function and write them out
			//if need be
			
			if(bean.getArgumentBeans().size() > 0)
			{
				for(Iterator iterator = bean.getArgumentBeans().keySet().iterator(); iterator.hasNext();)
				{	
					CFCArgumentBean argBean = (CFCArgumentBean)bean.getArgumentBeans().get(iterator.next());
					//CFCArgumentBean argBean = (CFCArgumentBean)iterator.next();
					
					sb.append("\n\t\t");
					sb.append("<cfargument name=\"");
					sb.append(argBean.getName() + "\"");
					
					if(argBean.getDisplayName().length() > 0) {
						sb.append(" displayName=\"");
						sb.append(argBean.getDisplayName() + "\"");
					}
					
					if(argBean.getType().length() > 0){
						sb.append(" type=\"");
						sb.append(argBean.getType() + "\"");
					}
					
					if(argBean.getHint().length() > 0){
						sb.append(" hint=\"");
						sb.append(argBean.getHint() + "\"");
					}
					
					if(argBean.getDefaultVal().length() > 0){
						sb.append(" default=\"");
						sb.append(argBean.getDefaultVal() + "\"");
					}
					
					sb.append(" required=\"");
					if(argBean.isRequired())
						sb.append("true" + "\"");
					else
						sb.append("false" + "\"");
					
					sb.append(" />");
				}
			}
				
			
			sb.append("\n\t\t");
			sb.append("<!--- TODO: Implement Method --->");
			sb.append("\n\t\t");
			
			sb.append("<cfreturn />");
			
			sb.append("\n");
			sb.append("\t</cffunction>\n");
		}
		
		return sb.toString();
	}
	
	private static String getFunctionsAsCFScript(List functionBeans) {
		StringBuffer sb = new StringBuffer();

		for (Iterator iter = functionBeans.iterator(); iter.hasNext();) {
			CFCFunctionBean bean = (CFCFunctionBean) iter.next();

			sb.append("\n\t");
			if (bean.getAccess().length() > 0)
				sb.append(bean.getAccess() + " ");

			if (bean.getReturnType().length() > 0)
				sb.append(bean.getReturnType() + " ");

			sb.append("function " + bean.getName() + "(");
			// now see if there are arguments to this function and write them out
			// if need be

			if (bean.getArgumentBeans().size() > 0) {
				for (Iterator iterator = bean.getArgumentBeans().keySet().iterator(); iterator.hasNext();) {
					CFCArgumentBean argBean = (CFCArgumentBean) bean.getArgumentBeans().get(iterator.next());
					// CFCArgumentBean argBean = (CFCArgumentBean)iterator.next();

					if (argBean.isRequired())
						sb.append("required ");

					if (argBean.getType().length() > 0) {
						sb.append(argBean.getType());
						sb.append(" ");
					}
					sb.append(argBean.getName());

					if (argBean.getDefaultVal().length() > 0) {
						sb.append("=\"");
						sb.append(argBean.getDefaultVal() + "\"");
					}

					if (argBean.getDisplayName().length() > 0) {
						sb.append(" displayName=\"");
						sb.append(argBean.getDisplayName() + "\"");
					}

					if (argBean.getHint().length() > 0) {
						sb.append(" hint=\"");
						sb.append(argBean.getHint() + "\"");
					}
					if (iterator.hasNext())
						sb.append(", ");
				}
			}

			sb.append(")");

			if (bean.getDisplayName().length() > 0)
				sb.append(" displayname=\"" + bean.getDisplayName() + "\"");

			if (bean.getHint().length() > 0)
				sb.append(" hint=\"" + bean.getHint() + "\"");

			if (bean.isOutput())
				sb.append(" output=\"true\"");
			else
				sb.append(" output=\"false\"");

			if (bean.getRoles().length() > 0)
				sb.append(" roles=\"" + bean.getRoles() + "\"");

			sb.append(" {");
			sb.append("\n\t\t");
			sb.append("/* TODO: Implement Method */");
			sb.append("\n\t\t");
			sb.append("return \"\";");
			sb.append("\n");
			sb.append("\t}\n");
		}

		return sb.toString();
	}
	
	//These methods are to convert a CFC file into a CFC object, used for templating really:
	
	
}

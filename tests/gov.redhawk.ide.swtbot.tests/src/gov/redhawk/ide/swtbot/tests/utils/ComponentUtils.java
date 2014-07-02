/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.swtbot.tests.utils;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.graphiti.mm.pictograms.Diagram;

public class ComponentUtils { //SUPPRESS CHECKSTYLE INLINE
	
	/**
	 * Determines whether the given component shape is the assembly controller of its waveform diagram
	 * @param componentShape
	 * @return
	 */
	public static boolean isAssemblyController(ComponentShape componentShape) {
		Diagram diagram = DUtil.findDiagram(componentShape);
		final SoftwareAssembly sad = (SoftwareAssembly) DUtil.getBusinessObject(diagram, SoftwareAssembly.class);
		AssemblyController ac = sad.getAssemblyController();
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		if (ac != null && ac.getComponentInstantiationRef() != null) {
			return ac.getComponentInstantiationRef().getRefid().equals(ci.getId());
		}
		return false;
	}
}

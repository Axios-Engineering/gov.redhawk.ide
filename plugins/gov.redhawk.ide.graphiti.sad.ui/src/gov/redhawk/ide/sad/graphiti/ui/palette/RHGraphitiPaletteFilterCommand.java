/** 
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.palette;

import org.eclipse.gef.commands.Command;


/**
 * @since 3.2
 */
public class RHGraphitiPaletteFilterCommand extends Command {


	private String newFilter;
	private RHGraphitiPaletteFilter filter;
	private String oldFilter;

	public RHGraphitiPaletteFilterCommand(RHGraphitiPaletteFilter filter, String s) {
		this.filter = filter;
		this.newFilter = s;
	}

	public void execute() {
//		oldFilter = filter.getFilter();
//		filter.setFilter(newFilter);
//		oldName = label.getLabelContents();
//		label.setLabelContents(newName);
	}

	public void undo() {
//		filter.setFilter(oldFilter);
//		label.setLabelContents(oldName);
	}

}

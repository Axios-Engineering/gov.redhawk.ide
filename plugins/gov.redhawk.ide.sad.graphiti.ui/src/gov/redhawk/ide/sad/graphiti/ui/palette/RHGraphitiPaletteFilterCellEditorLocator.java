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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.2
 */
public class RHGraphitiPaletteFilterCellEditorLocator implements CellEditorLocator {

	private RHGraphitiPaletteFilterFigure stickyNote;

	public RHGraphitiPaletteFilterCellEditorLocator(RHGraphitiPaletteFilterFigure stickyNote) {
		setLabel(stickyNote);
	}

	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Rectangle rect = stickyNote.getClientArea();
		stickyNote.translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		rect.height += trim.height;
		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Returns the stickyNote figure.
	 */
	protected RHGraphitiPaletteFilterFigure getLabel() {
		return stickyNote;
	}

	/**
	 * Sets the Sticky note figure.
	 * 
	 * @param stickyNote
	 *            The stickyNote to set
	 */
	protected void setLabel(RHGraphitiPaletteFilterFigure stickyNote) {
		this.stickyNote = stickyNote;
	}

}
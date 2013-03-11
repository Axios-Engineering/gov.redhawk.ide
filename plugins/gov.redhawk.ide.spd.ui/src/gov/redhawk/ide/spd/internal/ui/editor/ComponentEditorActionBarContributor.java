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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.ui.editor.ScaMultipageActionBarContributor;

import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextEditor;

public class ComponentEditorActionBarContributor extends ScaMultipageActionBarContributor {

	public ComponentEditorActionBarContributor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IEditorActionBarContributor getSubActionBarContributor(final IEditorPart activeEditor) {
		if (activeEditor instanceof ComponentEditor) {
			return new SpdActionBarContributor();
		}
		return super.getSubActionBarContributor(activeEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getType(final IEditorPart activeEditor) {
		if (activeEditor instanceof ComponentEditor) {
			return "Component Editor";
		} else if (activeEditor instanceof TextEditor) {
			return "Text Editor";
		} else {
			try {
				if (activeEditor instanceof org.eclipse.wst.sse.ui.StructuredTextEditor) {
					return "XML Editor";
				}
			} catch (final NoClassDefFoundError e) {
				// PASS
			}
		}
		return "";
	}

}

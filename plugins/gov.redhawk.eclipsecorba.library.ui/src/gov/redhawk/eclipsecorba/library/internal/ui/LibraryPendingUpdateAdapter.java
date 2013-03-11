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
package gov.redhawk.eclipsecorba.library.internal.ui;

import gov.redhawk.eclipsecorba.library.ui.LibraryUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * 
 */
public class LibraryPendingUpdateAdapter extends PendingUpdateAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final Object object) {
		final ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(LibraryUIPlugin.PLUGIN_ID,
		        "icons/IdlLibrary.gif");
		return imageDescriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(final Object o) {
		return "IDL Library (Loading...)";
	}
}

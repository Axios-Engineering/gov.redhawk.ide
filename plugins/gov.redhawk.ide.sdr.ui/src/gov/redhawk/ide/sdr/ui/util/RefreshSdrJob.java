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
package gov.redhawk.ide.sdr.ui.util;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @since 3.1
 * 
 */
public class RefreshSdrJob extends Job {

	private SdrRoot sdrRoot;

	public RefreshSdrJob(final SdrRoot sdrRoot) {
		super("Refreshing SDR Root");
		setPriority(Job.LONG);
		setUser(true);
		this.sdrRoot = sdrRoot;
	}
	
	/**
	 * Refreshes the Target SDR
	 * @since 3.3
	 */
	public RefreshSdrJob() {
		this(null);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (this.sdrRoot == null) {
			this.sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
		}
		if (this.sdrRoot != null) {
			this.sdrRoot.reload(monitor);
		}
		return Status.OK_STATUS;
	}

}

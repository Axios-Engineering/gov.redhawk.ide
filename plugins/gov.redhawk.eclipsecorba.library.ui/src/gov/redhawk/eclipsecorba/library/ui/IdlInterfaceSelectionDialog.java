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
package gov.redhawk.eclipsecorba.library.ui;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * A selection dialog that allows the user to select an IDL interface.
 * @since 1.1
 */
public class IdlInterfaceSelectionDialog extends SelectionStatusDialog {

	private IdlFilteredTree filteredTree;
	private IdlLibrary library;
	private IStatus status;
	
	/**
	 * Construct a dialog against a given IdlLibrary.
	 * @param parent
	 * @param library
	 */
	public IdlInterfaceSelectionDialog(Shell parent, IdlLibrary library) {
		super(parent);
		Assert.isNotNull(library);
		this.library = library;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		final Label headerLabel = new Label(composite, SWT.NONE);
		headerLabel.setText("Select an IDL interface (? = any character, * = any string)");
		headerLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		filteredTree = new IdlFilteredTree(composite, true, library);
		GridData gd = new GridData(GridData.FILL_BOTH);
		applyDialogFont(filteredTree.getViewer().getTree());
		gd.heightHint = filteredTree.getViewer().getTree().getItemHeight() * 15; // hint to show 15 items
        filteredTree.setLayoutData(gd);
        filteredTree.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				handleSelected(selection);
			}
		});
        
        updateStatus(new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null));
		return composite;
	}

	/**
	 * Handle a selection change in the viewer.  Ensures that the correct number of items
	 * of the correct type are selected.
	 */
	protected void handleSelected(StructuredSelection selection) {
		IStatus s =  new Status(IStatus.OK, LibraryUIPlugin.PLUGIN_ID, IStatus.OK, "", null);
		
		// You can only select one item and it must be an interface
		if (selection.size() != 1) {
			s = new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null);
		} else {
			if (!(selection.getFirstElement() instanceof IdlInterfaceDcl)) {
				s = new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null);
			}
		}
		updateStatus(s);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateStatus(IStatus status) {
		this.status = status;
		super.updateStatus(status);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		if (status != null && (status.isOK() || status.getCode() == IStatus.INFO)) {
			super.okPressed();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void computeResult() {
		java.util.List< ? > selectedElements = ((StructuredSelection) filteredTree.getViewer().getSelection()).toList();
		setResult(selectedElements);
	}
	
	/**
	 * Display the contents of the target SDR interface library.
	 * 
	 * @param parent the shell
	 * @return the selected interface or null if cancel was pressed
	 */
	public static IdlInterfaceDcl create(Shell parent) {
		// No library was supplied, using the IDL Library from the SDR Root.
		IdlLibrary library =  SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary();
		return create(parent, library);
	}

	/**
	 * Display the contents of the provided interface library.
	 * 
	 * @param parent the shell
	 * @param library an idl library.
	 * @return the selected interface or null if cancel was pressed
	 */
	public static IdlInterfaceDcl create(Shell parent, IdlLibrary library) {
		IdlInterfaceSelectionDialog dialog = new IdlInterfaceSelectionDialog(parent, library);
		int result = dialog.open();
		if (result == Dialog.OK) {
			return (IdlInterfaceDcl) dialog.getFirstResult();
		} else {
			return null;
		}
	}
}

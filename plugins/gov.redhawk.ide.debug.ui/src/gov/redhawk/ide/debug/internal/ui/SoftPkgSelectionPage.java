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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrLabelProvider;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class SoftPkgSelectionPage extends WizardPage {

	private LaunchComponentWizard wizard;

	private DataBindingContext dbc = new DataBindingContext();

	protected SoftPkgSelectionPage(LaunchComponentWizard wizard) {
		super("spdSelection", "Select Soft Package", null);
		setDescription("Select the Soft Package (spd) to launch");
		this.wizard = wizard;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(2, false));

		TreeViewer viewer = new TreeViewer(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		viewer.setContentProvider(new SdrContentProvider() {
			@Override
			public boolean hasChildren(Object object) {
				if (object instanceof SoftPkg) {
					return false;
				}
				return super.hasChildren(object);
			}
		});
		viewer.setLabelProvider(new SdrLabelProvider());
		viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) element;
					if (spd.getDescriptor() != null) {
						ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
						if (type != null) {
							switch (type) {
							case DEVICE:
							case DEVICE_MANAGER:
							case DOMAIN_MANAGER:
							case RESOURCE:
							case SERVICE:
								return true;
							default:
								return false;
							}
						}
					}
				}
				return false;
			}
		} });
		viewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof SoftPkg && e2 instanceof SoftPkg) {
					SoftPkg spd1 = (SoftPkg) e1;
					SoftPkg spd2 = (SoftPkg) e2;
					return spd1.getName().compareTo(spd2.getName());
				}
				return super.compare(viewer, e1, e2);
			}
		});
		
		Group descriptionGroup = new Group(main, SWT.None);
		descriptionGroup.setText("Description");
		descriptionGroup.setLayout(new GridLayout());
		descriptionGroup.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
		final Text descriptionField = new Text(descriptionGroup, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		descriptionField.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.FILL, 100).grab(true, false).create());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selection instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) selection;
					descriptionField.setText((spd.getDescription() == null) ? "" : spd.getDescription());
				} else {
					descriptionField.setText("");
				}
			}
		});

		dbc.bindValue(ViewersObservables.observeInput(viewer), BeansObservables.observeValue(wizard, "spdContainer"));
		dbc.bindValue(ViewersObservables.observeSingleSelection(viewer), BeansObservables.observeValue(wizard, "softPkg"),
			new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {

				@Override
				public IStatus validate(Object value) {
					if (value == null) {
						return ValidationStatus.error("Must select an spd");
					}
					return ValidationStatus.ok();
				}

			}), null);

		WizardPageSupport.create(this, dbc);

		setControl(main);
	}
}
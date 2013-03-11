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
package gov.redhawk.ide.spd.internal.ui.editor.detailspart;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.spd.internal.ui.editor.ImplementationsSection;
import gov.redhawk.ide.spd.internal.ui.editor.composite.PropertyRefComposite;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.editor.ScaFormPage;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 */
public class PropertyRefDetailsPage extends ScaDetails {

	private final ImplementationsSection fSection;
	private PropertyRefComposite client;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public PropertyRefDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	/**
	 * Creates the property ref section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createPropertyRefSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Property Ref");
		section.setDescription("The 'propertyref' element is used to indicate "
		        + "a unique refid attribute that references a simple allocation property,"
		        + " defined in the package, and a property value attribute used by the domain "
		        + "Management function to perform the dependency check. This 'refid' is a DCE UUID.");

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.client = new PropertyRefComposite(section, SWT.None, toolkit);
		section.setClient(this.client);
		toolkit.adapt(this.client);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScaFormPage getPage() {
		return this.fSection.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext dataBindingContext, final EObject input) {
		final List<Binding> retVal = new ArrayList<Binding>();
		retVal.add(FormEntryBindingFactory.bind(dataBindingContext, this.client.getIdEntry(), getEditingDomain(),
		        SpdPackage.Literals.PROPERTY_REF__REF_ID, input, new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(FormEntryBindingFactory.bind(dataBindingContext, this.client.getValueEntry(), getEditingDomain(),
		        SpdPackage.Literals.PROPERTY_REF__VALUE, input, null, null));
		this.client.setEditable(SCAEditorUtil.isEditableResource(getPage(), input.eResource()));
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createPropertyRefSection(toolkit, parent);

	}

}

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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import gov.redhawk.diagram.util.FindByStubUtil;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByServiceWizardPage;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByServicePattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "Service";
	public static final String FIND_BY_SERVICE_NAME = "Service Name";

	public FindByServicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "";
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_SERVICE;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof FindByStub) {
			FindByStub findByStub = (FindByStub) mainBusinessObject;
			return FindByStubUtil.isFindByStubService(findByStub);
		}
		return false;
	}

	// DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {

		// prompt user for Service information
		FindByServiceWizardPage page = getWizardPage();
		if (page == null) {
			return null;
		}

		// get user selections
		final String serviceNameText = page.getModel().getEnableServiceName() ? page.getModel().getServiceName() : null;
		final String serviceTypeText = page.getModel().getEnableServiceType() ? page.getModel().getServiceType() : null;
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null && !page.getModel().getUsesPortNames().isEmpty()) ? page.getModel().getUsesPortNames()
			: null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null && !page.getModel().getProvidesPortNames().isEmpty()) ? page.getModel().getProvidesPortNames()
			: null;

		// create new business object
		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				findByStubs[0].setDomainFinder(domainFinder);
				if (serviceNameText != null && !serviceNameText.isEmpty()) {
					domainFinder.setType(DomainFinderType.SERVICENAME);
					domainFinder.setName(serviceNameText);
				} else if (serviceTypeText != null && !serviceTypeText.isEmpty()) {
					domainFinder.setType(DomainFinderType.SERVICETYPE);
					domainFinder.setName(serviceTypeText);
				}

				// if applicable add uses port stub(s)
				if (usesPortNames != null) {
					int i = 0; // counter
					for (String usesPortName : usesPortNames) {
						UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
						usesPortStub.setName(usesPortName);
						findByStubs[i].getUses().add(usesPortStub);
					}
				}

				// if applicable add provides port stub(s)
				if (providesPortNames != null) {
					int i = 0; // counter
					for (String providesPortName : providesPortNames) {
						ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
						providesPortStub.setName(providesPortName);
						findByStubs[i].getProvides().add(providesPortStub);
					}
				}

				// add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);

			}
		});

		addGraphicalRepresentation(context, findByStubs[0]);

		return new Object[] { findByStubs[0] };
	}

	/**
	 * Creates the FindByStub in the diagram with the provided serviceNameText or serviceTypeText. Only one should be
	 * passed in, the other should be null
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param namingServiceText
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub create(final String serviceNameText, final String serviceTypeText, final IFeatureProvider featureProvider, final Diagram diagram) {

		// create new business object
		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				findByStubs[0].setDomainFinder(domainFinder);
				if (serviceNameText != null && !serviceNameText.isEmpty()) {
					domainFinder.setType(DomainFinderType.SERVICENAME);
					domainFinder.setName(serviceNameText);
				} else if (serviceTypeText != null && !serviceTypeText.isEmpty()) {
					domainFinder.setType(DomainFinderType.SERVICETYPE);
					domainFinder.setName(serviceTypeText);
				}

				// add to diagram resource file
				diagram.eResource().getContents().add(findByStubs[0]);

			}
		});

		return findByStubs[0];

	}

	protected static FindByServiceWizardPage getWizardPage() {
		return getWizardPage(null, new Wizard() {
			public boolean performFinish() {
				return true;
			}
		});
	}

	public static FindByServiceWizardPage getWizardPage(FindByStub existingFindByStub, Wizard wizard) {
		FindByServiceWizardPage page = new FindByServiceWizardPage();
		wizard.addPage(page);
		if (existingFindByStub != null) {
			fillWizardFieldsWithExistingProperties(page, existingFindByStub);
		}
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return page;
	}

	private static void fillWizardFieldsWithExistingProperties(FindByServiceWizardPage page, FindByStub findByStub) {
		DomainFinderType type = findByStub.getDomainFinder().getType();
		final String serviceName = findByStub.getDomainFinder().getName();
		EList<UsesPortStub> usesPorts = findByStub.getUses();
		EList<ProvidesPortStub> providesPorts = findByStub.getProvides();

		if (type.equals(DomainFinderType.SERVICENAME)) {
			page.getModel().setServiceName(serviceName);
			page.getModel().setEnableServiceName(true);
			page.getModel().setEnableServiceType(false);
		} else {
			page.getModel().setServiceType(serviceName);
			page.getModel().setEnableServiceType(true);
			page.getModel().setEnableServiceName(false);
		}
		if (usesPorts != null && !usesPorts.isEmpty()) {
			for (UsesPortStub port : usesPorts) {
				page.getModel().getUsesPortNames().add(port.getName());
			}
		}
		if (providesPorts != null && !providesPorts.isEmpty()) {
			for (ProvidesPortStub port : providesPorts) {
				page.getModel().getProvidesPortNames().add(port.getName());
			}
		}
	}

	@Override
	public String getOuterTitle(FindByStub findByStub) {
		// service name/type
		String displayOuterText = "";
		if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME)) {
			displayOuterText = NAME + " Name";
		} else if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE)) {
			displayOuterText = NAME + " Type";
		}
		return displayOuterText;
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
	}
	
	@Override
	public void setInnerTitle(FindByStub findByStub, String value) {
		findByStub.getDomainFinder().setName(value);
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return null;
	}
	
	
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape containerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(containerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof FindByStub && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean update(IUpdateContext context) {
		// TODO: Catch calls from the edit context wizard
		return super.update(context);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// TODO: Catch calls from the edit context wizard
		return super.updateNeeded(context);
	}
	
	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}

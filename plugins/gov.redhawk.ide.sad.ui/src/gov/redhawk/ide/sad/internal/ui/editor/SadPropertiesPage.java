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
package gov.redhawk.ide.sad.internal.ui.editor;

import gov.redhawk.ide.sad.internal.ui.properties.PropertiesContentProvider;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewer;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerControlFactory;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerConverter;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerLabelProvider;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerModelConverter;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerControlFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerEditAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * 
 */
public class SadPropertiesPage extends ScaFormPage {

	private PropertiesViewer viewer;
	private ViewerModelConverter model = new ViewerModelConverter();
	private IAction expandAllAction = new Action() {
		{
			setToolTipText("Expand All");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		}
		public void run() {
			viewer.expandAll();
		}
	};
	private IAction collapseAllAction = new Action() {
		{
			setToolTipText("Collapse All");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
			setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
		}
		public void run() {
			viewer.collapseAll();
		}
	};
	private IAction configureAction;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public SadPropertiesPage(SCAFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * @param editor
	 * @param id
	 * @param title
	 * @param newStyleHeader
	 */
	public SadPropertiesPage(SCAFormEditor editor, String id, String title, boolean newStyleHeader) {
		super(editor, id, title, newStyleHeader);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.setText("Properties");
		createViewer(managedForm, managedForm.getForm().getBody());
		addActions(managedForm.getForm().getToolBarManager());
		viewer.getControl().setFocus();
		super.createFormContent(managedForm);
	}

	private void addActions(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator("Action"));
		toolBarManager.add(expandAllAction);
		toolBarManager.add(collapseAllAction);
		if (configureAction != null) {
			toolBarManager.add(configureAction);
		}
	}

	private void createViewer(IManagedForm managedForm, Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		viewer = new PropertiesViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewer.setContentProvider(new PropertiesContentProvider());
		PropertiesViewerLabelProvider labelProvider = new PropertiesViewerLabelProvider(viewer);
		viewer.setLabelProvider(labelProvider);
		
		XViewerControlFactory cFactory = new PropertiesViewerControlFactory();
		XViewerConverter converter = new PropertiesViewerConverter(labelProvider);
		viewer.setXViewerEditAdapter(new XViewerEditAdapter(cFactory,
				converter));
		
		configureAction = viewer.getCustomizeAction();
		
		model.setViewer(viewer);
		viewer.setInput(model);
		viewer.expandToLevel(2);
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ui.editor.ScaFormPage#refresh(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	protected void refresh(Resource resource) {
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		model.setSoftwareAssembly(sad);
	}
	
	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}
	
	@Override
	protected Control getFocusControl() {
		if (viewer != null) {
			return viewer.getControl();
		}
		return super.getFocusControl();
	}
	
	

}
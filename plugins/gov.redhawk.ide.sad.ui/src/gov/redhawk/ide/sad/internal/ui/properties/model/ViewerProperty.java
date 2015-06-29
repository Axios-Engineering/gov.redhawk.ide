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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import java.util.Collection;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * 
 */
public abstract class ViewerProperty< T extends AbstractProperty > extends ViewerItemProvider {

	protected final T def;
	private Object parent;
	private ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	/**
	 * 
	 */
	public ViewerProperty(T def, Object parent) {
		this.def = def;
		this.parent = parent;
	}

	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	protected void firePropertyChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).valueChanged(this);
		}
	}

	protected void fireExternalIDChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).externalIDChanged(this);
		}
	}

	public Object getParent() {
		return parent;
	}

	protected AbstractPropertyRef< ? > getValueRef() {
		if (parent instanceof ViewerComponent) {
			return ((ViewerComponent) parent).getChildRef(this.getID());
		} else if (parent instanceof ViewerStructProperty) {
			return ((ViewerStructProperty) parent).getChildRef(this.getID());
		}
		return null;
	}

	protected ExternalProperty getExternalProperty() {
		if (parent instanceof ViewerComponent) {
			return ((ViewerComponent) parent).getExternalProperty(getID());
		}
		return null;
	}

	public T getDefinition() {
		return this.def;
	}

	public SadComponentInstantiation getComponentInstantiation() {
		Object element = getParent();
		while (element != null) {
			if (element instanceof ViewerComponent) {
				return ((ViewerComponent) element).getComponentInstantiation();
			} else if (element instanceof ViewerProperty< ? >) {
				element = ((ViewerProperty< ? >) element).getParent();
			}
		}
		return null;
	}

	public String getExternalID() {
		final ExternalProperty property = getExternalProperty();
		if (property != null) {
			return property.getExternalPropID();
		}
		return null;
	}

	public String getID() {
		return def.getId();
	}

	public boolean isAssemblyControllerProperty() {
		SadComponentInstantiation compInst = getComponentInstantiation();
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
		if (sad.getAssemblyController() != null) {
			AssemblyController assemblyController = sad.getAssemblyController();
			if (assemblyController.getComponentInstantiationRef() != null) {
				if (PluginUtil.equals(compInst.getId(), assemblyController.getComponentInstantiationRef().getRefid())) {
					return true;
				}
			}
		}
		return false;
	}

	public String resolveExternalID() {
		String externalIdentifier = getExternalID();
		if (externalIdentifier != null) {
			return externalIdentifier;
		} else {
			return this.getID();
		}
	}

	public void setSadValue(Object value) {
		EditingDomain editingDomain = getEditingDomain();
		Command command = createCommand(editingDomain, SetCommand.class, "value", value);
		if (command != null && command.canExecute()) {
			editingDomain.getCommandStack().execute(command);
		}
	}

	public void setExternalID(String newExternalID) {
		if (newExternalID != null) {
			newExternalID = newExternalID.trim();
			if (newExternalID.isEmpty()) {
				newExternalID = null;
			}
		}

		EditingDomain editingDomain = getEditingDomain();
		Command command = createCommand(editingDomain, SetCommand.class, "id", newExternalID);
		if (command != null && command.canExecute()) {
			editingDomain.getCommandStack().execute(command);
		}
	}

	public abstract Object getValue();

	public abstract String getPrfValue();

	public Collection< ? > getKinds() {
		if (getParent() instanceof ViewerProperty< ? >) {
			return ((ViewerProperty< ? >) getParent()).getKinds();
		}
		return getKindTypes();
	}

	protected abstract Collection< ? > getKindTypes();

	@Override
	public Object getParent(Object object) {
		return parent;
	}

	@Override
	public EditingDomain getEditingDomain() {
		return ((ViewerItemProvider) getParent()).getEditingDomain();
	}

	@Override
	protected Object getContainer(Object feature) {
		final String stringFeature = (String)feature;
		if (stringFeature.equals("value")) {
			return getValueRef();
		} else if (stringFeature.equals("id")) {
			return getExternalProperty();
		}
		return null;
	}

	@Override
	protected Object createContainer(Object feature, Object value) {
		final String stringFeature = (String)feature;
		if (stringFeature.equals("id")) {
			ExternalProperty property = SadFactory.eINSTANCE.createExternalProperty();
			SadComponentInstantiation compInst = getComponentInstantiation();
			property.setCompRefID(compInst.getId());
			property.setPropID(getID());
			property.setExternalPropID((String) value);
			return property;
		}
		return null;
	}

	@Override
	protected Command createParentCommand(EditingDomain domain, Object feature, Object value) {
		if (((String) feature).equals("id")) {
			SadComponentInstantiation compInst = getComponentInstantiation();
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
			ExternalProperties properties = sad.getExternalProperties();
			if (properties != null) {
				return AddCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, (ExternalProperty)value);
			} else {
				properties = SadFactory.eINSTANCE.createExternalProperties();
				properties.getProperties().add((ExternalProperty) value);
				return SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, properties);
			}
		}
		return super.createParentCommand(domain, feature, value);
	}

	@Override
	protected Command createSetCommand(EditingDomain domain, Object owner, Object feature, Object value) {
		if (((String)feature).equals("id")) {
			return SetCommand.create(domain, owner, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}
}

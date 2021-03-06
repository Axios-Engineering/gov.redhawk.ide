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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.UpdateNoBoFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AddFeatureForPattern;
import org.eclipse.graphiti.pattern.DirectEditingFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ext.ServiceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete.DCDConnectionInterfaceDeleteFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete.ReleaseShapeFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.reconnect.DCDReconnectFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.update.DCDConnectionInterfaceUpdateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.update.GraphitiDcdDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.DeviceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.ServiceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DCDConnectInterfacePattern;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.ServicePattern;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.CollapseAllShapesFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.CollapseShapeFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.DisabledDeleteFeatureWrapper;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ExpandAllShapesFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ExpandShapeFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ShowConsoleFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StartFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StopFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.TerminateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiFeatureProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class DCDDiagramFeatureProvider extends AbstractGraphitiFeatureProvider {

	public DCDDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		addPattern(new DevicePattern());
		addPattern(new ServicePattern());
		addConnectionPattern(new DCDConnectInterfacePattern());
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		String shapeType = (String) context.getProperty("shapeType");
		if (shapeType != null) {
			List<IPattern> patterns = getPatterns();

			// Ensures the correct add feature is returned based on selection
			switch (shapeType) {
			case DeviceCreateFeature.SHAPE_TYPE:
				for (IPattern pattern : patterns) {
					if (pattern instanceof DevicePattern) {
						return new AddFeatureForPattern(this, pattern);
					}
				}
				break;
			case ServiceCreateFeature.SHAPE_TYPE:
				for (IPattern pattern : patterns) {
					if (pattern instanceof ServicePattern) {
						return new AddFeatureForPattern(this, pattern);
					}
				}
				break;
			default:
				break;
			}
		}

		return super.getAddFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		ICustomFeature[] parentCustomFeatures = super.getCustomFeatures(context);
		List<ICustomFeature> retList = new ArrayList<ICustomFeature>(Arrays.asList(parentCustomFeatures));

		Diagram diagram = getDiagramTypeProvider().getDiagram();
		PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length == 0) {
			return retList.toArray(new ICustomFeature[retList.size()]);
		}
		Object businessObject = DUtil.getBusinessObject(pes[0]);

		if (pes[0] instanceof Diagram) {
			// Diagram features
			retList.add(new LayoutDiagramFeature(this));
			retList.add(new ExpandAllShapesFeature(this));
			retList.add(new CollapseAllShapesFeature(this));
		} else if (pes[0] instanceof RHContainerShape) {
			// Our standard shape features
			retList.add(new ExpandShapeFeature(this));
			retList.add(new CollapseShapeFeature(this));

			if (businessObject instanceof FindByStub) {
				// findby features
				retList.add(new FindByEditFeature(this));
			} else if (businessObject instanceof DcdComponentInstantiation) {
				if (DUtil.isDiagramRuntime(diagram)) {
					// Device/service runtime features
					retList.add(new StartFeature(this));
					retList.add(new StopFeature(this));
					retList.add(new ShowConsoleFeature(this));
					retList.add(new LogLevelFeature(this));

					// Add terminate, but not for the explorer
					if (!DUtil.isDiagramExplorer(diagram)) {
						retList.add(new TerminateFeature(this));
					}
				}
			}
		}

		return retList.toArray(new ICustomFeature[retList.size()]);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		// Search for shapes that we don't want the user to have move capability
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, 
			RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE})) {
			return new DefaultMoveShapeFeature(this) {
				public boolean canMoveShape(IMoveShapeContext context) {
					return false;
				}
			};
		}

		return super.getMoveShapeFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		if (context.getPictogramElement() instanceof Connection) {
			return new DCDConnectionInterfaceUpdateFeature(this);
		} else if (context.getPictogramElement() instanceof Diagram) {
			return new GraphitiDcdDiagramUpdateFeature(this);
		}

		// hide update icon for some pictogram elements
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE,
			RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE})) {
			return new UpdateNoBoFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}

		return super.getUpdateFeature(context);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		// Don't show delete for the node explorer
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		if (DUtil.isDiagramExplorer(diagram)) {
			return null;
		}

		// Search for shapes for which we don't want the user to have the delete capability, including the diagram as a
		// whole
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram || pe instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(context,
				new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER,
					RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER,
					RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
					RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE,
					RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE })) {
			return null;
		}

		// If the element to be deleted is a connection, return the proper feature
		if (pe instanceof Connection) {
			return new DCDConnectionInterfaceDeleteFeature(this);
		}

		// If the element is in the Chalkboard, its removal will be handled by the Release and Terminate features
		if (DUtil.isDiagramRuntime(diagram)) {
			if (pe instanceof DeviceShape || pe instanceof ServiceShape) {
				return new ReleaseShapeFeature(this);
			}
			return null;
		}

		// Use parent class logic, but disable the result
		IDeleteFeature deleteFeature = super.getDeleteFeature(context);
		if (deleteFeature != null && DUtil.isDiagramReadOnly(diagram)) {
			deleteFeature = new DisabledDeleteFeatureWrapper(deleteFeature);
		}
		return deleteFeature;
	}

	// the text we double click is nested inside of the pictogram element that links to our business object
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}
		IDirectEditingFeature ret = null;
		for (IPattern pattern : this.getPatterns()) {
			if (checkPattern(pattern, getBusinessObjectForPictogramElement(DUtil.findContainerShapeParentWithProperty(context.getPictogramElement(),
				RHContainerShapeImpl.SHAPE_OUTER_CONTAINER)))) {
				IPattern chosenPattern = null;
				IDirectEditingFeature f = new DirectEditingFeatureForPattern(this, pattern);
				if (checkFeatureAndContext(f, context)) {
					if (ret == null) {
						ret = f;
						chosenPattern = pattern;
					} else {
						traceWarning("getDirectEditingFeature", pattern, chosenPattern); //$NON-NLS-1$
					}
				}
			}
		}

		if (ret == null) {
			ret = getDirectEditingFeatureAdditional(context);
		}

		return ret;
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		ICreateConnectionFeature[] connectionFeatures = getCreateConnectionFeatures();
		return connectionFeatures;
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		// Search for shapes that we don't want the user to have resize capability
		if (context.getPictogramElement() instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE,
				RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER,
				RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE,
				RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE})) {
			return new DefaultResizeShapeFeature(this) {
				public boolean canResizeShape(IResizeShapeContext context) {
					return false;
				}
			};
		}

		return super.getResizeShapeFeature(context);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new DefaultRemoveFeature(this) {
			public boolean isAvailable(IContext context) {
				return false;
			}
		};
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			// We don't currently support reconnect actions for runtime
			return null;
		}

		Object businessObject = getBusinessObjectForPictogramElement(context.getOldAnchor());
		if (businessObject instanceof UsesPortStub || businessObject instanceof ProvidesPortStub || businessObject instanceof ComponentSupportedInterfaceStub) {
			return new DCDReconnectFeature(this);
		}
		return super.getReconnectionFeature(context);
	}
}

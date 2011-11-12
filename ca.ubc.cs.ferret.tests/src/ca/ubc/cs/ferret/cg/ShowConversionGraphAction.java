package ca.ubc.cs.ferret.cg;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import ca.ubc.cs.ferret.tests.FerretTestsPlugin;
import ca.ubc.cs.ferret.types.tests.TestingTypesConversionManager;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.awt.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.awt.VisualizationComponent;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.screencap.Dump;
import edu.uci.ics.screencap.EPSDump;

public class ShowConversionGraphAction extends Action implements IViewActionDelegate {
	protected IViewPart view;

	public ShowConversionGraphAction() {}

	public void run(IAction action) {
		run();
	}
	
	@Override
	public void run() {
		view.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viz(new TestingTypesConversionManager().testGetConversionGraph());
			}});
	}
	
	public <V,E> void viz(final Graph<V,E> graph) {
		final Layout<V,E> layout = new FRLayout<V,E>(graph);
		final VisualizationModel<V,E> visualizationModel = 
            new DefaultVisualizationModel<V,E>(layout, new Dimension(300,300));
		final VisualizationComponent<V, E> vv = createViewer(visualizationModel);

		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	       
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv.getServer(), 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv.getServer(), 1/1.1f, vv.getCenter());
            }
        });
        JButton deleteNode = new JButton("Delete");
        deleteNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Object selected[] = graphMouse.getSelectedObjects();
            	if(selected != null && selected.length > 0) {
            		deleteNode(vv, selected);
            	}
            }
        });
        JButton snapshot = new JButton("Save...");
        snapshot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//				SVG exporting doesn't work too well
//                saveAsSVG(visualizationModel);
                saveAsEPS(vv);
            }
        });
        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        controls.add(zoomControls);
        controls.add(modeBox);
        controls.add(deleteNode);
        controls.add(snapshot);

		JFrame frame = new JFrame("Type Conversion Graph");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(gzsp);
		frame.getContentPane().add(controls, BorderLayout.SOUTH);

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				layout.reset();
				vv.repaint();
			}
		});

		frame.pack();
		frame.setVisible(true);
	}

	protected <V, E> VisualizationComponent<V, E> createViewer(
			final VisualizationModel<V, E> visualizationModel) {
		final VisualizationComponent<V,E> vv = new VisualizationComponent<V, E>(visualizationModel);
		        vv.setBackground(Color.WHITE);
//        VertexLabelAsShapeRenderer<V,E> vlasr = 
//        	new VertexLabelAsShapeRenderer<V,E>(vv.getRenderContext());
 
		vv.setPreferredSize(new Dimension(1000,800));
		vv.getRenderContext().setVertexDrawPaintTransformer(new ConstantTransformer<V,Paint>(Color.GRAY));
		vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer<E,Paint>(Color.GRAY));
		vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer<E,Paint>(Color.GRAY));
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer<E,Paint>(Color.GRAY));
		vv.getRenderContext().setEdgeStrokeTransformer(
				new ConstantTransformer<E,Stroke>(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					      BasicStroke.JOIN_MITER, 10.0f)));
//		vv.getRenderContext().setVertexShapeTransformer(vlasr);
		vv.getRenderContext().setVertexFillPaintTransformer(new ConstantTransformer<V,Paint>(Color.WHITE));
//        vv.getRenderer().setVertexLabelRenderer(vlasr);
//        vv.getRenderContext().setVertexLabelTransformer(
//        		// this chains together Transformers so that the html tags
//        		// are prepended to the toString method output
//        		new ChainedTransformer<V,String>(new Transformer[]{
//        				new Transformer<V, String>() {
//        					public String transform(V input) {
//        						String value = input.toString();
//        						int lastDot = value.lastIndexOf('.');
//        						return lastDot < 0 ? value : value.substring(lastDot + 1);
//        					}},
//        		new Transformer<String,String>() {
//					public String transform(String input) {
//						return "<html><center><p>&nbsp;"+input +"&nbsp;</p></center></html>";
//					}}}));
		vv.getRenderContext().setVertexLabelTransformer(
				new Transformer<V, String>() {
					public String transform(V input) {
						String value = input.toString();
						int lastDot = value.lastIndexOf('.');
						return lastDot < 0 ? value : value.substring(lastDot + 1);
					}});
//				new ToStringLabeller()
		// vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		return vv;
	}

	protected <V,E>void deleteNode(VisualizationComponent<V,E> vv, Object selected[]) {
		for(V v : vv.getServer().getPickedVertexState().getPicked()) {
			vv.getGraphLayout().getGraph().removeVertex(v);
		}
//		vv.getGraphLayout().reset();
		vv.repaint();
	}
	
	protected <V,E> void saveAsSVG(VisualizationModel<V,E> visualizationModel) {
		String fileName = getSaveFileName("Destination for SVG");
        if(fileName == null) { return; }
        
		// taken from <http://xmlgraphics.apache.org/batik/using/svg-generator.html>
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        // XXX: The following unfortunately doesn't work: nothing is produced.
        VisualizationComponent<V,E> vv = createViewer(visualizationModel);
        vv.setDoubleBuffered(false);
        vv.setVisible(true);
        vv.paint(svgGenerator);
        svgGenerator.setSVGCanvasSize(vv.getPreferredSize());
               	
        boolean useCss = true;	// use CSS style attributes
        try {
	        Writer out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
	        try {
	        	svgGenerator.stream(out, useCss);
	        } catch (SVGGraphics2DIOException e) {
	        	ErrorDialog.openError(view.getSite().getShell(), "Error generating SVG", "Could not generate SVG", 
	        			new Status(IStatus.ERROR, FerretTestsPlugin.pluginID, "Could not generate SVG to " + fileName, e));
			} finally {
	        	try { out.close(); } catch(IOException e) {/*ignore*/}
	        }
        } catch(FileNotFoundException e) {
        	ErrorDialog.openError(view.getSite().getShell(), "File not found", "Could not generate SVG", 
        			new Status(IStatus.ERROR, FerretTestsPlugin.pluginID, "Could not generate SVG to " + fileName, e));
        } catch (UnsupportedEncodingException e) {
        	ErrorDialog.openError(view.getSite().getShell(), "Unsupported encoding", "Could not generate SVG", 
        			new Status(IStatus.ERROR, FerretTestsPlugin.pluginID, "Could not generate SVG", e));
		}
	}

	protected void saveAsEPS(JComponent p) {
		String fileName = getSaveFileName("Destination for EPS");
        if(fileName == null) { return; }
        
        File out = new File(fileName);
        try {
        	Dump d = new EPSDump();
        	d.dumpComponent(out, p);
        } catch (IOException e) {
        	ErrorDialog.openError(view.getSite().getShell(),
		    "Error generating EPS", "Could not generate EPS", 
		    new Status(IStatus.ERROR, FerretTestsPlugin.pluginID,
			"Could not generate EPS to " + fileName, e));
        }
	}
	

	private String getSaveFileName(final String text) {
		final String name[] = new String[1];
		view.getSite().getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
		        FileDialog dialog = new FileDialog(view.getSite().getShell(), SWT.SAVE);
		        dialog.setText(text);
		        name[0] = dialog.open();
			}});
		return name[0];
	}

	public void init(IViewPart view) {
		this.view = view;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	class ConstantTransformer<I,O> implements Transformer<I,O> {
		protected O value;
		public ConstantTransformer(O v) {
			value = v;
		}
		public O transform(I input) {
			return value;
		}
	}
}

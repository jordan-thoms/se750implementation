package pdedit.pdGraphWidget;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class WidgetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("FrameDemo");
		GraphWidget v = new GraphWidget();
		v.addDiagramEventListener(new DiagramEventAdapter() {
			public void nodeCreated(DiagramEvent d){
				System.out.println("GraphWidget says: Node Created now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void nodeChanged(DiagramEvent d){
				System.out.println("GraphWidget says: Node Changed now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void nodeSelected(DiagramEvent d){
				System.out.println("GraphWidget says: Node Selected now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void nodeRemoved(DiagramEvent d){
				System.out.println("GraphWidget says: Node removed now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void linkCreated(DiagramEvent d){
				System.out.println("GraphWidget says: link Created now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void linkSelected(DiagramEvent d){
				System.out.println("GraphWidget says: link Selected now do something");
				System.out.println(d.toString());
				System.out.println();
			}
			
			public void linkRemoved(DiagramEvent d){
				System.out.println("GraphWidget says: link removed now do something");
				System.out.println(d.toString());
				System.out.println();
			}

		});
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
		
		//check for mac
		if (System.getProperty("os.name").contains("Mac")){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		v.setNodeDescription("Type");
		v.setLinkDescription("Relation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(v);
		frame.pack();
		frame.setSize(600, 400);
		frame.setVisible(true);
	}

}

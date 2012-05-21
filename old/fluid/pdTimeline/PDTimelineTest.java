package fluid.pdTimeline;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

public class PDTimelineTest {
	
	private static int year;
	private static int month;
	private static int dayOfMonth;
	private static int hourOfDay;
	private static int minute;
	private static int second;
	
	static final int FPS_MIN = 0;
	static final int FPS_MAX = 30;
	static final int FPS_INIT = 20;
	static int prev = 0;
	
	public static void main(String[] arg) {
		ArrayList<GregorianCalendar> dates = new ArrayList<GregorianCalendar>();
		year = 1990;
		month = 1;
		dayOfMonth = 1;
		hourOfDay = 00;
		minute = 00;
		second = 00;
		for (int i = 1; i < 12; i++){
			dates.add(new GregorianCalendar(year+i, month+i, dayOfMonth, hourOfDay, minute, second));
			dates.add(new GregorianCalendar(year+i, month+i, dayOfMonth+i, hourOfDay, minute, second));
			dates.add(new GregorianCalendar(year+i, month+i, dayOfMonth+i, hourOfDay+i, minute, second));
			dates.add(new GregorianCalendar(year+i, month+i, dayOfMonth+i, hourOfDay+i, minute+i, second));
			dates.add(new GregorianCalendar(year+i, month+i, dayOfMonth+i, hourOfDay+i, minute+i, second+i));
		}
		
		for(GregorianCalendar d : dates){
			System.out.println(""+d.getTime());
		}
		
		final Timeline t = new Timeline(TimelineOrientation.Vertical);
		t.addTimelineListener(new TimelineListener() {
			
			public void nodeSelected(TimelineEvent t) {
				System.out.println("node");
			}
		});
		t.setDates(dates);
		
		final JScrollPane scrollPane = new JScrollPane(t);
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
		JFrame frame = new JFrame("FrameDemo");
		
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("Hours") );
		labelTable.put( new Integer( FPS_MAX/3 ), new JLabel("Days") );
		labelTable.put( new Integer( (2*FPS_MAX)/3  ), new JLabel("Months") );
		labelTable.put( new Integer( FPS_MAX ), new JLabel("Years") );
		slider.setLabelTable( labelTable );

		slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(FPS_MAX/3);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		frame.getContentPane().add(scrollPane);
		frame.getContentPane().add(slider);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(600, 400);
		frame.setVisible(true);
		
	}
}

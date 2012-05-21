package pdstore.ui.historyview;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class PDHistoryViewerApp
{
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}
        JFrame frame = new PDHistoryView();
        frame.show();
    }
}

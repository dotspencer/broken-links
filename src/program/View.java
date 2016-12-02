package program;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class View {

	public static void main(String[] args) {
		new View(300, 300);
	}
	
	public View(int width, int height){
		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//frame.setVisible(true);
		
		new Crawler();
	}

}

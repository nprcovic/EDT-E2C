package control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class EdtVBox extends VBox {
	
	static int VBOX_SPACING = 30;
	static int VBOX_PADDING = 10;
	
	public EdtVBox(int spacing) {
		super(spacing);
		setAlignment(Pos.CENTER);
		this.setPadding(new Insets(VBOX_PADDING));
	}
	
	public EdtVBox() {
		this(VBOX_SPACING);
	}

}

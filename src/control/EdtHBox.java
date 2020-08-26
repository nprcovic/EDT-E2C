package control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class EdtHBox extends HBox {
	
	static int HBOX_SPACING = 40;
	static int HBOX_PADDING = 10;
	
	public EdtHBox(int spacing) {
		super(spacing);
		setAlignment(Pos.CENTER);
		this.setPadding(new Insets(HBOX_PADDING));
	}
	public EdtHBox() {
		this(HBOX_SPACING);
	}

}

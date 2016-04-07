package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

		private Stage myStage;

		/**
		 * Sets up a stage to launch our window and initializes the splash screen.
		 * @param stage
		 */

		public void start (Stage stage) {

			myStage = stage;
			myStage.setTitle("VOOGA");
			myStage.setWidth(700);
			myStage.setHeight(700);
			Vooga vooga = new Vooga(myStage);
			myStage.setScene(vooga.init());
			myStage.show();
		}

		/**
		 * Launches our program.
		 * @param args
		 */

		public static void main (String[] args) {
			launch(args);
		}

}

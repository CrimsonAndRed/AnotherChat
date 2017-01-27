import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnotherChat extends Application {
	private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/ui/fxml/main.fxml"));
			Scene scene = new Scene(root, 640, 480);
			primaryStage.setScene(scene);
			primaryStage.setTitle("AnotherChat v1.0");
			primaryStage.show();
		} catch (Exception ex) {
			logger.error("GUI Error");
			logger.error(ex);
		}
	}
}

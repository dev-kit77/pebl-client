module PeblClient {
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.media;
	requires javafx.controls;
	requires javafx.web;
	requires json.simple;
	requires java.net.http;

	opens app.pebl to javafx.graphics, javafx.fxml;
	opens app.pebl.friends to javafx.fxml;
	opens app.pebl.profile to javafx.fxml;
	opens app.pebl.login to javafx.fxml;
	opens app.pebl.posts to javafx.fxml;

	exports app.pebl;
}

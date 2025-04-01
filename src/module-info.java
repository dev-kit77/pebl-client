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
	opens app.pebl.connections to javafx.fxml;
	opens app.pebl.profile to javafx.fxml;
	opens app.pebl.login to javafx.fxml;
	opens app.pebl.posts to javafx.fxml;
	opens app.pebl.prompts to javafx.fxml;
	opens app.pebl.data to javafx.fxml;
	opens app.pebl.util to javafx.fxml, javafx.graphics;

	exports app.pebl;
	exports app.pebl.connections;
	exports app.pebl.profile;
	exports app.pebl.login;
	exports app.pebl.posts;
	exports app.pebl.prompts;
	exports app.pebl.data;
	exports app.pebl.util;
}

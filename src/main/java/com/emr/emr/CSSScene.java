package com.emr.emr;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;

import java.net.URL;

public class CSSScene extends Scene {

	private URL url;

	public CSSScene(Parent root, URL url) {
		super(root);
		this.url = url;
		addCSS();
	}

	public CSSScene(Parent root, double width, double height, URL url) {
		super(root, width, height);
		this.url = url;
		addCSS();
	}

	public CSSScene(Parent root, double width, double height, boolean depthBuffer, URL url) {
		super(root, width, height, depthBuffer);
		this.url = url;
		addCSS();
	}

	public CSSScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing,
			URL url) {
		super(root, width, height, depthBuffer, antiAliasing);
		this.url = url;
		addCSS();
	}

	private void addCSS() {
		getStylesheets().add(url.toExternalForm());
	}
}

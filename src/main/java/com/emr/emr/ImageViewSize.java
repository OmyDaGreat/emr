package com.emr.emr;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewSize extends ImageView {
	public ImageViewSize(Image i) {
		super(i);
		setPreserveRatio(true);
		setFitWidth(100);
	}
}

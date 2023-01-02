package com.ryd.banner.viewpager2;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 1/2/23 12:34 AM
 * @desc : com.ryd.banner.viewpager2
 */
public class ViewModel {

    private String text;
    private int image;

    public ViewModel(String text, int image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

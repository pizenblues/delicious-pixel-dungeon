package com.shatteredpixel.shatteredpixeldungeon.ui;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
public class ClickableArea extends Button {

    protected NinePatch bg;
    protected RenderedTextBlock text;
    protected Image icon;

    public ClickableArea(String label ) {
        this(label, 9);
    }

    public ClickableArea(String label, int size ){
        super();

        bg = Chrome.get( Chrome.Type.NOBG_BUTTON );
        addToBack( bg );

        text = PixelScene.renderTextBlock( size );
        text.text( label );
        add( text );
    }

    @Override
    protected void layout() {

        super.layout();

        bg.x = x;
        bg.y = y;
        bg.size( width, height );
        int bottomPadding = 8;

        float componentWidth = 0;

        if (icon != null) componentWidth += icon.width() + 2;

        if (text != null && !text.text().equals("")){
            componentWidth += text.width() + 2;

            text.setPos(
                    x + (width() + componentWidth)/2f - text.width() - 1,
                    y + (height() - text.height() - bottomPadding)
            );
            PixelScene.align(text);

        }

        if (icon != null) {

            icon.x = x + (width() - componentWidth)/2f + 1;
            icon.y = y + (height() - icon.height() - bottomPadding);
            PixelScene.align(icon);
        }

    }

    @Override
    protected void onPointerDown() {
        bg.brightness( 1.2f );
        Sample.INSTANCE.play( Assets.Sounds.CLICK );
    }

    @Override
    protected void onPointerUp() {
        bg.resetColor();
    }

    public void enable( boolean value ) {
        active = value;
        text.alpha( value ? 1.0f : 0.3f );
        if (icon != null) icon.alpha( value ? 1.0f : 0.3f );
    }

    public void text( String value ) {
        text.text( value );
        layout();
    }

    public String text(){
        return text.text();
    }

    public void textColor( int value ) {
        text.hardlight( value );
    }

    public void icon( Image icon ) {
        if (this.icon != null) {
            remove( this.icon );
        }
        this.icon = icon;
        if (this.icon != null) {
            add( this.icon );
            layout();
        }
    }

    public Image icon(){
        return icon;
    }

    public void alpha(float value){
        if (icon != null) icon.alpha(value);
        if (bg != null)   bg.alpha(value);
        if (text != null) text.alpha(value);
    }

    public float reqWidth() {
        float reqWidth = 0;
        if (icon != null){
            reqWidth += icon.width() + 2;
        }
        if (text != null && !text.text().equals("")){
            reqWidth += text.width() + 2;
        }
        return reqWidth;
    }

    public float reqHeight() {
        float reqHeight = 0;
        if (icon != null){
            reqHeight = Math.max(icon.height() + 4, reqHeight);
        }
        if (text != null && !text.text().equals("")){
            reqHeight = Math.max(text.height() + 4, reqHeight);
        }
        return reqHeight;
    }
}

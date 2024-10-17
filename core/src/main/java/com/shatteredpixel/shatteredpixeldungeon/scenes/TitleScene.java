/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ClickableArea;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.DeviceCompat;

public class TitleScene extends PixelScene {
	private Image background;
	@Override
	public void create() {
		super.create();
		String currentClass = "none";

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1},
				new float[]{1},
				false
		);

		uiCamera.visible = false;
		int maxWidth = Camera.main.width;
		int maxHeight = Camera.main.height;
		int topPadding = landscape() ? 50 : 30;
		float scaleGraph = landscape() ? 0.5f : 0.85f;

		background = new Image(TextureCache.createSolid(0xFF1f102a), 0, 0, 800, 800);
		background.scale.set(scaleGraph);
		background.x = (maxWidth - background.width())/2f;
		background.y = ((maxHeight - background.height() + topPadding)/2f);
		PixelScene.align(background);

		if (GamesInProgress.checkAll().size() != 0){
			currentClass = GamesInProgress.checkAll().get(GamesInProgress.checkAll().size() - 1).heroClass.name();
		}

		try {
			background.texture("splashes/"+ currentClass + "bg.png");
		} catch (Exception e){
			Game.reportException(e);
			background.texture(TextureCache.createSolid(0xFF1f102a));
			background.frame(0, 0, 800, 800);
		}
		add(background);

		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );
		float topRegion = Math.max(title.height - 6, maxHeight*0.45f);
		title.x = (maxWidth - title.width()) / 2f;
		title.y = landscape() ? 8 : ((topRegion - title.height() + 40) / 2f);
		align(title);

		ClickableArea btnPlay = new ClickableArea(Messages.get(this, "enter")){
			@Override
			protected void onClick() {
				if (GamesInProgress.checkAll().size() == 0){
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
				} else {
					ShatteredPixelDungeon.switchNoFade( StartScene.class );
				}
			}

			@Override
			protected boolean onLongClick() {
				//making it easier to start runs quickly while debugging
				if (DeviceCompat.isDebug()) {
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
					return true;
				}
				return super.onLongClick();
			}
		};
		btnPlay.icon(Icons.get(Icons.ARROW));
		add(btnPlay);

		IconButton btnSettings = new IconButton(Icons.get(Icons.PREFS)){
			@Override
			public void update() {
				super.update();
			}
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().add(new WndSettings());
			}
		};
		add(btnSettings);

		IconButton btnAbout = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene( AboutScene.class );
			}
		};
		add(btnAbout);

		final int buttonSize = 24;
		int clickableArea = 140;
		int GAP = 3;

		int bottomPadding = 20;
		placeTorch(maxWidth/2, maxHeight/2 + 50);
		btnAbout.setRect(GAP, GAP, buttonSize, buttonSize);
		btnSettings.setRect(maxWidth - (buttonSize + GAP), GAP, buttonSize, buttonSize);
		btnPlay.setRect((maxWidth-120) / 2, maxHeight - clickableArea - bottomPadding, 120, clickableArea);

		BitmapText version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = maxWidth - version.width() - 4;
		version.y = maxHeight - version.height() - 2;
		version.y = maxHeight - version.height() - 2;
		add( version );

		if (DeviceCompat.isDesktop()) {
			ExitButton btnExit = new ExitButton();
			btnExit.setPos( GAP,maxWidth - 30 );
			btnSettings.setRect(btnExit.left(), GAP, buttonSize, buttonSize);
			add( btnExit );
		}

		fadeIn();
	}

	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}
}
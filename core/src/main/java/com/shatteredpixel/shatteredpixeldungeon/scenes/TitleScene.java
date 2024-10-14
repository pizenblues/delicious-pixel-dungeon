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
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;
import java.util.Date;

public class TitleScene extends PixelScene {

	private Image background;

	@Override
	public void create() {
		
		super.create();
		String currentClass;

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1},
				new float[]{1},
				false
		);

		uiCamera.visible = false;
		int w = Camera.main.width;
		int h = Camera.main.height;
		int topPadding = landscape() ? 50 : 30;
		float scaleGraph = landscape() ? 0.5f : 0.85f;

		background = new Image(TextureCache.createSolid(0xFF1f102a), 0, 0, 800, 800);
		background.scale.set(scaleGraph);
		background.x = (w - background.width())/2f;
		background.y = ((h - background.height() + topPadding)/2f);
		PixelScene.align(background);

		if (GamesInProgress.checkAll().size() != 0){
			currentClass = GamesInProgress.checkAll().get(GamesInProgress.checkAll().size() - 1).heroClass.name();
		} else {
			currentClass = "none";
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
		float topRegion = Math.max(title.height - 6, h*0.45f);
		title.x = (w - title.width()) / 2f;
		title.y = landscape() ? 8 : ((topRegion - title.height() - 20) / 2f);
		align(title);

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;
		final Chrome.Type RED_TR = Chrome.Type.RED_BUTTON;

		StyledButton btnPlay = new StyledButton(RED_TR, Messages.get(this, "enter")){
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
		btnPlay.icon(Icons.get(Icons.ENTER));
		add(btnPlay);

		StyledButton btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings"));
		add(btnSettings);

		StyledButton btnAbout = new StyledButton(GREY_TR, Messages.get(this, "about")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene( AboutScene.class );
			}
		};
		btnAbout.icon(Icons.get(Icons.SHPX));
		add(btnAbout);

		final int buttonHeight = 24;
		int buttonWidth = 60;
		int GAP = 3;
		int bottomPadding = landscape() ? 30 : 40;

		if(landscape()){
			btnPlay.setRect((Camera.main.width - buttonWidth*2) / 2, Camera.main.height - bottomPadding, buttonWidth*2, buttonHeight);
			btnAbout.setRect(GAP, GAP, buttonWidth, buttonHeight);
			btnSettings.setRect(Camera.main.width - (buttonWidth + GAP), GAP, buttonWidth, buttonHeight);
		}else{
			btnSettings.setRect((Camera.main.width - (buttonWidth*2 + GAP)) / 2, Camera.main.height - bottomPadding, buttonWidth, buttonHeight);
			btnAbout.setRect(btnSettings.right() + GAP, btnSettings.top(), buttonWidth, buttonHeight);
			btnPlay.setRect(btnSettings.left(), btnSettings.top() - (buttonHeight + GAP), buttonWidth*2, buttonHeight);
		}

		BitmapText version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = w - version.width() - 4;
		version.y = h - version.height() - 2;
		add( version );

		if (DeviceCompat.isDesktop()) {
			ExitButton btnExit = new ExitButton();
			btnExit.setPos( 3, 3 );
			add( btnExit );
		}

		fadeIn();
	}

	private static class SettingsButton extends StyledButton {
		public SettingsButton( Chrome.Type type, String label ){
			super(type, label);
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				icon(Icons.get(Icons.LANGS));
				icon.hardlight(1.5f, 0, 0);
			} else {
				icon(Icons.get(Icons.PREFS));
			}
		}

		@Override
		public void update() {
			super.update();

			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				textColor(ColorMath.interpolate( 0xFFFFFF, CharSprite.NEGATIVE, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				WndSettings.last_index = 4;
			}
			ShatteredPixelDungeon.scene().add(new WndSettings());
		}
	}

}
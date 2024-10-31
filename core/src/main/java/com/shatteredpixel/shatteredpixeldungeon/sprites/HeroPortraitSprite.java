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

package com.shatteredpixel.shatteredpixeldungeon.sprites;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class HeroPortraitSprite extends CharSprite {
    public static Image avatar( HeroClass cl, int status ) {
        Image avatar = new Image( cl.portraits() );
        RectF frame;

        if (status == 0){
            frame = avatar.texture.uvRect( 0, 0, 44, 50 );
        }else if(status == 1){
            frame = avatar.texture.uvRect( 44, 0, 88, 50 );
        }else if (status == 2){
            frame = avatar.texture.uvRect( 88, 0, 132, 50 );
        }else if (status == 3) {
            frame = avatar.texture.uvRect(132, 0, 176, 50);
        }else if(status == 4){
            frame = avatar.texture.uvRect( 0, 50, 44, 98 );
        }else{
            frame = avatar.texture.uvRect( 0, 0, 44, 50 );
        }

        frame.shift( 0,0);
        avatar.frame( frame );
        return avatar;
    }
}
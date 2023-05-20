package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import edu.cornell.gdiac.bubblebound.GameCanvas;
import edu.cornell.gdiac.bubblebound.InputController;


public class ButtonPrompt  {

    float posX;

    float posY;

    Texture[] letters;

    Vector2 scale = new Vector2(0, 0);

    boolean two;

    int mapping1;

    int mapping2 = 0;



    Texture buttonTexture;

    public ButtonPrompt(float x, float y, Texture buttonTexture, int mapping1) {
        this.posX = x;
        this.posY = y;
        this.buttonTexture = buttonTexture;
        two = false;
        this.mapping1 = mapping1;


    }


    public void setScale(Vector2 s){
        scale.set(s);
    }
    Texture left;
    Texture right;

    public void setLetters(Texture[] letters){
        this.letters = letters;
    }
    public void setMouse(Texture left, Texture right){
        this.left = left;
        this.right = right;
    }

    public ButtonPrompt(float x, float y, Texture buttonTexture, int mapping1, int mapping2) { //mapping1/mapping2 coordinate with index of mappings
        this.posX = x;                                  //e.g jump = 0, 1      left = 3, 4
        this.posY = y;
        this.buttonTexture = buttonTexture;
        two = true;
        this.mapping1 = mapping1;
        this.mapping2 = mapping2;

    }

    public boolean noButtons = false;
    public ButtonPrompt(float x, float y, Texture buttonTexture){
        posX = x;
        posY = y;
        noButtons = true;
        this.buttonTexture = buttonTexture;
    }

    public int getTexture(int x){
        if(x <= 16){ // 0-9 are 30 - 39
            return x + 23;
        }
        if(x < 23){
            return x - 19; //up,down,left,right return 0-3
        }
        return x - 25; //alphabet return 4-29
    }



    public void draw(GameCanvas canvas){
        float sx = canvas.getWidth()/1920f;
        float sy = canvas.getHeight()/1080f;
        sx /= 2;
        sy /= 2;
        float x = posX * scale.x;
        float y = posY * scale.y;

        InputController input = InputController.getInstance();
        int[] buttons = input.buttons;
        canvas.draw(buttonTexture, Color.WHITE, 0, buttonTexture.getHeight(),x, y, 0, sx, sy);
        if(two){
            //System.out.println("TWO");
            float sx2 = sx;
            float sy2 = sy;
            Texture one = letters[getTexture(buttons[mapping1])];
            if(mapping1 == 8 && input.isMouseControlls()){
                //System.out.println("mapping1 8");

                if(input.mouse){
                    //System.out.println("one left");

                    one = left;
                }else{
                    //System.out.println("one right");

                    one = right;
                }
                sx2 /= 1.7;
                sy2 /= 1.7;
            }
            if(mapping1 == 9 && input.isMouseControlls()){
                //System.out.println("mapping1 9");

                if(input.mouse){
                    //System.out.println("one right");

                    one = right;
                }else{
                    //System.out.println("one left");

                    one = left;
                }
                sx2 /= 1.7;
                sy2 /= 1.7;
            }
            //canvas.draw(one, Color.WHITE, one.getWidth()/2f, one.getHeight()/2f, x + (85 * sx), y - (132 * sy), 0,2 * sx, 2 * sy);
            if(one == left || one == right){
                canvas.draw(one, Color.WHITE, one.getWidth()/2f, one.getHeight()/2f, x + (86 * sx), y - (190 * sy), 0,1.5f * sx2, 1.5f * sy2);

            }else{
                canvas.draw(one, Color.WHITE, one.getWidth()/2f, one.getHeight()/2f, x + (86 * sx), y - (175 * sy), 0,1.5f * sx2, 1.5f * sy2);

            }

            float sx3 = sx;
            float sy3 = sy;
            Texture two = letters[getTexture(buttons[mapping2])];
            if(mapping2 == 8 && input.isMouseControlls()){
                if(input.mouse){
                    two = left;
                }else{

                    two = right;
                }
                sx3 /= 1.7;
                sy3 /= 1.7;
            }
            if(mapping2 == 9 && input.isMouseControlls()){

                if(input.mouse){

                    two = right;
                }else{

                    two = left;
                }
                sx3 /= 1.7;
                sy3 /= 1.7;
            }

            if(two == left || two == right){
                canvas.draw(two, Color.WHITE, two.getWidth()/2f, two.getHeight()/2f, x + (264 * sx), y - (190 * sy), 0,1.5f * sx3, 1.5f * sy3);
            }else{
                canvas.draw(two, Color.WHITE, two.getWidth()/2f, two.getHeight()/2f, x + (264 * sx), y - (175 * sy), 0,1.5f * sx3, 1.5f * sy3);

            }

        }else if(noButtons) {

        }else{

            if(input.isMouseControlls()) {
                Texture lc = left;
                Texture rc = right;
                if (!input.mouse) { //mouse == true mouse controls r normal
                    lc = right;
                    rc = left;
                }
                if(mapping1 == 8){ //this is for grapple/release
                    canvas.draw(lc, Color.WHITE, lc.getWidth()/2f, lc.getHeight()/2f, x + (175 * sx), y - (175 * sy), 0, sx, sy);
                }else{//
                    canvas.draw(rc, Color.WHITE, rc.getWidth()/2f, lc.getHeight()/2f, x + (175 * sx), y - (175 * sy), 0, sx, sy);
                }


            }else{
                Texture t = letters[getTexture(buttons[mapping1])];
                canvas.draw(t, Color.WHITE, t.getWidth()/ 2f, t.getHeight() / 2f, x + (175 * sx), y - (175 * sy), 0, 1.5f * sx, 1.5f * sy);
            }
        }



    }


}

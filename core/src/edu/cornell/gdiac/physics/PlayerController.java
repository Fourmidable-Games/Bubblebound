package edu.cornell.gdiac.physics;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Player core variables and FSM for platform game.
 *
 */
public class PlayerController {



    /** The initializing data (to avoid magic numbers) */
    private final JsonValue data;


    /** Cooldown (in animation frames) for shooting */
    private final int jumpLimit;

    /** Cooldown (in animation frames) for shooting */
    private final int shotLimit;



    /** Whether we are actively jumping */
    private boolean isJumping;

    /** How long until we can jump again */
    private int jumpCooldown;

    private int shootCooldown;

    /** Whether our feet are on the ground */
    private boolean isGrounded;

    /** Whether our feet were on the ground last frame */
    private boolean wasGrounded = false;

    /** Whether we are actively shooting */
    private boolean isShooting;

    /** Whether we are actively grappling */
    private boolean isGrappling;


    /** The current maximum health (in half hearts) of the player */
    public int MAX_HEALTH = 4;

    /** The current health (in half hearts) of the player */

    public int health;

    /** Whether we are actively invincible */
    public boolean isInvincible = false;

    public Timer invincibletimer = new Timer(50);

    /** Whether we are actively grappling */
    private boolean isFacingRight;

    public boolean isShooting() {
        return isShooting && shootCooldown <= 0;
    }

    public void setShooting(boolean value){
        isShooting = value;
    }

    public boolean isGrappling() {
        return isGrappling;
    }

    public void setGrappling(boolean value){
        isGrappling = value;
    }

    public boolean isJumping(){
        return isJumping && isGrounded && jumpCooldown <= 0;
    }

    public boolean justJumped(){
        return isJumping && isGrounded;
    }

    public void setJumping(boolean value){
        isJumping = value;
    }

    public boolean isGrounded(){
        return isGrounded;
    }

    public void setGrounded(boolean value){
        isGrounded = value;
    }

    public boolean justGrounded(){
        boolean output = !isGrappling && !isJumping && isGrounded && !wasGrounded;
        wasGrounded = isGrounded;
        return output;
    }

    public boolean isFacingRight(){
        return isFacingRight;
    }

    public void setFacingRight(boolean value){
        isFacingRight = value;
    }

    public boolean isInvincible(){
        return isInvincible;
    }

    public void setInvincible(boolean value){
        isInvincible = value;
    }

    public boolean isAlive(){
        return health > 0;
    }

    public int getHealth(){
        return health;
    }

    public  int getMaxHealth(){
        return MAX_HEALTH;
    }

    public PlayerController(JsonValue data){
        health = MAX_HEALTH;
        jumpLimit = data.getInt( "jump_cool", 0 );
        shotLimit = data.getInt( "shot_cool", 0 );
        this.data = data;

        shootCooldown = 0;
        jumpCooldown = 0;

        // Gameplay attributes
        isGrounded = false;
        isShooting = false;
        isJumping = false;

        invincibletimer = new Timer(50);
        isFacingRight = true;

    }

    public void update(){
        // Apply cooldowns
        if (isJumping()) {
            jumpCooldown = jumpLimit;
        } else {
            jumpCooldown = Math.max(0, jumpCooldown - 1);
        }

        if (isShooting()) {
            shootCooldown = shotLimit;
        } else {
            shootCooldown = Math.max(0, shootCooldown - 1);
        }
        if(isInvincible){
            invincibletimer.update();
            if(invincibletimer.hasFinished()){
                invincibletimer.reset();
            }
        }
    }

    public void hurt(){
        if(!isInvincible) {
            health--;
        }
    }



}

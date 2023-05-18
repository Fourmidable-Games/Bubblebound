package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import edu.cornell.gdiac.bubblebound.obstacle.BoxObstacle;
import edu.cornell.gdiac.bubblebound.obstacle.ComplexObstacle;
import edu.cornell.gdiac.bubblebound.obstacle.*;

public class Rope extends ComplexObstacle {

    Body body1;
    Body body2;
    RevoluteJoint body1_connection;

    RevoluteJoint body2_connection;

    int x;
    int y;
    double height;
    double width;
    double starting_angle;
    BoxObstacle rope;
    public Rope(Body character, Body bubble){
        body1 = character;
        body2 = bubble;
        height = Math.sqrt(Math.pow(body1.getPosition().x-body2.getPosition().x,2)+ Math.pow(body1.getPosition().y-body2.getPosition().y,2));
        // ////////////System.out.println(character.getPosition());
        Vector2 c = new Vector2(body1.getPosition());
        Vector2 c2 = new Vector2(body2.getPosition());
        c.add(c2);
        c.scl(0.5f);
        rope = new BoxObstacle(c.x,c.y,(float)height + 1,0.25f);
        // ////////////System.out.println(c);
        rope.setName("rope");
        rope.setDensity(0);
        double x_dist = body1.getPosition().x - body2.getPosition().x;
        double y_dist = body1.getPosition().y - body2.getPosition().y;
        double angle = Math.atan(y_dist/x_dist);
        // ////////////System.out.println(angle);
        // ////////////System.out.println((float)angle);
        starting_angle = angle;
        rope.setAngle((float)angle);
        rope.setBodyType(BodyDef.BodyType.DynamicBody);

        bodies.add(rope);
        // ////////////System.out.println("rope made");

    }

    protected boolean createJoints(World world){
        assert rope != null;
        rope.activatePhysics(world);
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body2;
        jointDef.bodyB = bodies.get(0).getBody();
        Vector2 anchor1 = body2.getLocalCenter();
        Vector2 anchor2 = new Vector2(-1*(body1.getPosition().x - body2.getPosition().x)/2,-1*(body1.getPosition().y - body2.getPosition().y)/2);
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        jointDef.collideConnected = false;
        Joint joint = world.createJoint(jointDef);
        joints.add(joint);

        jointDef.bodyA = body1;
        jointDef.bodyB = bodies.get(0).getBody();
        anchor1 = body1.getLocalCenter();
        anchor2 = new Vector2((body1.getPosition().x - body2.getPosition().x)/2,(body1.getPosition().y - body2.getPosition().y)/2);
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        jointDef.collideConnected = false;
        joint = world.createJoint(jointDef);
        joints.add(joint);
        return true;
    }

    public void setTexture(TextureRegion texture) {
        rope.setTexture(texture);
    }
    public TextureRegion getTexture() {
        return rope.getTexture();
    }

}

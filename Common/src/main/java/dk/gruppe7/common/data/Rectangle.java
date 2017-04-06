/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.gruppe7.common.data;

/**
 *
 * @author Mathies H
 */
public class Rectangle
{
    private int x, y, width, height;

    public Rectangle() {
    }
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public Rectangle add(int width, int height) {
        return new Rectangle(this.width + width, this.height + height);
    }
    
    public Rectangle add(Rectangle other) {
        return new Rectangle(this.width + other.width, this.height + other.height);
    }
    
    public Rectangle sub(int width, int height) {
        return new Rectangle(this.width - width, this.height - height);
    }
    
    public Rectangle sub(Rectangle other) {
        return new Rectangle(this.width - other.width, this.height - other.height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return this.width * this.height <= 0;
    }
}

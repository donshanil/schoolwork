package menu;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;


public class MenuButton {
	private String btnTitle;
	private float xPos, yPos;
	private float btnWidth, btnHeight;
	private boolean btnClicked; 
	
	public MenuButton(String title, float x, float y){
		this.btnTitle = title;
		this.xPos = x;
		this.yPos = y;
	}
	
	public void update(Input input){
		this.btnClicked = false;
		
		if (mouseInButton(input)){
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
				this.btnClicked = true;
			}
		}
	}
	
	public void render(Graphics g){
		this.btnWidth = g.getFont().getWidth(btnTitle) + 5;
		this.btnHeight = g.getFont().getHeight(btnTitle) + 5;
		g.drawRect(xPos, yPos, this.btnWidth, this.btnHeight);
		g.drawString(btnTitle, xPos+5, yPos+5);
	}
	
	public boolean mouseInButton(Input input){
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		
		if ( (mouseX >= this.xPos && mouseX <= this.xPos + this.btnWidth) &&
				(mouseY >= this.yPos && mouseY <= this.yPos + this.btnHeight) ){
			return true;
		}
		
		return false;
	}
	
	public boolean buttonClicked(){
		return btnClicked;
	}
	
}

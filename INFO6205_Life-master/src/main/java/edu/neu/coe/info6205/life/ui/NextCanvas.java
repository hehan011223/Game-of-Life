/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.coe.info6205.life.ui;


import edu.neu.coe.info6205.life.base.*;
import io.jenetics.AnyGene;
import io.jenetics.EnumGene;
import io.jenetics.Phenotype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 *
 * @author HH
 */
public class NextCanvas extends Thread{
	protected java.awt.Component app = new JFrame();
	private Game game=new Game();
	private boolean done = false;
	private int genCount = 0;
    private int MAX_GENERATION = 10;
    
	public NextCanvas (JFrame app){		
		this.app = app;		
	}
	
	public void run() {
		draw();
	}
	
	public void draw() {
		Graphics g = app.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		Dimension size = app.getSize();
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, size.width, size.height-40);
		
		g2d.setColor(Color.BLACK);
		g2d.drawRect(10, 40, size.width-20, size.height-80);
		
		for(int i = 10; i < size.width-10; i+=20) {
			g2d.drawLine(i, 40, i, size.height-40);
		}
		for(int i = 40; i < size.height-40; i+=20) {
			g2d.drawLine(10, i, size.width-10, i);
		}
		
		
		//game.run();
		Long bestfitness=0L;
    	
    	Phenotype<AnyGene<String>, Long> bestFirst = game.findbestFirst();
    	String bestPatternWithoutMutation = bestFirst.getGenotype().toString().substring(2, bestFirst.getGenotype().toString().length() - 2);
    	bestfitness=bestFirst.getFitness();
    	
    	String[] firstpatterns=bestPatternWithoutMutation.split(",");
    	for(String s:firstpatterns) {
    		String[] ss=s.split(" ");
    		int n1=Integer.parseInt(ss[0]);
        	int n2=Integer.parseInt(ss[1]);
        	if(n1>20||n2>20||n1<-20||n2<-20) continue;
        	paintPoint(g2d,n1,n2);
    	}
    	
    	
    	long nowfitness=0L;
        Phenotype<EnumGene<Integer>, Long> best=game.bestMutation(bestFirst);	
        nowfitness=best.getFitness();
        Phenotype<EnumGene<Integer>, Long> bestsave=best;
        
        
        int count=1,extinction=0;
        
        while(nowfitness<1000) {
        	if(extinction>0) break;
        	//count<10||nowfitness>bestfitness
        	best=game.nextbestMutation(best);
        	if(best.getFitness()==0) extinction++;
        	if(best.getFitness()>bestsave.getFitness())
        		bestsave=best;
        	nowfitness=best.getFitness();
        	count++;
        	
        	if(nowfitness>=bestfitness) {
        	g2d.setColor(Color.WHITE);
    		g2d.fillRect(0, 0, size.width, size.height-40);
    		g2d.setStroke(new BasicStroke(0.30f));
    		g2d.setColor(Color.BLACK);
    		g2d.drawRect(10, 40, size.width-20, size.height-80);
    		
    		
    		for(int i = 10; i < size.width-10; i+=20) {
    			g2d.drawLine(i, 40, i, size.height-40);
    		}
    		for(int i = 40; i < size.height-40; i+=20) {
    			g2d.drawLine(10, i, size.width-10, i);
    		}
            
        	bestfitness=nowfitness;
        	
        	
        	
        	String[] patterns = best.getGenotype().toString().substring(1, best.getGenotype().toString().length() - 1).split("\\|");
	        String pattern = "";
	        
	        
	        for (int i = 0; i < patterns.length; i += 2) {
	        	int n1=Integer.parseInt(patterns[i]);
	        	int n2=Integer.parseInt(patterns[i+1]);
	        	if(n1>20||n2>20||n1<-20||n2<-20) continue;
	        	paintPoint(g2d,n1,n2);
	        }
	        try {
                Thread.sleep(5);
            } catch (InterruptedException ex){}
        	}
        }
        
        String nextpattern = "";
        String[] nextpatterns = bestsave.getGenotype().toString().substring(1, bestsave.getGenotype().toString().length() - 1).split("\\|");
        
        for (int i = 0; i < nextpatterns.length; i += 2) {
        	nextpattern += nextpatterns[i] + " " + nextpatterns[i + 1] + " ,";
        }
        nextpattern = nextpattern.substring(0, nextpattern.length() - 1);
        
        System.out.println("The First Best from random is : " + bestPatternWithoutMutation);
        System.out.println("The First Best from random fitness is : " + bestFirst.getFitness());
        
        if(bestsave.getFitness()>bestFirst.getFitness()) {
        	System.out.println("After " + count+" times mutation get the best situation.");
        	System.out.println("The Best Pattern : " + nextpattern);
	        System.out.println("The Best Fitness : " + bestsave.getFitness());
        }
        else {
        	System.out.println("Doesn't get better result after Mutation");
        }
        
		/*for(Point point:plist) {
			genCount++;
			paintPoint(g2d,point.getX(),point.getY());
			
			try {
                Thread.sleep(1);
            } catch (InterruptedException ex){}
            
			
			//if (genCount > MAX_GENERATION) done = true;
		}*/
        //paintPoint(g2d,0,0);
        
	}
	
	private void paintPoint(Graphics2D g2d, int startx, int starty) {
		g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(10.0f));
		g2d.drawLine(410+startx*20,440+ starty*20, 410+startx*20, 440+starty*20);

	}
}

package edu.neu.coe.info6205.life.base;

import edu.neu.coe.info6205.life.library.Library;
import edu.neu.coe.info6205.life.ui.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import io.jenetics.*;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.Factory;

public class Game implements Generational<Game, Grid>, Countable, Renderable {

		/**
		 * Method to get the cell count.
		 *
		 * @return the number of live cells.
		 */
		@Override
		public int getCount() {
				return grid.getCount();
		}

		@Override
		public String toString() {
				return "Game{" +
								"grid=" + grid +
								", generation=" + generation +
								'}';
		}

		/**
		 * Method to test equality, ignoring generation.
		 *
		 * @param o the other Game.
		 * @return true if this and o are equivalent.
		 */
		@Override
		public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof Game)) return false;
				Game game = (Game) o;
				return grid.equals(game.grid);
		}

		/**
		 * Method to generate a hashCode, ignoring generation.
		 *
		 * @return hashCode for this.
		 */
		@Override
		public int hashCode() {
				return Objects.hash(grid);
		}

		@Override
		public Game generation(BiConsumer<Long, Grid> monitor) {
				monitor.accept(generation, grid);
				return new Game(generation + 1, grid.generation(this.monitor), this, this.monitor);
		}

		public Game(long generation, BiConsumer<Long, Group> monitor) {
				this(generation, new Grid(generation), null, monitor);
		}

		public Game(long generation) {
				this(generation, (l, g) -> {
				});
		}

		public Game() {
				this(0L);
		}

		@Override
		public String render() {
				return grid.render();
		}

		/**
		 * Get the (unique) Group belonging to the grid.
		 *
		 * @return a Group.
		 */
		public Group getGroup() {
				return grid.getGroup();
		}
		
		public Grid getGrid() {
			return grid; 
		}

		/**
		 * Method to get a very crude measure of growth rate based on this Game and the very first Game in the series.
		 *
		 * @return a double which will be zero for expired Games and 1.0 for stable games.
		 * Anything else suggests sustained growth.
		 */
		public double growthRate() {
				Game game = this;
				while (game.previous != null) {
						game = game.previous;
				}
				long growth = (long) getCount() - game.getCount();
				long generations = generation - game.generation;
				return generations > 0 ? growth * 1.0 / generations : -0.1;
		}

		public static final int MaxGenerations = 1000;

		/**
		 * Main program for Game of Life.
		 * @param args the name of the starting pattern (defaults to "Blip")
		 */
		private static long fitness(final String pattern) {
	        final Behavior generations = run(0L, pattern);
	        return generations.generation;
	    }

	    private static int eval(Genotype<IntegerGene> gt) {
	        return gt.getChromosome()
	                .as(IntegerChromosome.class)
	                .intValue();
	    }

	    private static String RandomString() {
	        Random random = new Random();
	        int length = random.nextInt(50);
	        while (length % 2 != 0 || length < 1) {
	            length = random.nextInt(50);
	        }
	        
	        String str="";
	        Set<int[]> set=new HashSet<>();
	        Random ran=new Random();
	        for(int i=0;i<length/2;i++) {
	        	int[] nums=new int[2];
	        	nums[0]=ran.nextInt(40)-20;
	        	nums[1]=ran.nextInt(40)-20;
	        	while(set.contains(nums)) {
	        		nums[0]=ran.nextInt(40)-20;
		        	nums[1]=ran.nextInt(40)-20;
	        	}
	        	set.add(nums);
	        	str+=nums[0]+" "+nums[1]+" ,";
	        }
	        return str.substring(0,str.length()-2);
	    }
	    
	    public static Phenotype<AnyGene<String>, Long> findbestFirst() {
	        final Engine<AnyGene<String>, Long> engine = Engine // Create a new builder with the given fitness
	                .builder(
	                        Game::fitness, Codecs.ofScalar(Game::RandomString))
	                .optimize(Optimize.MAXIMUM)
	                .build();
//	        final EvolutionStatistics<Double, ?>
//	                statistics = EvolutionStatistics.ofNumber();
	        final Phenotype<AnyGene<String>, Long> bestFirst = engine.stream()
	                .limit(10)
	                .collect(toBestPhenotype());
//	        Map<Long, String> res = new HashMap<>();
//	        res.put(bestFirst.getFitness(), bestFirst.getGenotype().toString().substring(2, bestFirst.getGenotype().toString().length() - 2));
	        return bestFirst;
	    }
	    
	    public static Phenotype<EnumGene<Integer>, Long> bestMutation(Phenotype<AnyGene<String>, Long> bestFirst){
	    	String bestPatternWithoutMutation = bestFirst.getGenotype().toString().substring(2, bestFirst.getGenotype().toString().length() - 2);
	        BuildFirst gfp = BuildFirst.of(bestPatternWithoutMutation);
	        
	        Engine<EnumGene<Integer>, Long> engine = Engine
	                .builder(gfp)
	                .optimize(Optimize.MAXIMUM)
	                .alterers(
	                        new Mutator<>(0.2),
	                        new SinglePointCrossover<>(0.25)
	                )
	                .build();
	        EvolutionStatistics<Long, ?>
	                statistics = EvolutionStatistics.ofNumber();
	        Phenotype<EnumGene<Integer>, Long> best =
	                engine.stream()
	                        .limit(bySteadyFitness(20))
	                        .limit(10)
	                        .peek(statistics)
	                        .collect(toBestPhenotype());
	        //System.out.println(statistics);
	        
	        
	        return best;
	    }
	    
	    public static Phenotype<EnumGene<Integer>, Long> nextbestMutation(Phenotype<EnumGene<Integer>,Long> best){
	    	String[] patterns = best.getGenotype().toString().substring(1, best.getGenotype().toString().length() - 1).split("\\|");
	        String pattern = "";
	        for (int i = 0; i < patterns.length; i += 2) {
	        	if(Math.random()>0.8) continue;
	        	int n1=Integer.parseInt(patterns[i]);
	        	int n2=Integer.parseInt(patterns[i+1]);
	        	if(Math.random()>0.5) {
	        		n1=n1+(int)Math.random()*6-3;
	        		n2=n2+(int)Math.random()*6-3;
	        	}
	            pattern += n1 + " " + n2 + " ,";
	        }
	        if(pattern.length()==0) pattern="0 0 ";
	        else pattern = pattern.substring(0, pattern.length() - 1);
	        while(Math.random()>0.5) {
	        	Random random=new Random();
	        	int n1=random.nextInt(40)-20;
	        	int n2=random.nextInt(40)-20;
	        	pattern+=n1+" "+n2;
	        }
	        
	    	//String bestPatternWithoutMutation = best.getGenotype().toString().substring(2, best.getGenotype().toString().length() - 2);
	        BuildFirst gfp = BuildFirst.of(pattern);
	        
	        Engine<EnumGene<Integer>, Long> engine = Engine
	                .builder(gfp)
	                .optimize(Optimize.MAXIMUM)
	                .alterers(
	                        new Mutator<>(0.6),
	                        new SinglePointCrossover<>(0.55)
	                )
	                .build();
	        EvolutionStatistics<Long, ?>
	                statistics = EvolutionStatistics.ofNumber();
	        Phenotype<EnumGene<Integer>, Long> nextbest =
	                engine.stream()
	                        .limit(bySteadyFitness(20))
	                        .limit(100)
	                        .peek(statistics)
	                        .collect(toBestPhenotype());
	        //System.out.println(statistics);
	        return nextbest;
	    }
	    
	    public static void run() {
	    	Long bestfitness=0L;
	    	
	    	Phenotype<AnyGene<String>, Long> bestFirst = findbestFirst();
	    	String bestPatternWithoutMutation = bestFirst.getGenotype().toString().substring(2, bestFirst.getGenotype().toString().length() - 2);
	    	bestfitness=bestFirst.getFitness();
	        
	        
	        long nowfitness=0L;
	        Phenotype<EnumGene<Integer>, Long> best=bestMutation(bestFirst);	
	        nowfitness=best.getFitness();

	        Phenotype<EnumGene<Integer>, Long> bestsave=best;
	        
	        int count=1;
	        while(count<50||nowfitness>bestfitness) {
	        	bestfitness=nowfitness;
	        	best=nextbestMutation(best);
	        	if(best.getFitness()>bestsave.getFitness())
	        		bestsave=best;
	        	nowfitness=best.getFitness();
	        	count++;
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
	        
	        
	        
	        
	    }
	    
	    public static String beststr() {
	    	String s = null;
	    	return s;
	    }
	    
	    public static void runbest(Phenotype<AnyGene<String>, Long> best) {
	    	final Engine<AnyGene<String>, Long> engine = Engine.builder(
	    			Game::fitness, Codecs.ofScalar(Game::beststr))
	                .optimize(Optimize.MAXIMUM)
	                .alterers(new Mutator<>(0.55),
	                        new SinglePointCrossover<>(0.06))
	                .build();
	    	
	    }

	    public static void main(String[] args) {
//	        String patternName = args.length > 0 ? args[0] : "Test";
//	        System.out.println("Game of Life with starting pattern: " + patternName);
//	        final String pattern = Library.get(patternName);
//	        final Behavior generations = run(0L, pattern);
//	        System.out.println("Ending Game of Life after " + generations + " generations");
	    	
	        //run();
	        
	        MyApp mapp = new MyApp();
	    }

		/**
		 * Run the game starting with pattern.
		 *
		 * @param generation the starting generation.
		 * @param pattern    the pattern name.
		 * @return the generation at which the game expired.
		 */
		public static Behavior run(long generation, String pattern) {
				return run(generation, Point.points(pattern));
		}

		/**
		 * Run the game starting with a list of points.
		 *
		 * @param generation the starting generation.
		 * @param points     a list of points in Grid coordinates.
		 * @return the generation at which the game expired.
		 */
		public static Behavior run(long generation, List<Point> points) {
				return run(create(generation, points), (l, g) -> System.out.println("generation " + l + "; grid=" + g));
		}

		/**
		 * Factory method to create a new Game starting at the given generation and with the given points.
		 * @param generation the starting generation.
		 * @param points a list of points in Grid coordinates.
		 * @return a Game.
		 */
		public static Game create(long generation, List<Point> points) {
				final Grid grid = new Grid(generation);
				grid.add(Group.create(generation, points));
				BiConsumer<Long, Group> groupMonitor = (l, g) -> System.out.println("generation " + l + ";\ncount=" + g.getCount());
				return new Game(generation, grid, null, groupMonitor);
		}

		/**
		 * Method to run a Game given a monitor method for Grids.
		 *
		 * @param game        the game to run.
		 * @param gridMonitor the monitor
		 * @return the generation when expired.
		 */
		public static Behavior run(Game game, BiConsumer<Long, Grid> gridMonitor) {
				if (game == null) throw new LifeException("run: game must not be null");
				Game g = game;
				while (!g.terminated()) g = g.generation(gridMonitor);
				int reason = g.generation >= MaxGenerations ? 2 : g.getCount() <= 1 ? 0 : 1;
				return new Behavior(g.generation, g.growthRate(), reason);
		}

		/**
		 * Class to model the behavior of a game of life.
		 */
		public static class Behavior {
				/**
				 * The generation at which the run stopped.
				 */
				public final long generation;
				/**
				 * The average rate of growth.
				 */
				public final double growth;
				/**
				 * The reason the run stopped:
				 * 0: the cells went extinct
				 * 1: a repeating sequence was noted;
				 * 2: the maximum configured number of generations was reached.
				 */
				private final int reason;

				public Behavior(long generation, double growth, int reason) {
						this.generation = generation;
						this.growth = growth;
						this.reason = reason;
				}

				@Override
				public String toString() {
						return "Behavior{" +
										"generation=" + generation +
										", growth=" + growth +
										", reason=" + reason +
										'}';
				}

				@Override
				public boolean equals(Object o) {
						if (this == o) return true;
						if (!(o instanceof Behavior)) return false;
						Behavior behavior = (Behavior) o;
						return generation == behavior.generation &&
										Double.compare(behavior.growth, growth) == 0 &&
										reason == behavior.reason;
				}

				@Override
				public int hashCode() {
						return Objects.hash(generation, growth, reason);
				}
		}

		private Game(long generation, Grid grid, Game previous, BiConsumer<Long, Group> monitor) {
				this.grid = grid;
				this.generation = generation;
				this.previous = previous;
				this.monitor = monitor;
		}

		private boolean terminated() {
				return testTerminationPredicate(g -> g.generation >= MaxGenerations, "having exceeded " + MaxGenerations + " generations") ||
								testTerminationPredicate(g -> g.getCount() <= 1, "extinction") ||
								// TODO now we look for two consecutive equivalent games...
								testTerminationPredicate(Game::previousMatchingCycle, "having matching previous games");
		}

		/**
		 * Check to see if there is a previous pair of matching games.
		 * <p>
		 * NOTE this method of checking for cycles is not guaranteed to work!
		 * NOTE this method may be very inefficient.
		 * <p>
		 * TODO project teams may need to fix this method.
		 *
		 * @param game the game to check.
		 * @return a Boolean.
		 */
		private static boolean previousMatchingCycle(Game game) {
				MatchingGame matchingGame = findCycle(game);
				int cycles = matchingGame.k;
				Game history = matchingGame.match;
				if (history == null) return false;
				Game current = game;
				while (current != null && history != null && cycles > 0) {
						if (current.equals(history)) {
								current = current.previous;
								history = history.previous;
								cycles--;
						} else
								return false;
				}
				return true;
		}

		/**
		 * Find a game which matches the given game.
		 *
		 * @param game the game to match.
		 * @return a MatchingGame object: if the match field is null it means that we did not find a match;
		 * the k field is the number of generations between game and the matching game.
		 */
		private static MatchingGame findCycle(Game game) {
				int k = 1;
				Game candidate = game.previous;
				while (candidate != null && !game.equals(candidate)) {
						candidate = candidate.previous;
						k++;
				}
				return new MatchingGame(k, candidate);
		}

		private static class MatchingGame {
				private final int k;
				private final Game match;

				public MatchingGame(int k, Game match) {
						this.k = k;
						this.match = match;
				}
		}

		private boolean testTerminationPredicate(Predicate<Game> predicate, String message) {
				if (predicate.test(this)) {
						System.out.println("Terminating due to: " + message);
						return true;
				}
				return false;
		}
		
		

		private final Grid grid;
		private final Game previous;
		private final BiConsumer<Long, Group> monitor;
		private final long generation;
}

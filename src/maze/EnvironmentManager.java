package maze;

import gui.GameWindow;

/**
 * Creates the game window which displays the maze graphically. Instantiates and
 * runs the maze and the agents active in it. Passes moves from agents to the
 * maze, and returns move results to agents.
 */
public class EnvironmentManager
{
	private static GameWindow gameWindow;
	private static Agent agent;

	public static void main(String[] args)
	{
		Maze maze = new Maze();
		gameWindow = new GameWindow(maze);

		agent = new Agent(maze);
		agent.run();
	}

	/**
	 * Execute move and return result.
	 * 
	 * @param maze
	 *            maze to execute move in
	 * @param x
	 *            x coordinate of destination tile
	 * @param y
	 *            y coordinate of destination tile
	 */
	public static void executeMove(Maze maze, int x, int y, Direction direction)
	{
		int result = maze.getTileValue(x, y);
		int agentXOld = agent.getXPosition();
		int agentYOld = agent.getYPosition();
		agent.update(x, y, direction, result);
		// Trigger move animation if move was valid
		if (result != -1)
		{
			gameWindow.showMoveAnimation(x, y);
			gameWindow.updateQValue(agentXOld, agentYOld, direction, agent.getQValue(agentXOld, agentYOld, direction));
		}
	}
}

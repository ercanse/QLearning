package strategy;

import maze.Direction;

public class StrategyProfile
{
	private Strategy[][] profile;

	public StrategyProfile(int x, int y)
	{
		profile = new Strategy[x][y];
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < y; j++)
			{
				profile[i][j] = new Strategy();
			}
		}
	}

	public Direction chooseDirectionFromTile(int x, int y)
	{
		return profile[x][y].chooseDirection();
	}

	public void excludeDirectionFromTile(int x, int y, Direction direction)
	{
		profile[x][y].excludeDirection(direction);
	}
}
package main.gui;

import main.agent.Direction;
import main.maze.EnvironmentManager;
import main.maze.Maze;
import main.maze.Position;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main frame for graphical maze representation.
 */
public class GameWindow extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1903142717890981086L;

    private static final int fontStyle = Font.BOLD;
    private static final int fontSize = 18;

    private boolean simulationRunning;

    private JPanel mainPanel;
    private MazePanel mazePanel;
    private QValuesPanel qValuesPanel;

    private JLabel scoreLabel;
    private JButton goButton;
    private JButton pauseButton;

    /**
     * Creates a new window with a graphical representation of maze and the
     * corresponding Q-values.
     *
     * @param maze maze to display
     */
    public GameWindow(Maze maze) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        getContentPane().add(mainPanel);

        // Determine dimension of maze panel and Q-values panel
        int panelWidth = maze.getWidth() * MazePanel.tileWidth
                + (maze.getWidth() + 1) * MazePanel.xMargin;
        int panelHeight = maze.getHeight() * MazePanel.tileHeight
                + (maze.getHeight() + 1) * MazePanel.yMargin;

        mainPanel.add(createMazeAndQValuesPanel(maze, panelWidth, panelHeight));
        mainPanel.add(createControlsPanel(panelWidth * 2, 100));

        // Adjust size to maze dimension
        int windowWidth = panelWidth * 2 + 20;
        int windowHeight = panelHeight + 150;
        setSize(windowWidth, windowHeight);
        CoordinatePair windowLocation = getWindowLocation(windowWidth, windowHeight);
        setLocation(windowLocation.getX(), windowLocation.getY());

        setResizable(false);
        setTitle("Q-learning");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);
    }

    static CoordinatePair getWindowLocation(int windowWidth, int windowHeight) {
        // Adjust location to size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        return new CoordinatePair((int) screenWidth / 2 - windowWidth / 2,
                (int) screenHeight / 2 - windowHeight / 2);
    }

    /**
     * Creates a panel for the maze, a corresponding panel for the agent's
     * Q-values for the maze, and puts both in a container panel.
     *
     * @param maze        maze to create panels for
     * @param panelWidth  individual width of both panels
     * @param panelHeight individual width of both panels
     * @return panel containing maze and Q-values panels
     */
    private JPanel createMazeAndQValuesPanel(Maze maze, int panelWidth, int panelHeight) {
        // Create container panel
        JPanel mazeAndQValuesPanel = new JPanel();
        mazeAndQValuesPanel.setLayout(new BoxLayout(mazeAndQValuesPanel, BoxLayout.X_AXIS));

        // Create maze panel
        mazePanel = new MazePanel(maze);
        mazePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        mazeAndQValuesPanel.add(mazePanel);

        // Create Q-values panel
        qValuesPanel = new QValuesPanel(maze.getWidth(), maze.getHeight());
        qValuesPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        mazeAndQValuesPanel.add(qValuesPanel);

        return mazeAndQValuesPanel;
    }

    /**
     * @return panel containing a score label and buttons for resuming/pausing
     * the simulation
     */
    private JPanel createControlsPanel(int panelWidth, int panelHeight) {
        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Create container panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

        JPanel scorePanel = createScorePanel();
        JPanel buttonsPanel = createButtonsPanel();

        controlsPanel.add(scorePanel);
        controlsPanel.add(buttonsPanel);

        mainPanel.add(controlsPanel);

        JPanel timeControlPanel = createTimeControlPanel();
        mainPanel.add(timeControlPanel);

        return mainPanel;
    }

    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("", fontStyle, fontSize));
        updateScore(0);
        scorePanel.add(scoreLabel);

        return scorePanel;
    }

    private JPanel createTimeControlPanel() {
        JPanel timeButtonPanel = new JPanel();
        timeButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        timeButtonPanel.add(new JLabel("Time factor:"));
        JSpinner timeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        timeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                EnvironmentManager.setTimeFactor((int) timeSpinner.getValue());
            }
        });
        timeButtonPanel.add(timeSpinner);

        return timeButtonPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        goButton = new JButton("Go");
        goButton.setPreferredSize(new Dimension(70, 30));
        goButton.addActionListener(this);
        buttonsPanel.add(goButton);

        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(70, 30));
        pauseButton.addActionListener(this);
        buttonsPanel.add(pauseButton);

        return buttonsPanel;
    }

    public void processMove(Position oldPosition, Position newPosition, Direction direction,
                            int score, double utility) {
        showMoveAnimation(newPosition);
        updateScore(score);
        updateUtility(oldPosition, direction, utility);
    }

    /**
     * Calls mazePanel's move animation functionality.
     *
     * @param position destination of move that was executed
     */
    public void showMoveAnimation(Position position) {
        mazePanel.showMoveAnimation(position);
    }

    private void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Updates Q-value for action '(position, direction)' to q.
     *
     * @param position  position of tile to update Q-value for
     * @param direction direction to update Q-value for
     * @param utility   new utility value
     */
    private void updateUtility(Position position, Direction direction, double utility) {
        qValuesPanel.updateQValue(position, direction, utility);
    }

    public boolean simulationIsRunning() {
        return simulationRunning;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == goButton) {
            simulationRunning = true;
        } else if (e.getSource() == pauseButton) {
            simulationRunning = false;
        }
    }
}

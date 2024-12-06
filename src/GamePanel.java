import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;

    final int[] xCoordOfHead = new int[GAME_UNITS];
    final int[] yCoordOfHead = new int[GAME_UNITS];
    int nBodyParts = 6;
    int nApplesEaten = 0;
    int xCoordOfApple;
    int yCoordOfApple;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setVisible(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private static void drawGridForDevelopment(Graphics g) {
        g.setColor(Color.gray);
        drawHorizontalGridLines(g);
        drawVerticalGridLines(g);
    }

    private static void drawVerticalGridLines(Graphics g) {
        for (int i = 0; i <= nTotalScreenHeightUnits(); i++) {
            drawVerticalGridLine(g, i);
        }
    }

    private static void drawVerticalGridLine(Graphics g, int i) {
        g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
    }

    private static void drawHorizontalGridLines(Graphics g) {
        for (int i = 0; i <= nTotalScreenWidthUnits(); i++) {
            drawHorizontalGridLine(g, i);
        }
    }

    private static void drawHorizontalGridLine(Graphics g, int i) {
        g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
    }

    private static int nTotalScreenWidthUnits() {
        return SCREEN_WIDTH / UNIT_SIZE;
    }

    private static int nTotalScreenHeightUnits() {
        return SCREEN_HEIGHT / UNIT_SIZE;
    }

    private static int getCenterBasedOnFont(FontMetrics metrics, String text) {
        return (SCREEN_WIDTH - metrics.stringWidth(text)) / 2;
    }

    private static void setFontOnGameOverScreen(Graphics g, String fontName, int fontSize) {
        Font font = new Font(fontName, Font.BOLD, fontSize);
        g.setFont(font);
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            drawGameWhenInRunningState(g);
        } else {
            gameOver(g);
        }
    }

    private void drawGameWhenInRunningState(Graphics g) {
        drawGridForDevelopment(g);
        drawApple(g);
        drawSnake(g);
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < nBodyParts; i++) {
            drawSnakeComponent(g, i);
        }
    }

    private void drawSnakeComponent(Graphics g, int i) {
        if (i == 0) {
            drawHeadOfSnake(g, i);
        } else {
            drawBodyOfSnake(g, i);
        }
    }

    private void drawBodyOfSnake(Graphics g, int i) {
        drawSnakeBodyPart(g, new Color(45, 180, 0), i);
    }

    private void drawHeadOfSnake(Graphics g, int i) {
        drawSnakeBodyPart(g, Color.green, i);
    }

    private void drawSnakeBodyPart(Graphics g, Color color, int i) {
        g.setColor(color);
        g.fillRect(xCoordOfHead[i], yCoordOfHead[i], UNIT_SIZE, UNIT_SIZE);
    }

    private void drawApple(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(xCoordOfApple, yCoordOfApple, UNIT_SIZE, UNIT_SIZE);
    }

    public void newApple() {
        xCoordOfApple = randomlyPickAnIntFromOneTo(nTotalScreenWidthUnits());
        yCoordOfApple = randomlyPickAnIntFromOneTo(nTotalScreenHeightUnits());
    }

    private int randomlyPickAnIntFromOneTo(int totalUnits) {
        return random.nextInt(totalUnits) * UNIT_SIZE;
    }

    public void move() {
        for (int i = nBodyParts; i > 0; i--) {
            shiftXCoordinatesBackOne(i);
            shiftYCoordinatesBackOne(i);
        }

        switch (direction) {
            case 'U':
                moveUp();
                break;
            case 'D':
                moveDown();
                break;
            case 'L':
                moveLeft();
                break;
            case 'R':
                moveRight();
                break;
        }
    }

    private void shiftYCoordinatesBackOne(int i) {
        yCoordOfHead[i] = yCoordOfHead[i - 1];
    }

    private void shiftXCoordinatesBackOne(int i) {
        xCoordOfHead[i] = xCoordOfHead[i - 1];
    }

    private void moveRight() {
        xCoordOfHead[0] = xCoordOfHead[0] + UNIT_SIZE;
    }

    private void moveLeft() {
        xCoordOfHead[0] = xCoordOfHead[0] - UNIT_SIZE;
    }

    private void moveDown() {
        yCoordOfHead[0] = yCoordOfHead[0] + UNIT_SIZE;
    }

    private void moveUp() {
        yCoordOfHead[0] = yCoordOfHead[0] - UNIT_SIZE;
    }

    public void checkApple() {
        if (isHeadOfSnakeOnTopOfApple()) {
            eatApple();
        }

    }

    private boolean isHeadOfSnakeOnTopOfApple() {
        return (xCoordOfHead[0] == xCoordOfApple)
                && (yCoordOfHead[0] == yCoordOfApple);
    }

    private void eatApple() {
        nBodyParts++;
        nApplesEaten++;
        newApple();
    }

    public void checkCollisions() {
        checkIsHeadCollidingWithBodyOfSnake();
        checkIsHeadCollidingWithBorder();
    }

    private void checkIsHeadCollidingWithBorder() {
        if (isHeadCollidingWithBorderOfGame()) {
            youHaveLost();
        }
    }

    private boolean isHeadCollidingWithBorderOfGame() {
        return isHeadCollidingWithLeftBorder()
                || isHeadCollidingWithRightBorder()
                || isHeadCollidingWithTopBorder()
                || isHeadCollidingWithBottomBorder();
    }

    private boolean isHeadCollidingWithBottomBorder() {
        return yCoordOfHead[0] >= SCREEN_HEIGHT;
    }

    private boolean isHeadCollidingWithTopBorder() {
        return yCoordOfHead[0] < 0;
    }

    private boolean isHeadCollidingWithRightBorder() {
        return xCoordOfHead[0] >= SCREEN_WIDTH;
    }

    private boolean isHeadCollidingWithLeftBorder() {
        return xCoordOfHead[0] < 0;
    }

    private void checkIsHeadCollidingWithBodyOfSnake() {
        for (int i = nBodyParts; i > 0; i--) {
            if (isHeadCollidingWithBodyPartI(i)) {
                youHaveLost();
                break;
            }
        }
    }

    private void youHaveLost() {
        running = false;
        timer.stop();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        drawGameOverText(g);
        drawScoreText(g);
        drawNextStepsText(g);
    }

    private void drawNextStepsText(Graphics g) {
        setFontOnGameOverScreen(g, "Ink Free", 22);
        String gameOverInstructions = "Press SPACE to RESTART";
        g.drawString(
                gameOverInstructions,
                getCenterForText(g, gameOverInstructions),
                SCREEN_HEIGHT / 2 + 50
        );
        String gameOverInstructions2 = "Press ESC to EXIT";
        g.drawString(
                gameOverInstructions2,
                getCenterForText(g, gameOverInstructions2),
                SCREEN_HEIGHT / 2 + 75
        );
    }

    private void drawScoreText(Graphics g) {
        setFontOnGameOverScreen(g, "Ink Free", 25);
        String applesEatenText = "Apples Eaten: " + nApplesEaten;
        g.drawString(applesEatenText, getCenterForText(g, applesEatenText), SCREEN_HEIGHT / 2 + 25);
    }

    private void drawGameOverText(Graphics g) {
        String gameOverText = "Game Over";
        setFontOnGameOverScreen(g, "Ink Free", 75);
        g.drawString(gameOverText, getCenterForText(g, gameOverText), SCREEN_HEIGHT / 2 - 50);
    }

    private int getCenterForText(Graphics g, String text) {
        return getCenterBasedOnFont(getFontMetrics(g.getFont()), text);
    }

    private boolean isHeadCollidingWithBodyPartI(int i) {
        return isXCoordOfHeadEqualToXCoordOfBodyPartI(i)
                && isYCoordOfHeadEqualToYCoordOfBodyPartI(i);
    }

    private boolean isYCoordOfHeadEqualToYCoordOfBodyPartI(int i) {
        return yCoordOfHead[0] == yCoordOfHead[i];
    }

    private boolean isXCoordOfHeadEqualToXCoordOfBodyPartI(int i) {
        return xCoordOfHead[0] == xCoordOfHead[i];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}

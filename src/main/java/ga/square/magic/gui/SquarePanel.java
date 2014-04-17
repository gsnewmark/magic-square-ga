package ga.square.magic.gui;

import ga.square.magic.impl.MagicSquare;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

public class SquarePanel extends JPanel {
    private MagicSquare magicSquare;

    public void updateMagicSquare(final MagicSquare magicSquare) {
        this.magicSquare = magicSquare;
        repaint();
    }

    private void doDrawing(final Graphics g) {
        if (magicSquare != null) {
            final int squareSize = magicSquare.getSquareSize();
            final Graphics2D g2d = (Graphics2D) g;

            final Dimension size = getSize();
            final Insets insets = getInsets();

            final int width = size.width - insets.left - insets.right;
            final int height = size.height - insets.top - insets.bottom;

            final int squareWidth = width / squareSize;
            final int squareHeight = height / squareSize;

            final Map<Integer, Point> points = new HashMap<>();

            for (int i = 0; i < squareSize; ++i) {
                for (int j = 0; j < squareSize; ++j) {
                    final int v = magicSquare.getCellValue(j, i);
                    final int x = j * squareWidth + squareWidth / 2;
                    final int y = i * squareHeight + squareHeight / 2;

                    points.put(v, new Point(x, y));

                    g2d.setColor(Color.black);
                    g2d.drawString(String.valueOf(v), x, y);

                    g2d.setColor(Color.black);
                    g2d.drawRect(j * squareWidth, i * squareHeight, squareWidth, squareHeight);
                }
            }

            g2d.setColor(Color.orange);
            g2d.setStroke(new BasicStroke(2));

            for (int i = 1; i <= squareSize * squareSize; ++i) {
                final Point start = points.get(i);
                final Point end = (i == squareSize * squareSize)
                        ? points.get(1) : points.get(i + 1);

                final Path2D.Float curve = new Path2D.Float();
                curve.moveTo(start.x, start.y);
                final int xDiff = (end.x - start.x) / 4;
                final int yDiff = (end.y - start.y) / 2;
                curve.curveTo(
                        start.x + xDiff, start.y + yDiff,
                        end.x - xDiff, end.y - yDiff,
                        end.x, end.y);
                g2d.draw(curve);
            }
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
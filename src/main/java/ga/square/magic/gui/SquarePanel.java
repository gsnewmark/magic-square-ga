package ga.square.magic.gui;

import ga.square.magic.impl.MagicSquare;

import javax.swing.*;
import java.awt.*;

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
            g2d.setColor(Color.black);

            final Dimension size = getSize();
            final Insets insets = getInsets();

            final int width = size.width - insets.left - insets.right;
            final int height = size.height - insets.top - insets.bottom;

            final int squareWidth = width / squareSize;
            final int squareHeight = height / squareSize;

            for (int i = 0; i < squareSize; ++i) {
                for (int j = 0; j < squareSize; ++j) {
                    final String s = String.valueOf(magicSquare.getCellValue(j, i));
                    final int x = j * squareWidth + squareWidth / 2;
                    final int y = i * squareHeight + squareHeight / 2;
                    g2d.drawString(s, x, y);
                    g2d.drawRect(j * squareWidth, i * squareHeight, squareWidth, squareHeight);
                }
            }
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
package ga.square.magic.gui;

import ga.square.magic.GeneticAlgorithm;
import ga.square.magic.Solver;
import ga.square.magic.SolverConfiguration;
import ga.square.magic.impl.MagicSquare;
import ga.square.magic.impl.MagicSquareGA;
import ga.square.magic.impl.MagicSquareSolver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maryna
 * Date: 3/31/14
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestGui {
    private JPanel MagicSquarePanel;
    private JButton RUNButton;
    private JButton STOPButton;
    private JTextField InputGeneration;
    private JLabel Time;
    private JButton RUNDEFAULTButton;
    private JTextField InputPopultion;
    private JTextField InputPoolSize;
    private JProgressBar progressBar;
    private JTextField InputSquareSize;
    private JTextField InputCrossover;
    private JTextField InputMutation;
    private JButton RESETButton;
    private JPanel squarePanelRef;
    private JLabel currentGeneration;
    private JLabel bestFitness;

    private SquarePanel squarePanel;

    private PropertyChangeListener propertyChangeListener;

    private MagicSquareSolver s;

    public TestGui() {
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer)evt.getNewValue());
                } else if ("currentBestIndividual".equals(evt.getPropertyName())) {
                    if (evt.getNewValue() != null) {
                        if (evt.getNewValue() instanceof Solver.SolverResult) {
                            final Solver.SolverResult<MagicSquare> r =
                                    (Solver.SolverResult<MagicSquare>) evt.getNewValue();

                            squarePanel.updateMagicSquare(r.getResult());
                            currentGeneration.setText(String.valueOf(r.getGeneration()));
                            bestFitness.setText(String.valueOf(r.getFitness()));
                        }
                    }
                } else if ("totalTime".equals(evt.getPropertyName())) {
                    Time.setText(evt.getNewValue().toString());
                }
            }
        };

        RUNButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final SolverConfiguration sc = new SolverConfiguration.Builder()
                        .maxGenerations(Integer.parseInt(InputGeneration.getText()))
                        .populationSize(Integer.parseInt(InputPopultion.getText()))
                        .parentPoolSize(Integer.parseInt(InputPoolSize.getText()))
                        .crossoverProbability(Double.parseDouble(InputCrossover.getText()))
                        .mutationProbability(Double.parseDouble(InputMutation.getText()))
                        .build();
                final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3, 1);
                s = new MagicSquareSolver(a, Integer.parseInt(InputSquareSize.getText()), sc);
                s.addPropertyChangeListener(propertyChangeListener);

                s.execute();
            }
        });

        STOPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                s.cancel(true);
                s.removePropertyChangeListener(propertyChangeListener);
            }
        });


        RESETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                squarePanel.updateMagicSquare(null);
                currentGeneration.setText("");
                bestFitness.setText("");
                progressBar.setValue(0);
                InputGeneration.setText("800");
                InputCrossover.setText("0.8");
                InputPopultion.setText("1000");
                InputMutation.setText("0.3");
                InputPoolSize.setText("250");
                InputSquareSize.setText("5");
                Time.setText("TIME");

            }
        });
        RUNDEFAULTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final SolverConfiguration sc = new SolverConfiguration.Builder()
                        .maxGenerations(800)
                        .populationSize(1000)
                        .parentPoolSize(250)
                        .crossoverProbability(0.8)
                        .mutationProbability(0.3)
                        .build();
                final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3, 1);
                s = new MagicSquareSolver(a, 5, sc);
                s.addPropertyChangeListener(propertyChangeListener);

                s.execute();
            }

        });
    }

    private void createUIComponents() {
        squarePanel = new SquarePanel();
        squarePanelRef = squarePanel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestGui");
        frame.setContentPane(new TestGui().MagicSquarePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

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
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private JTextField InputGeneration;
    private JLabel Time;
    private JTextField InputPopultion;
    private JProgressBar progressBar;
    private JTextField InputSquareSize;
    private JTextField InputCrossover;
    private JTextField InputMutation;
    private JButton RESETButton;
    private JPanel squarePanelRef;
    private JLabel currentGeneration;
    private JLabel bestFitness;
    private JTextField InputTournamentSize;
    private JTextField InputSymmetryMultiplier;
    private JTextField InputN;
    private JButton saveResultsButton;
    private JButton saveParametersButton;

    private SquarePanel squarePanel;

    private PropertyChangeListener propertyChangeListener;

    private MagicSquareSolver s;

    public TestGui() {
        final StringBuilder resultsStats = new StringBuilder();

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

                            resultsStats.append("Generation: " + r.getGeneration() + '\n');
                            resultsStats.append("Best fitness: " + r.getFitness() + '\n');
                            resultsStats.append("Best individual: " + r.getResult() + '\n');
                            resultsStats.append("Population:\n");
                            final List<Integer> fitnesses = new ArrayList(r.getPopulation().keySet());
                            Collections.sort(fitnesses);
                            for (final Integer fitness : fitnesses) {
                                resultsStats.append("Fitness: " + fitness + " Individuals:\n");
                                for (final MagicSquare ms : r.getPopulation().get(fitness)) {
                                    resultsStats.append(ms + "\n");
                                }
                            }
                            resultsStats.append("\n");
                        }
                    }
                } else if ("totalTime".equals(evt.getPropertyName())) {
                    RUNButton.setText("Run");
                    saveResultsButton.setEnabled(true);
                    Time.setText(evt.getNewValue().toString());
                    resultsStats.append("Total time: " + evt.getNewValue() + " seconds\n");
                }
            }
        };

        RUNButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Run".equals(RUNButton.getText())) {
                    progressBar.setValue(0);
                    saveResultsButton.setEnabled(false);
                    RUNButton.setText("Stop");
                    Time.setText("TIME");
                    resultsStats.delete(0, resultsStats.length());
                    final SolverConfiguration sc = new SolverConfiguration.Builder()
                            .maxGenerations(Long.parseLong(InputGeneration.getText()))
                            .populationSize(Long.parseLong(InputPopultion.getText()))
                            .N(Long.parseLong(InputN.getText()))
                            .crossoverProbability(Double.parseDouble(InputCrossover.getText()))
                            .mutationProbability(Double.parseDouble(InputMutation.getText()))
                            .build();
                    final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(
                            Integer.parseInt(InputTournamentSize.getText()),
                            Double.parseDouble(InputSymmetryMultiplier.getText()));
                    s = new MagicSquareSolver(a, Integer.parseInt(InputSquareSize.getText()), sc);
                    s.addPropertyChangeListener(propertyChangeListener);

                    s.execute();
                } else {
                    RUNButton.setText("Run");
                    saveResultsButton.setEnabled(true);
                    s.cancel(true);
                    s.removePropertyChangeListener(propertyChangeListener);
                }
            }
        });

        RESETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                squarePanel.updateMagicSquare(null);
                currentGeneration.setText("");
                bestFitness.setText("");
                progressBar.setValue(0);
                InputGeneration.setText("1000");
                InputCrossover.setText("1.0");
                InputPopultion.setText("2000");
                InputN.setText("100");
                InputMutation.setText("0.4");
                InputSquareSize.setText("4");
                InputTournamentSize.setText("50");
                InputSymmetryMultiplier.setText("1.0");
                Time.setText("TIME");

            }
        });

        final JFileChooser paramsFileChooser = new JFileChooser();
        saveParametersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final int returnVal = paramsFileChooser.showSaveDialog(MagicSquarePanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File file = paramsFileChooser.getSelectedFile();
                    Writer writer = null;

                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(file), "utf-8"));
                        writer.write(
                                "Square size: " + InputSquareSize.getText() + '\n');
                        writer.write(
                                "Max generations: " + InputGeneration.getText() + '\n');
                        writer.write(
                                "Population size: " + InputPopultion.getText() + '\n');
                        writer.write(
                                "N (current result period): " + InputN.getText() + '\n');
                        writer.write(
                                "Crossover probability: " + InputCrossover.getText() + '\n');
                        writer.write(
                                "Mutation probability: " + InputMutation.getText() + '\n');
                        writer.write(
                                "Tournament size: " + InputTournamentSize.getText() + '\n');
                        writer.write(
                                "Symmetry fitness multiplier: " + InputSymmetryMultiplier.getText() + '\n');
                    } catch (IOException ex) {
                        // report
                    } finally {
                        try {writer.close();} catch (Exception ex) {}
                    }
                }
            }
        });

        final JFileChooser resultsFileChooser = new JFileChooser();
        saveResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final int returnVal = paramsFileChooser.showSaveDialog(MagicSquarePanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File file = paramsFileChooser.getSelectedFile();
                    Writer writer = null;

                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(file), "utf-8"));
                        writer.write(resultsStats.toString());
                    } catch (IOException ex) {
                        // report
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception ex) {
                        }
                    }
                }
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

import ga.square.magic.GeneticAlgorithm;
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
    private JPanel magicSquarePanel;
    private JButton RUNButton;
    private JButton RESETButton;
    private JTextArea textArea1;
    private JTextField InputGeneration;
    private JLabel Time;
    private JButton RUNDEFAULTButton;
    private JTextField InputPopultion;
    private JTextField InputPoolSize;
    private JProgressBar progressBar;

    private PropertyChangeListener propertyChangeListener;

    private MagicSquareSolver s;


    public TestGui() {
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer)evt.getNewValue());
                } else if ("currentBestIndividual".equals(evt.getPropertyName())) {
                    textArea1.setText(evt.getNewValue().toString());
                }
            }
        };

        RUNButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final SolverConfiguration sc = new SolverConfiguration.Builder()
                        .maxGenerations(Integer.parseInt(InputGeneration.getText()))
                        .populationSize(1000)
                        .parentPoolSize(250)
                        .crossoverProbability(0.8)
                        .mutationProbability(0.4)
                        .build();
                final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3);
                s = new MagicSquareSolver(a, 6, sc, textArea1, Time);
                s.addPropertyChangeListener(propertyChangeListener);

                s.execute();
            }
        });


        RESETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                s.cancel(true);
                s.removePropertyChangeListener(propertyChangeListener);
            }
        });
        RUNDEFAULTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final SolverConfiguration sc = new SolverConfiguration.Builder()
                        .maxGenerations(1000)
                        .populationSize(1000)
                        .parentPoolSize(250)
                        .crossoverProbability(0.8)
                        .mutationProbability(0.4)
                        .build();
                final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3);
                s = new MagicSquareSolver(a, 4, sc, textArea1, Time);
                s.addPropertyChangeListener(propertyChangeListener);

                s.execute();
            }

        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestGui");
        frame.setContentPane(new TestGui().magicSquarePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}

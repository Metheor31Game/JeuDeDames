package jeuDeDames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Dames extends JFrame {
    private final int[][] board = new int[6][6]; // Représente le plateau : 0 = vide, 1 = joueur 1, 2 = joueur 2
    private final JButton[][] buttons = new JButton[6][6]; // Interface graphique pour le plateau
    private int mode; // 1 = PvP, 2 = IA simple, 3 = IA complexe
    private int currentPlayer = 1; // Joueur actif (1 ou 2)
    private Point selectedCell = null; // Case sélectionnée par le joueur

    public Dames(int mode) {
        this.mode = mode;
        setTitle("Jeu de Dames Simplifiées");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 6));

        initializeBoard();
        initializeGUI();

        setVisible(true);
    }

    private void initializeBoard() {
        // Initialise les positions des pions pour un jeu de dames simplifié
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = 1; // Pions du joueur 1
                }
            }
        }
        for (int row = 4; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = 2; // Pions du joueur 2
                }
            }
        }
    }

    private void initializeGUI() {
        // Crée les boutons pour chaque case du plateau
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.addActionListener(new CellClickListener(row, col));
                buttons[row][col] = button;
                add(button);
            }
        }

        // Appel initial pour mettre à jour les icônes et l'apparence des boutons
        updateBoard();
    }

    private void updateBoard() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                buttons[i][j].setText(""); // Supprime tout texte

                // Couleurs de fond alternées pour l'échiquier
                if ((i + j) % 2 == 0) {
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    buttons[i][j].setBackground(Color.DARK_GRAY);
                }

                // Ajout des disques pour les pions
                if (board[i][j] == 1) { // Pion du joueur 1 (rouge)
                    buttons[i][j].setIcon(createCircleIcon(Color.RED));
                } else if (board[i][j] == 2) { // Pion du joueur 2 (bleu)
                    buttons[i][j].setIcon(createCircleIcon(Color.BLUE));
                } else {
                    buttons[i][j].setIcon(null); // Pas de pion
                }
            }
        }
    }

    private Icon createCircleIcon(Color color) {
        int size = 50; // Taille du disque
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Dessiner un cercle plein
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);

        // Nettoyage
        g2.dispose();

        return new ImageIcon(image);
    }

    private void updateButtonIcon(int row, int col) {
        // Met à jour l'icône du bouton en fonction de l'état du plateau
        if (board[row][col] == 1) {
            buttons[row][col].setText("O"); // Pion du joueur 1
            buttons[row][col].setForeground(Color.BLUE);
        } else if (board[row][col] == 2) {
            buttons[row][col].setText("X"); // Pion du joueur 2
            buttons[row][col].setForeground(Color.RED);
        } else {
            buttons[row][col].setText("");
        }
    }

    private boolean isValidMove(Point start, Point end) {
        // Vérifie les limites du plateau
        if (end.x < 0 || end.x >= 6 || end.y < 0 || end.y >= 6) {
            return false;
        }
        // Vérifie si la case de destination est vide
        if (board[end.x][end.y] != 0) {
            return false;
        }

        int dx = end.x - start.x; // Différence en lignes
        int dy = Math.abs(end.y - start.y); // Différence en colonnes (valeur absolue)

        // Mouvement simple (sans capture)
        if (Math.abs(dx) == 1 && dy == 1 && board[start.x][start.y] == currentPlayer) {
            return dx == (currentPlayer == 1 ? 1 : -1);
        }

        // Mouvement avec capture
        if (Math.abs(dx) == 2 && dy == 2) {
            int capturedX = (start.x + end.x) / 2; // Case sautée en ligne
            int capturedY = (start.y + end.y) / 2; // Case sautée en colonne

            // Vérifie si le pion capturé appartient à l'adversaire
            if (board[capturedX][capturedY] != 0 && board[capturedX][capturedY] != currentPlayer) {
                return true;
            }
        }

        return false;
    }

    private void makeMove(Point start, Point end) {
        // Déplacement du pion
        board[end.x][end.y] = board[start.x][start.y];
        board[start.x][start.y] = 0;

        // Vérifie si un pion a été capturé
        if (Math.abs(start.x - end.x) == 2 && Math.abs(start.y - end.y) == 2) {
            int capturedX = (start.x + end.x) / 2;
            int capturedY = (start.y + end.y) / 2;

            // Retire le pion capturé
            board[capturedX][capturedY] = 0;
        }

        // Mets à jour l'affichage
        updateBoard();

        // Passe au joueur suivant
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        selectedCell = null;
    }

    private void handlePlayerMove(int row, int col) {
        Point clickedCell = new Point(row, col);

        if (selectedCell == null) {
            // Sélectionne une case
            if (board[row][col] == currentPlayer) {
                selectedCell = clickedCell;
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un de vos pions !", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Valide le coup
            if (isValidMove(selectedCell, clickedCell)) {
                makeMove(selectedCell, clickedCell);
            } else {
                JOptionPane.showMessageDialog(this, "Coup invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CellClickListener extends MouseAdapter implements ActionListener {
        private final int row;
        private final int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handlePlayerMove(row, col);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dames(1)); // Par défaut en mode PvP
    }
}

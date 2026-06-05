package com.yut.ui.swing;

import com.yut.controller.view_interfaces.GameScreenInterface;
import com.yut.model.YutResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class GameScreen extends JPanel implements GameScreenInterface {
    private List<Piece> pieceList = new ArrayList<>();

    private final Map<Integer, Integer> piecesEntered = new HashMap<>();
    private final Map<Integer, PlayerCanvas> playerCanvases = new HashMap<>();
    private final MainFrame frame;
    private final int boardType;
    private final int playerCount;

    private static final int DEFAULT_PIECE_COUNT = 0;

    Color[] playerColors = {
            new Color(240, 128, 128),
            new Color(176, 224, 230),
            new Color(198, 233, 105),
            new Color(221, 160, 221)
    };

    private List<ClickableNode> clickableNodes;
    private Map<Integer, ClickableNode> nodeMap = new HashMap<>();

    private BoardCanvas boardCanvas;
    private Integer selectedPieceId = null;
    private boolean awaitingMove = false;
    private ClickableNode currentHintNode = null;

    private PreviewCircle previewCircle = null;
    private SelectRectangle selectRectangle = null;

    private final ControlPanel controlPanel;
    private JButton backButton;
    private JTextArea deckDisplayArea;

    private JLayeredPane layeredBoard = new JLayeredPane();

    public GameScreen(MainFrame frame, int boardType, int playerCount) {
        this.frame = frame;
        this.boardType = boardType;
        this.playerCount = playerCount;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        backButton = new JButton("← 시작 화면으로");
        backButton.setPreferredSize(new Dimension(150, 40));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        backButton.addActionListener(e -> frame.showStart());

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        layeredBoard.setLayout(null);
        layeredBoard.setPreferredSize(new Dimension(400, 400));

        BoardCanvas boardCanvas = new BoardCanvas(boardType);
        boardCanvas.setBounds(0, 0, 400, 400);
        boardCanvas.setEnabled(false);
        boardCanvas.setInteractive(true);

        layeredBoard.add(boardCanvas, JLayeredPane.DEFAULT_LAYER);
        boardCanvas.setGameScreen(this);
        this.boardCanvas = boardCanvas;

        layeredBoard.repaint();
        Toolkit.getDefaultToolkit().sync();

        SwingUtilities.invokeLater(() -> {
            clickableNodes = boardCanvas.getClickableNodes();
            for (ClickableNode node : clickableNodes) {
                nodeMap.put(node.nodeID, node);

                int radius = 18;
                int x = node.getNodeX() - radius;
                int y = node.getNodeY() - radius;
                node.setBounds(x, y, radius * 2, radius * 2);
                layeredBoard.add(node, JLayeredPane.POPUP_LAYER);
            }
        });

        clickableNodes = boardCanvas.getClickableNodes();
        for (ClickableNode node : clickableNodes) {
            nodeMap.put(node.nodeID, node);
        }

        JPanel boardPanel = new JPanel();
        boardPanel.add(layeredBoard);
        controlPanel = new ControlPanel();

        centerPanel.add(boardPanel);
        centerPanel.add(controlPanel);
        add(centerPanel, BorderLayout.CENTER);

        initBottomPanel();
    }

    private void initBottomPanel() {
        JPanel bottomPanelWrapper = new JPanel();
        bottomPanelWrapper.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("남은 말 개수 표시", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottomPanelWrapper.add(statusLabel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (int i = 1; i <= playerCount; i++) {
            piecesEntered.put(i, 0);
            Color color = playerColors[(i - 1) % playerColors.length];
            PlayerCanvas player = new PlayerCanvas(i, DEFAULT_PIECE_COUNT, color);
            playerCanvases.put(i, player);
            bottomPanel.add(player);
        }
        bottomPanelWrapper.add(bottomPanel, BorderLayout.CENTER);

        deckDisplayArea = new JTextArea(5, 20);
        deckDisplayArea.setEditable(false);
        deckDisplayArea.setLineWrap(true);
        deckDisplayArea.setWrapStyleWord(true);
        deckDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        deckDisplayArea.setBorder(BorderFactory.createTitledBorder("덱 상태"));
        bottomPanelWrapper.add(deckDisplayArea, BorderLayout.SOUTH);
        add(bottomPanelWrapper, BorderLayout.SOUTH);
    }

    @Override
    public void printDeckContents(Deque<YutResult> deck) {
        StringBuilder sb = new StringBuilder();
        if (deck.isEmpty()) {
            sb.append("(덱이 비어 있습니다)");
        } else {
            for (YutResult result : deck) {
                sb.append(result.korName).append(" ");
            }
        }
        deckDisplayArea.setText(sb.toString());
    }

    public void movePiece(int nodeID) {
        ClickableNode node = boardCanvas.getNodeById(nodeID);
        int cx = node.x - pieceList.get(selectedPieceId).getWidth() / 2;
        int cy = node.y - pieceList.get(selectedPieceId).getHeight() / 2;
        pieceList.get(selectedPieceId).setLocation(cx, cy);
        pieceList.get(selectedPieceId).setCoords(cx, cy);
    }

    public class SelectRectangle extends JComponent {
        public static final int radius = 18;
        private int x;
        private int y;

        SelectRectangle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(0, 0, radius * 2, radius * 2);
        }
    }

    public class PreviewCircle extends JComponent {
        public static final int radius = 18;
        private int x;
        private int y;
        private Color color;

        PreviewCircle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(5));
            g2.drawOval(0, 0, radius * 2, radius * 2);
        }
    }

    @Override
    public void showMovePreview(int nodeID, int playerID) {
        ClickableNode node = nodeMap.get(nodeID);
        int x = node.getNodeX() - PreviewCircle.radius;
        int y = node.getNodeY() - PreviewCircle.radius;
        previewCircle = new PreviewCircle(x, y, playerColors[playerID - 1]);
        previewCircle.setBounds(x, y, PreviewCircle.radius * 2, PreviewCircle.radius * 2);
        node.setPreviewPiece(true);
        currentHintNode = node;
        layeredBoard.add(previewCircle);
    }

    @Override
    public void deleteMovePreview() {
        if (previewCircle != null) {
            if (currentHintNode != null) {
                currentHintNode.setPreviewPiece(false);
                currentHintNode = null;
            }
            layeredBoard.remove(previewCircle);
            previewCircle = null;
            layeredBoard.revalidate();
            layeredBoard.repaint();
        }
    }

    @Override
    public void drawPiece(int nodeID, int playerID, int pieceNumber) {
        ClickableNode node = nodeMap.get(nodeID);
        if (node == null)
            throw new RuntimeException("Node does not exist");

        Piece piece = new Piece(node.getNodeX() - Piece.radius, node.getNodeY() - Piece.radius,
                playerColors[playerID - 1], pieceNumber);
        piece.setBounds(node.getNodeX() - Piece.radius, node.getNodeY() - Piece.radius,
                Piece.radius * 2, Piece.radius * 2);
        layeredBoard.add(piece, JLayeredPane.PALETTE_LAYER);
        node.setOnNodePiece(piece);
        layeredBoard.revalidate();
        layeredBoard.repaint();
    }

    @Override
    public void deletePiece(int nodeID) {
        if (nodeMap.get(nodeID) == null)
            throw new RuntimeException("There are no pieces to delete on node" + nodeID);
        ClickableNode node = nodeMap.get(nodeID);
        Piece piece = node.getOnNodePiece();

        if (piece == null)
            return;
        node.setOnNodePiece(null);
        layeredBoard.remove(piece);
        layeredBoard.revalidate();
        layeredBoard.repaint();
    }

    // 1 = 미리보기 힌트 있는 노드, 2 = 빈 노드, 3 = 말이 있는 노드
    @Override
    public int getNodeState(int nodeID) {
        return nodeMap.get(nodeID).getState();
    }

    @Override
    public void updatePlayerCanvas(int playerID, int pieceCount) {
        PlayerCanvas playercanvas = playerCanvases.get(playerID);
        playercanvas.setPieceCount(pieceCount);
        playercanvas.repaint();
    }

    @Override
    public void select(int nodeID) {
        if (selectRectangle != null) {
            layeredBoard.remove(selectRectangle);
            layeredBoard.revalidate();
            layeredBoard.repaint();
        }
        ClickableNode node = nodeMap.get(nodeID);
        int x = node.getNodeX() - SelectRectangle.radius;
        int y = node.getNodeY() - SelectRectangle.radius;
        selectRectangle = new SelectRectangle(x, y);
        selectRectangle.setBounds(x, y, SelectRectangle.radius * 2, SelectRectangle.radius * 2);
        node.setPreviewPiece(true);
        layeredBoard.add(selectRectangle);
    }

    @Override
    public void highlightCurrentPlayer(int currentPlayerId) {
        for (int i = 1; i <= playerCount; i++) {
            PlayerCanvas canvas = playerCanvases.get(i);
            if (canvas != null)
                canvas.setHighlighted(i == currentPlayerId);
        }
    }

    @Override
    public void updateRandomResult(YutResult result) {
        controlPanel.highlightYutButton(result.code);
        if (result.grantsExtraTurn()) {
            controlPanel.getMoveNewPieceButton().setBackground(Color.LIGHT_GRAY);
            controlPanel.getMoveNewPieceButton().setEnabled(false);
        } else {
            controlPanel.getMoveNewPieceButton().setBackground(null);
            controlPanel.getMoveNewPieceButton().setEnabled(true);
        }
    }

    @Override
    public void addRandomThrowButtonListener(ActionListener listener) {
        controlPanel.getRandomButton().addActionListener(listener);
    }

    @Override
    public void addSelectedThrowButtonListener(int index, ActionListener listener) {
        controlPanel.getYutButtons()[index].addActionListener(listener);
    }

    @Override
    public void addMoveNewPieceButtonListener(ActionListener listener) {
        controlPanel.getMoveNewPieceButton().addActionListener(listener);
    }

    @Override
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    @Override
    public void addNodeClickListener(ClickableNode node, MouseListener listener) {
        node.addMouseListener(listener);
    }

    @Override
    public void addGoalButtonListener(ActionListener listener) {
        controlPanel.getGoalButton().addActionListener(listener);
    }

    @Override
    public void setGoalButtonVisible(boolean visible) {
        controlPanel.getGoalButton().setVisible(visible);
    }

    @Override
    public Map<Integer, ClickableNode> getNodeMap() {
        return nodeMap;
    }
}

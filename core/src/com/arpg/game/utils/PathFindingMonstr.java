package com.arpg.game.utils;

import com.arpg.game.GameScreen;
import com.arpg.game.Map;
import com.arpg.game.Monster;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PathFindingMonstr {

    private static class CellNode implements Comparable<CellNode> {
        private int x, y;
        private CellNode from;
        private int cost, priority;
        private boolean passable;
        private boolean processed;
        private List<CellNode> neighbors;

        public CellNode(int x, int y) {
            this.x = x;
            this.y = y;
            this.neighbors = new ArrayList<>();
            this.passable = true;
        }

        @Override
        public int compareTo(CellNode o) {
            return this.priority - o.priority;
        }

        @Override
        public String toString() {
            return "C{" + cost + '}';
        }
    }


//    private GameScreen gs;
    private static CellNode[][] nodes;



//    private int srcX = 1, srcY = 1;
//    private int dstX = 8, dstY = 8;


//    public PathFindingMonstr(GameScreen gs) {
//        this.gs = gs;
//    }

    public static void buildRoute(GameScreen gs, Monster m) {
        nodes = new CellNode[Map.MAP_SIZE_Y][Map.MAP_SIZE_Y];

        for (int i = 0; i < Map.MAP_SIZE_X; i++) {
            for (int j = 0; j < Map.MAP_SIZE_Y; j++) {
                nodes[i][j].neighbors.clear();
                nodes[i][j].processed = false;
                nodes[i][j].from = null;
                nodes[i][j].cost = 0;
                nodes[i][j].priority = 0;

                if (i > 0 &&  gs.getMap().isCellPassable(new Vector2(i-1,j))) {
                    nodes[i][j].neighbors.add(nodes[i - 1][j]);
                }
                if (i < Map.MAP_SIZE_X - 1 && gs.getMap().isCellPassable(new Vector2(i+1, j))) {
                    nodes[i][j].neighbors.add(nodes[i + 1][j]);
                }
                if (j > 0 && gs.getMap().isCellPassable(new Vector2(i, j-1))) {
                    nodes[i][j].neighbors.add(nodes[i][j - 1]);
                }
                if (j < Map.MAP_SIZE_Y - 1 && gs.getMap().isCellPassable(new Vector2(i, j+1))) {
                    nodes[i][j].neighbors.add(nodes[i][j + 1]);
                }
            }
        }

        int srcX = m.getCellX();
        int srcY = m.getCellY();

        int dstX = gs.getHero().getCellX();
        int dstY = gs.getHero().getCellY();

        nodes[srcX][srcY].from = null;
        nodes[srcX][srcY].cost = 0;

        PriorityQueue<CellNode> frontier = new PriorityQueue<>(1000);
        frontier.add(nodes[srcX][srcY]);

        while (!frontier.isEmpty()) {
            CellNode current = frontier.poll();
            current.processed = true;

            if (current.x == dstX && current.y == dstY) {
                break;
            }

            for (int i = 0; i < current.neighbors.size(); i++) {
                CellNode next = current.neighbors.get(i);
                int newCost = current.cost + gs.getMap().isCellPassable(next.x, next.y);
                if (!next.processed && (next.from == null || newCost < next.cost)) {
                    next.cost = newCost;
                    next.priority = newCost + (Math.abs(next.x - dstX) + Math.abs(next.y - dstY)) * 1;
                    next.from = current;
                    if (!frontier.contains(next)) {
                        frontier.add(next);
                    }
                }
            }
        }
    }


}

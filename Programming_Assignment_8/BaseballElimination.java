package Programming_Assignment_8;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class BaseballElimination {

    private int flows;
    private final int[] win;
    private final int[] lose;
    private final int[] left;
    private final int[][] remain;
    private String[] teams;
    private final int teamnum;
    private HashMap<Integer,Integer>pos;
    private final HashMap<String, Integer> map;
    private HashSet<String> set;

    private int allv;
    private int gameTeams;

    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException("Wrong file name...");
        In in = new In(filename);
        teamnum = Integer.parseInt(in.readLine());
        teams = new String[teamnum];
        map = new HashMap<String, Integer>();
        win = new int[teamnum];
        lose = new int [teamnum];
        left = new int[teamnum];
        remain = new int[teamnum][teamnum];

        int tot = 0;
        while (in.hasNextLine()) {
            String readline = in.readLine().trim();
            String []token = readline.split(" +");
            map.put(token[0], tot);

            teams[tot] = token[0];
            win[tot] = Integer.parseInt(token[1]);
            lose[tot] = Integer.parseInt(token[2]);
            left[tot] = Integer.parseInt(token[3]);


            for (int i = 0; i < teamnum; i++) {
                if (i == tot) remain[tot][i] = 0;
                else remain[tot][i] = Integer.parseInt(token[4 + i]);
            }
            tot++;
        }
    }

    public int numberOfTeams() {
        return teamnum;
    }

    public Iterable<String> teams() {
        return map.keySet();
    }

    public int wins(String team) {
        valid(team);
        Integer id = map.get(team);
        return win[id];
    }
    public int losses(String team) {
        valid(team);
        Integer id = map.get(team);
        return lose[id];
    }

    public int remaining(String team) {
        valid(team);
        Integer id = map.get(team);
        return left[id];
    }

    public int against(String team1, String team2) {
        valid(team1);
        valid(team2);
        Integer id1 = map.get(team1);
        Integer id2 = map.get(team2);
        return remain[id1][id2];
    }

    private FlowNetwork buildFlowNetwrok(String team) {
        valid(team);
        Integer id = map.get(team);

        int most = win[id] + left[id];

        gameTeams = (teamnum - 1) * (teamnum - 2) / 2;
        allv = gameTeams + teamnum - 1 + 2;
        flows = 0;


        pos = new HashMap<>();
        int s = 0, t = allv - 1;
        int gameIndex = 1; //比赛结点
        int indexi = gameTeams;// 球队节点
        int indexj = indexi;
        double max = Double.POSITIVE_INFINITY;

        FlowNetwork flowNetwork = new FlowNetwork(allv);
        for (int i = 0; i < teamnum; i++) {
            if (id == i) continue;
            indexi++;
            indexj = indexi;
            if(win[i] > most) return null;

            for (int j = i + 1; j < teamnum; j++) {
                if (j == id) continue;
                indexj++;
                flows += remain[i][j];
                flowNetwork.addEdge(new FlowEdge(s,gameIndex,remain[i][j]));
                flowNetwork.addEdge(new FlowEdge(gameIndex,indexi,max));
                flowNetwork.addEdge(new FlowEdge(gameIndex,indexj,max));
                gameIndex++;
            }
            pos.put(indexi,i);

            flowNetwork.addEdge(new FlowEdge(indexi,t,most - win[i]));
        }

        return flowNetwork;
    }

    private void valid(String team) {
        if (team == null) throw new IllegalArgumentException("Wrong teams");
        if (!map.containsKey(team)) throw new IllegalArgumentException("Not in the team");
    }

    public boolean isEliminated(String team) {
       valid(team);
       FlowNetwork flowNetwork = buildFlowNetwrok(team);
       int id = map.get(team);

       if (flowNetwork == null) {
           set = new HashSet<>();
           for (int i = 0; i < teamnum; i++) {
                if (id == i) continue;
                if (win[id] + left[id] < win[i]) {
                    set.add(teams[i]);
                }
           }
           return true;
       }

       FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork,0,allv - 1);

       if (flows > fordFulkerson.value()) {
           set = new HashSet<>();
           for (int i = gameTeams + 1; i < allv - 1 ;i++) {
               if (fordFulkerson.inCut(i)) {
                   int Id = pos.get(i);
                   set.add(teams[Id]);
               }
           }

           return true;
       }
        return false;

    }

    public Iterable<String> certificateOfElimination(String team)  {
        valid(team);
        if (isEliminated(team)) return set;
        return null;
    }


}



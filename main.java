import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a file name");
            return;
        }
        File file = new File(args[0]);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String firstLine;
            firstLine = reader.readLine();
            if (firstLine == null) {
                System.out.println("File is empty");
                reader.close();
                return;
            }
            String[] firstLineArray = firstLine.split(" ");
            if (firstLineArray[0].equals("RR")) {
                int quantum = Integer.parseInt(firstLineArray[1]);
                String secondLine = reader.readLine();
                int numOfProcesses = Integer.parseInt(secondLine);
                // int[][] processes = new int[numOfProcesses][];
                ArrayList<int[]> processes = new ArrayList<int[]>();
                for (int i = 0; i < numOfProcesses; i++) {
                    // processes[i] = Integer.parseInt(reader.readLine());
                    String line = reader.readLine();
                    String[] lineArray = line.split(" ");
                    int[] p = { Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[2]) };
                    // processes[i] = p;
                    processes.add(p);
                }
                round_robin(quantum, numOfProcesses, processes);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void round_robin(int quantum, int numOfProcesses, ArrayList<int[]> processes) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("RR" + " " + quantum + "\n");
            int time = 0;
            while (!processes.isEmpty()) {
                if (!processes.isEmpty()) {
                    writer.write(time + " " + processes.get(0)[0] + "\n");
                }
                if (processes.get(0)[1] > quantum) {
                    time += quantum;
                    int[] p = { processes.get(0)[0], processes.get(0)[1] - quantum };
                    processes.add(p);
                    processes.remove(0);

                } else {
                    time += processes.get(0)[1];
                    processes.remove(0);
                }
            }
            writer.close();

            BufferedReader reader = new BufferedReader(new FileReader("output.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

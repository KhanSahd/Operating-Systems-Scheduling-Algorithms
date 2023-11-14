import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

class Process {
    int id;
    int burstTime;
    int arrivalTime;
    int priority;

    public Process(int id, int burstTime, int arrivalTime) {
        this.id = id + 1;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
    }

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id + 1;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
    }
}

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
            } else if (firstLineArray[0].equals("SJF")) {
                int numOfProcesses = Integer.parseInt(reader.readLine());
                ArrayList<Process> processes = new ArrayList<>();

                for (int i = 0; i < numOfProcesses; i++) {
                    String line = reader.readLine();
                    String[] lineArray = line.split(" ");
                    Process p = new Process(i, Integer.parseInt(lineArray[2]), Integer.parseInt(lineArray[1]));
                    processes.add(p);
                }

                sjf(numOfProcesses, processes);
            } else if (firstLineArray[0].equals("PR_noPREMP")) {
                int numOfProcesses = Integer.parseInt(reader.readLine());
                ArrayList<Process> processes = new ArrayList<>();
                for (int i = 0; i < numOfProcesses; i++) {
                    String line = reader.readLine();
                    String[] lineArray = line.split(" ");
                    Process p = new Process(i, Integer.parseInt(lineArray[1]),
                            Integer.parseInt(lineArray[2]), Integer.parseInt(lineArray[3]));
                    processes.add(p);
                }
                prNoPreemption(numOfProcesses, processes);
            } else if (firstLineArray[0].equals("PR_withPREMP")) {
                int numOfProcesses = Integer.parseInt(reader.readLine());
                ArrayList<Process> processes = new ArrayList<>();
                for (int i = 0; i < numOfProcesses; i++) {
                    String line = reader.readLine();
                    String[] lineArray = line.split(" ");
                    Process p = new Process(i, Integer.parseInt(lineArray[1]),
                            Integer.parseInt(lineArray[2]), Integer.parseInt(lineArray[3]));
                    processes.add(p);
                }
                prWithPreemption(numOfProcesses, processes);
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

    static void sjf(int numOfProcesses, ArrayList<Process> processes) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("SJF\n");
            PriorityQueue<Process> priorityQueue = new PriorityQueue<>((p1, p2) -> {
                if (p1.burstTime == p2.burstTime) {
                    if (p1.arrivalTime == p2.arrivalTime) {
                        return Integer.compare(p1.id, p2.id);
                    }
                    return Integer.compare(p1.arrivalTime, p2.arrivalTime);
                }
                return Integer.compare(p1.burstTime, p2.burstTime);
            });

            int time = 0;
            int processIndex = 0;
            int totalWaitingTime = 0;

            while (processIndex < numOfProcesses || !priorityQueue.isEmpty()) {
                while (processIndex < numOfProcesses && processes.get(processIndex).arrivalTime <= time) {
                    priorityQueue.add(processes.get(processIndex));
                    processIndex++;
                }

                if (!priorityQueue.isEmpty()) {
                    Process currentProcess = priorityQueue.poll();
                    writer.write(time + "\t" + currentProcess.id + "\n");

                    int waitingTime = time - currentProcess.arrivalTime;
                    totalWaitingTime += waitingTime;

                    time += currentProcess.burstTime;
                } else {
                    if (processIndex < numOfProcesses) {
                        time = processes.get(processIndex).arrivalTime;
                    }
                }
            }

            double avgWaitingTime = (double) totalWaitingTime / numOfProcesses;
            writer.write("AVG Waiting Time: " + String.format("%.2f", avgWaitingTime) + "\n");

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

    static void prNoPreemption(int numOfProcesses, ArrayList<Process> processes) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("PR_noPREMP\n");
            PriorityQueue<Process> priorityQueue = new PriorityQueue<>((p1, p2) -> {
                if (p1.priority == p2.priority) {
                    if (p1.id == p2.id) {
                        return Integer.compare(p1.id, p2.id);
                    }
                    return Integer.compare(p1.id, p2.id);
                }
                return Integer.compare(p1.priority, p2.priority);
            });

            int time = 0;
            int processIndex = 0;
            int totalWaitingTime = 0;

            while (processIndex < numOfProcesses || !priorityQueue.isEmpty()) {
                while (processIndex < numOfProcesses && processes.get(processIndex).arrivalTime <= time) {
                    priorityQueue.add(processes.get(processIndex));
                    processIndex++;
                }

                if (!priorityQueue.isEmpty()) {
                    Process currentProcess = priorityQueue.poll();
                    writer.write(time + "\t" + currentProcess.id + "\n");

                    int waitingTime = time - currentProcess.arrivalTime;
                    totalWaitingTime += waitingTime;

                    time += currentProcess.burstTime;
                } else {
                    if (processIndex < numOfProcesses) {
                        time = processes.get(processIndex).arrivalTime;
                    }
                }
            }

            double avgWaitingTime = (double) totalWaitingTime / numOfProcesses;
            writer.write("AVG Waiting Time: " + String.format("%.2f", avgWaitingTime) + "\n");

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

    static void prWithPreemption(int numOfProcesses, ArrayList<Process> processes) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("PR_withPREMP\n");

            PriorityQueue<Process> priorityQueue = new PriorityQueue<>((p1, p2) -> {
                if (p1.priority == p2.priority) {
                    if (p1.arrivalTime == p2.arrivalTime) {
                        return Integer.compare(p1.id, p2.id);
                    }
                    return Integer.compare(p1.arrivalTime, p2.arrivalTime);
                }
                return Integer.compare(p1.priority, p2.priority);
            });

            int time = 0;
            int totalWaitingTime = 0;

            while (!priorityQueue.isEmpty() || !processes.isEmpty()) {
                while (!processes.isEmpty() && processes.get(0).arrivalTime <= time) {
                    priorityQueue.add(processes.remove(0));
                }

                if (!priorityQueue.isEmpty()) {
                    Process currentProcess = priorityQueue.poll();
                    writer.write(time + "\t" + currentProcess.id + "\n");

                    int waitingTime = time - currentProcess.arrivalTime;
                    totalWaitingTime += waitingTime;

                    if (currentProcess.burstTime > 1) {
                        currentProcess.burstTime--;
                        priorityQueue.add(currentProcess);
                    }
                } else {
                    time = processes.get(0).arrivalTime;
                }

                time++;
            }

            double avgWaitingTime = (double) totalWaitingTime / numOfProcesses;
            writer.write("AVG Waiting Time: " + String.format("%.2f", avgWaitingTime) + "\n");

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

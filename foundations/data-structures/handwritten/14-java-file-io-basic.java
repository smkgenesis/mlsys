import java.io.*;

class Hello {
    public static void main(String[] args) {
        String s;
        int i, x;

        try {
            BufferedReader rd =
                    new BufferedReader(new FileReader("input.txt"));
            BufferedWriter wr =
                    new BufferedWriter(new FileWriter("output.txt"));

            for (i = 0; i < 3; i++) {
                s = rd.readLine();
                x = Integer.parseInt(s);
                s = Integer.toString(x + 1);
                wr.write(s);
                wr.newLine();
            }

            rd.close();
            wr.close();
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }
}

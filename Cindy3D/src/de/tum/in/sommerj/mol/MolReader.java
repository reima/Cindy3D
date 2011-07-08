package de.tum.in.sommerj.mol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class MolReader {
	
	
//	private static String rL(BufferedReader br) {
//		String s = "";
//		while (s.equals(""))
//			try {
//				s = br.readLine();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		
//		return s;
//	}
	
	public static void main(String[] args) {
		
		String filename = "Z:\\benzene.mol";
		String output = "Z:\\Water.cdy";
		
		System.out.println(filename);
		System.out.println(output);
		
		BufferedWriter bw = null;		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			bw = new BufferedWriter(new FileWriter(output));
			
			bw.write("begin3d();\n");
			bw.write("pointsize3d(5);\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		try {
			br.readLine();
			br.readLine();
			br.readLine();
			String t[] = br.readLine().trim().split(" ++");
			
			int atoms = Integer.parseInt(t[0]);
			int conns = Integer.parseInt(t[1]);
			
			LinkedList<String[]> l = new LinkedList<String[]>();
			for (int i=0; i<atoms; ++i) {
				t = br.readLine().trim().split(" ++");
				
				l.add(new String[] {t[0], t[1], t[2]});
				
				if (t[3].equals("H")) {
					bw.write("pointsize3d(10);\n");
					bw.write("pointcolor3d((1.0, 1.0, 1.0));\n");
				} else if (t[3].equals("O")) {
					bw.write("pointsize3d(15);\n");
					bw.write("pointcolor3d((1.0, 0.0, 0.0));\n");
				} else if (t[3].equals("C")) {
					bw.write("pointsize3d(15);\n");
					bw.write("pointcolor3d((0.2, 0.2, 0.2));\n");
				} else {
					bw.write("pointsize3d(0.5);\n");
					bw.write("pointcolor3d((0.0, 0.0, 1.0));\n");
				}
	
				bw.write("draw3d((" +
						l.get(i)[0] + ", " +
						l.get(i)[1] + ", " + 
						l.get(i)[2] + "));\n");
			}
			
			for (int i=0; i<conns; ++i) {
				t = br.readLine().trim().split(" ++");				
				bw.write("draw3d((" + 
						l.get(Integer.parseInt(t[0])-1)[0] + ", " +
						l.get(Integer.parseInt(t[0])-1)[1] + ", " +
						l.get(Integer.parseInt(t[0])-1)[2] + "), ("+
						l.get(Integer.parseInt(t[1])-1)[0] + ", " +
						l.get(Integer.parseInt(t[1])-1)[1] + ", " +
						l.get(Integer.parseInt(t[1])-1)[2] + "));\n");
				
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			br.close();
			bw.write("end3d()");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Conversion complete!");		
	}
}

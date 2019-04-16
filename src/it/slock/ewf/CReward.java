package it.slock.ewf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class CReward extends JFrame {

	double t1 = 0.1; // constant phase end in percent
	double t2 = 0.5; // asymptodic start in percent
	double t3 = 1; // end (must be 1!)

	long constduration = 1;// duration of const periods in month
	long blocktime = 5; // block time in seconds
	long rewardduration = 10; // reward duration in years
	long interval = 60 / blocktime * 60 * 24 * 365 * constduration / 12;
	long blocks = 60 / blocktime * 60 * 24 * 365 * rewardduration; // 'blocktime'
																	// seconds
																	// per block
																	// | 60min *
																	// 24h *
																	// 365d *
																	// 'rewardduration'years
	long block1 = (long) (blocks * t1); // block number for constant phase end
	long block2 = (long) (blocks * t2); // block number to start asymptomical
										// phase

	long quotient = 0; // integral quotient to calibrate to defined reward
						// tokens, is calculated by "CalculateReward"
	long rewardtokens = 10000000; // number of total reward tokens (in ether!)
	long maxreward = (long) (1e18 * rewardtokens / quotient); // maximum reward
																// in Wei, paid
																// only in the
																// constant
																// phase, later
																// it becomes
																// less. Is
																// calculated by
																// "CalculateReward"

	/** Calculate the calibration quotient. */
	public void CalculateReward() {
		System.out.println();
		System.out.println("###################################################");

		// reset values
		quotient = 0;
		maxreward = (long) 1e18;

		// calculate discrete integral
		double reward = 0;
		for (long block = 0; block < blocks; block++) {
			final long r = F(block);
			reward += r;
		}

		// recalculate values
		quotient = (long) (reward / 1e18);
		maxreward = (long) (1e18 * rewardtokens / quotient);

		System.out.println("quotient  = " + quotient + " THIS NEEDS TO BE USED IN CODE!");
		System.out.println("###################################################");
		System.out.println();
		System.out.println("maxreward = " + maxreward / 1e18);

		// check validity
		reward = 0;
		for (long block = 0; block < blocks; block++) {
			final long r = F(block);
			reward += r;
		}
		System.out.println("reward    = " + (long) (reward / 1e18) + " after " + rewardduration + " years");
	}

	/** Calculate the reward using the table data. */
	public void CalculateTotalRewardFromTableData(ArrayList<Long> al) {
		System.out.println();
		System.out.println("###################################################");

		// reset values
		maxreward = (long) 1e18;

		// check validity
		double reward = 0;// just use double to prevent variable from overflow
							// (long doesn't work)
		for (long block = 0; block < blocks; block++) {
			final long r = _F(block, al);
			reward += r;
		}
		System.out.println("real reward = " + (long) (reward / 1e18) + " after " + rewardduration + " years");
	}
	/*
	 * long alpha; long beta; long gamma;
	 */

	/**
	 * initialize the constants. NOT used any more, included in functions add: 2
	 * sub: 3 mul: 2 div: 0
	 */
	/*
	 * public void _init() { //calculate alpha constant alpha = block2 - blocks;
	 * //calculate beta constant beta = block2 - block1; //calculate gamma
	 * constant gamma = blocks * (blocks - (block1 + block2)) + block1 * block2;
	 * 
	 * System.out.println("blocks=" + blocks); System.out.println("alpha=" +
	 * alpha); System.out.println("beta=" + beta); System.out.println("gamma=" +
	 * gamma); System.out.println(); }
	 */

	/** double function f1. */
	public double f1(double x) {
		final double y = 1;
		return y;
	}

	/** double function f2. */
	public double f2(double x) {
		final double y = 1 + (x - t1) * (x - t1) * (t2 - t3) / (t2 - t1) / (t3 * t3 - t1 - t2 + t1 * t2);
		return y;
	}

	/** double function f3. */
	public double f3(double x) {
		final double y = (x - t3) * (x - t3) / (t3 * t3 - t1 - t2 + t1 * t2);
		return y;
	}

	/** double function. */
	public double f(double x) {
		if (x < t1) {
			return f1(x);
		} else if (x < t2) {
			return f2(x);
		} else {
			return f3(x);
		}
	}

	/** int to double function f1. */
	public double f1_(long currentblock) {
		return 1;
	}

	/** int to double function f2. */
	public double f2_(long currentblock) {
		// constants - can be outsourced into a init function
		final long a = block2 - blocks;
		final long b = block2 - block1;
		final long c = blocks * (blocks - (block1 + block2)) + block1 * block2;

		final double y = (double) (currentblock - block1) * (currentblock - block1) * a / b / c + 1;
		return y;
	}

	/** int to double function f3. */
	public double f3_(long currentblock) {
		final long c = blocks * (blocks - (block1 + block2)) + block1 * block2;
		final double y = (double) (currentblock - blocks) * (currentblock - blocks) / c;
		return y;
	}

	/** int to double function. */
	public double f_(long currentblock) {
		if (currentblock < block1) {
			return f1_(currentblock);
		} else if (currentblock < block2) {
			return f2_(currentblock);
		} else {
			return f3_(currentblock);
		}
	}

	/*****************************************************************************/

	/** int function f1. */
	public long F1(long currentblock) {
		return maxreward;
	}

	/**
	 * int function f2. add: 3 (1) sub: 4 (1) mul: 5 (3) div: 2 (2)
	 */
	public long F2(long currentblock) {
		// constants - can be outsourced into a init function
		final long a = block2 - blocks;
		final long b = block2 - block1;
		final long c = blocks * (blocks - (block1 + block2)) + block1 * block2;
		// help variable
		final long d = block1 - currentblock;
		// calculation
		// final long y = (long) ((double) maxreward * d * d * a / c / b +
		// maxreward);
		// final long y = maxreward * a * d * d / b / c + maxreward; //if
		// numerical possible
		final long y = maxreward / c * a * d / b * d + maxreward;

		return y;
	}

	/**
	 * int function f3. add: 2 (0) sub: 2 (1) mul: 4 (2) div: 1 (1)
	 */
	public long F3(long currentblock) {
		// constants - can be outsourced into a init function
		final long c = blocks * (blocks - (block1 + block2)) + block1 * block2;
		// help variable
		final long d = blocks - currentblock;
		// calculation
		// final long y = (long) ((double) maxreward * d * d / c);
		// final long y = maxreward * d * d / c; //if numerical possible
		final long y = maxreward / c * d * d;

		return y;
	}

	/** discrete function. */
	public long F(long currentblock) {
		final long block = currentblock - currentblock % interval;
		if (block < block1) {
			return F1(block);
		} else if (block < block2) {
			return F2(block);
		} else {
			return F3(block);
		}
	}

	/** discrete function using array. */
	public long _F(long currentblock, ArrayList<Long> al) {
		long l = 0;
		if (al != null) {
			final int index = (int) (currentblock / interval);
			l = al.get(index);
		}
		return l;
	}

	// array to store discrete values
	ArrayList<Long> array;

	/** Construct simple drawing app. */
	public CReward() {
		final JPanel p = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				// draw background
				g.setColor(new Color(0, 0, 0));
				g.fillRect(0, 0, getWidth(), getHeight());

				g.setColor(new Color(255, 255, 255));
				((Graphics2D) g).setStroke(new BasicStroke(2));
				g.drawRect(20, 20, getWidth() - 40, getHeight() - 40);
				((Graphics2D) g).setStroke(new BasicStroke(1, 0, 0, 1f, new float[] { 2f, 4f }, 1));

				for (int i = 0; i < 10; i++) {
					g.drawLine(20, 20 + (getHeight() - 40) * i / 10, getWidth() - 20, 20 + (getHeight() - 40) * i / 10);
					g.drawLine(20 + (getWidth() - 40) * i / 10, 20, 20 + (getWidth() - 40) * i / 10, getHeight() - 20);
				}

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// draw floating curve
				final int iSteps = 2000;
				final Polygon p1 = new Polygon();
				for (int j = 0; j < iSteps; j++) {
					final double dd = j / (double) iSteps;

					final int x = 20 + (int) (dd * (getWidth() - 40));
					p1.addPoint(x, getHeight() - 20 - (int) (f(dd) * (getHeight() - 40)));
				}
				((Graphics2D) g).setStroke(new BasicStroke(3));
				g.setColor(Color.green.darker());
				g.drawPolyline(p1.xpoints, p1.ypoints, p1.npoints);

				/*
				 * //draw floating curve using discrte arguments final Polygon
				 * p2 = new Polygon(); for (int j = 0; j < iSteps; j++) { final
				 * double dd = j / (double) iSteps; final int block = (int)
				 * (blocks * dd); //System.out.println(block); final int x = 20
				 * + (int) (dd * (getWidth() - 40)); p2.addPoint(x, getHeight()
				 * - 20 - (int) (f_(block) * (getHeight() - 40))); }
				 * g.setColor(Color.red); ((Graphics2D) g).setStroke(new
				 * BasicStroke(3, 1, 1, 1f, new float[] { 5f, 5f }, 1));
				 * g.drawPolyline(p2.xpoints, p2.ypoints, p2.npoints);
				 */

				// draw discrete curve
				final Polygon p3 = new Polygon();
				for (int j = 0; j < iSteps; j++) {
					final double dd = j / (double) iSteps;
					final int block = (int) (blocks * dd);
					// System.out.println(block);
					final int x = 20 + (int) (dd * (getWidth() - 40));
					p3.addPoint(x, getHeight() - 20 - (int) (F(block) / (double) maxreward * (getHeight() - 40)));
				}
				g.setColor(Color.red);
				// ((Graphics2D) g).setStroke(new BasicStroke(3, 0, 0, 1f, new
				// float[] { 5f, 5f }, 1));
				((Graphics2D) g).setStroke(new BasicStroke(1));
				g.drawPolyline(p3.xpoints, p3.ypoints, p3.npoints);

				// draw discrete curve using array (only if array is not to
				// big!)
				if (blocks / interval < 1000) {
					if (array == null) {
						// do it only once
						array = createArray();
					}

					final Polygon p4 = new Polygon();
					for (int j = 0; j < iSteps; j++) {
						final double dd = j / (double) iSteps;
						final int block = (int) (blocks * dd);
						// System.out.println(block);
						final int x = 20 + (int) (dd * (getWidth() - 40));
						p4.addPoint(x,
								getHeight() - 20 - (int) (_F(block, array) / (double) maxreward * (getHeight() - 40)));
					}
					g.setColor(Color.orange);
					((Graphics2D) g).setStroke(new BasicStroke(1, 0, 0, 1f, new float[] { 1f, 5f }, 1));
					// ((Graphics2D) g).setStroke(new BasicStroke(1));
					g.drawPolyline(p4.xpoints, p4.ypoints, p4.npoints);
				}

				g.setColor(Color.yellow);
				((Graphics2D) g).setStroke(new BasicStroke(2));
				g.drawLine((int) (20 + (getWidth() - 40) * block1 / blocks), 20,
						(int) (20 + (getWidth() - 40) * block1 / blocks), getHeight() - 20);
				g.drawLine((int) (20 + (getWidth() - 40) * block2 / blocks), 20,
						(int) (20 + (getWidth() - 40) * block2 / blocks), getHeight() - 20);
			}

		};
		this.getContentPane().add(p);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final CReward s = new CReward();

		System.out.println(
				"usage: [reward tokens=...] [blocktime=...] [years=...|blocks=...] [const=...|block1=...] [asymp=...|block2=...] [constinterval=...|constblockinterval=...]");
		System.out.println();

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("const=")) {
				final double d = Double.valueOf(args[i].substring(6));
				if (d >= 0 && d <= 100) {
					s.t1 = d / 100;
					s.block1 = (long) (s.blocks * s.t1); // block number for
															// constant phase
															// end
					System.out.println("INPUT: constant = " + d + " % to block " + s.block1);
				}
			} else if (args[i].startsWith("block1=")) {
				final int ii = Integer.valueOf(args[i].substring(7));
				if (ii >= 0) {
					s.block1 = ii;
					System.out.println("INPUT: constant to block " + s.block1);
				}
			} else if (args[i].startsWith("asymp=")) {
				final double d = Double.valueOf(args[i].substring(6));
				if (d >= 0 && d <= 100) {
					s.t2 = d / 100;
					s.block2 = (long) (s.blocks * s.t2);
					System.out.println("INPUT: asymptotic = " + d + " % from block " + s.block2);
				}
			} else if (args[i].startsWith("block2=")) {
				final int ii = Integer.valueOf(args[i].substring(7));
				if (ii >= 0) {
					s.block2 = ii;
					System.out.println("INPUT: asymptotic from block " + s.block2);
				}
			} else if (args[i].startsWith("blocks=")) {
				final int ii = Integer.valueOf(args[i].substring(7));
				if (ii >= 0) {
					s.blocks = ii;
					System.out.println("INPUT: maximum blocks " + s.blocks);
				}
			} else if (args[i].startsWith("years=")) {
				final int ii = Integer.valueOf(args[i].substring(6));
				if (ii >= 0) {
					s.rewardduration = ii;
					s.blocks = 60 / s.blocktime * 60 * 24 * 365 * s.rewardduration;
					System.out.println("INPUT: maximum blocks " + s.blocks + " after " + ii + " years");
				}
			} else if (args[i].startsWith("blocktime=")) {
				final int ii = Integer.valueOf(args[i].substring(10));
				if (ii >= 0) {
					s.blocktime = ii;
					s.blocks = 60 / s.blocktime * 60 * 24 * 365 * s.rewardduration;
					s.interval = 60 / s.blocktime * 60 * 24 * 365 * s.constduration / 12;
					System.out.println("INPUT: block time " + s.blocktime + " after " + s.blocks + " blocks");
				}
			} else if (args[i].startsWith("tokens=")) {
				final int ii = Integer.valueOf(args[i].substring(7));
				if (ii >= 0) {
					s.rewardtokens = ii;
					System.out.println("INPUT: tokens = " + s.rewardtokens);
				}
			} else if (args[i].startsWith("constinterval=")) {
				final int ii = Integer.valueOf(args[i].substring(14));
				if (ii >= 0) {
					s.constduration = ii;
					s.interval = 60 / s.blocktime * 60 * 24 * 365 * s.constduration / 12;
					System.out.println(
							"INPUT: constant intervall = " + s.constduration + "month (" + s.interval + " blocks)");
				}
			} else if (args[i].startsWith("constblockinterval=")) {
				final int ii = Integer.valueOf(args[i].substring(19));
				if (ii >= 0) {
					s.interval = ii;
					System.out.println("INPUT: constant intervall = " + s.interval + " blocks)");
				}
			}
		}

		// initialize
		s.CalculateReward();

		System.out.println();
		System.out.println("------------------------------------------------------");
		System.out.println();

		System.out.println("blocks    = " + s.blocks);
		System.out.println("block1    = " + s.block1);
		System.out.println("block2    = " + s.block2);
		System.out.println("blocktime = " + s.blocktime);
		System.out.println("years     = " + s.rewardduration);
		System.out.println("tokens    = " + s.rewardtokens);
		System.out.println("quotient  = " + s.quotient);
		System.out.println("maxreward = " + s.maxreward / 1e18);
		System.out.println("constint. = " + s.constduration);

		if (s.blocks / s.interval < 1000) {// print out only short lists
			final ArrayList<Long> arr = s.createArray();

			System.out.println();
			System.out.println("List of rewards [" + arr.size() + "]:");
			for (final Long l : arr) {
				System.out.println("    " + l);
			}

			s.CalculateTotalRewardFromTableData(arr);

		}

		// initialize constants
		// s._init();

		s.setSize(1200, 600);
		s.setVisible(true);
		s.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	/** create the array of discrete values. */
	private ArrayList<Long> createArray() {
		final ArrayList<Long> al = new ArrayList<Long>();
		// create list
		for (long l = 0; l < blocks; l += interval) {
			al.add(F(l));
		}

		return al;
	}
}

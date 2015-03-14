using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace VantagePro2 {
    public class WindDirectionSlices {
        /// <summary>
        /// Class to keep track of the previous wind directions
        /// </summary>
        class WindSlice {
            public int slice;
            public float lowHeading;
            public float highHeading;
            public List<DateTime> samples = new List<DateTime>();
            public WindSlice(int slice, float low, float high) {
                this.slice = slice;
                lowHeading = low;
                highHeading = high;
            }
        };
        /// <summary>
        /// Compare the size of two lists, in decending order
        /// </summary>
        class SliceComparer : IComparer<WindSlice> {
            public int Compare(WindSlice x, WindSlice y) {
                if (y.samples.Count != x.samples.Count)
                    return y.samples.Count - x.samples.Count;
                else if (x.samples.Count == 0)
                    return 0;
                else
                    return y.samples.ElementAt(y.samples.Count - 1).CompareTo(x.samples.ElementAt(x.samples.Count - 1));
            }
        }
        private static readonly int NUM_SLICES = 16;
        private static readonly float DEGREES_PER_SLICE = 22.5F;
        private static readonly float HALF_SLICE = DEGREES_PER_SLICE / 2F;
        private static readonly TimeSpan AGE_SPAN = new TimeSpan(0, 60, 0);
        private static readonly int MAX_PAST_HEADINGS = 4;
        private WindSlice[] windSlices = new WindSlice[NUM_SLICES];
        private SliceComparer comparer = new SliceComparer();

        /// <summary>
        /// Constructor.
        /// </summary>
        public WindDirectionSlices() {
            float heading = -(DEGREES_PER_SLICE / 2);
            for (int i = 0; i < NUM_SLICES; i++) {
                windSlices[i] = new WindSlice(i, heading, heading + DEGREES_PER_SLICE);
                heading += DEGREES_PER_SLICE;
            }
        }

        /// <summary>
        /// Add a heading to the list of headings
        /// </summary>
        /// <param name="iHeading"></param>
        public void addHeading(int iHeading) {
            float heading = (float)iHeading;
            if (heading > 360F - HALF_SLICE)
                heading -= 360F;

            foreach (WindSlice slice in windSlices) {
                if (heading > slice.lowHeading && heading <= slice.highHeading) {
                    slice.samples.Add(DateTime.Now);
                }
            }
        }

        /// <summary>
        /// Get the list of past headings that have the most number of samples
        /// </summary>
        /// <returns>The list of past headings, up to a maximum of 4</returns>
        public List<int> pastHeadings() {
            DateTime now = DateTime.Now;
            DateTime before = now.Subtract(AGE_SPAN);

            //
            // Get rid of the old samples
            //
            foreach (WindSlice slice in windSlices) {
                for (int i = slice.samples.Count - 1; i >= 0; i--) {
                    DateTime time = slice.samples.ElementAt(i);
                    if (time.CompareTo(before) < 0)
                        slice.samples.Remove(time);
                }
            }

            //
            // Sort the array based on the number of samples
            //
            Array.Sort(windSlices, comparer);

            //
            // Pull out the 4 with the highest number of samples
            //
            List<int> headings = new List<int>();
            for (int i = 0; i < MAX_PAST_HEADINGS; i++) {
                if (windSlices[i].samples.Count > 0)
                    headings.Add((int)((float)windSlices[i].slice * DEGREES_PER_SLICE));
            }

            return headings;
        }

        public static void test() {
            WindDirectionSlices bins = new WindDirectionSlices();
            bins.addHeading(10);
            bins.addHeading(10);
            bins.addHeading(320);
            bins.addHeading(355);
            System.Threading.Thread.Sleep(1000);
            bins.addHeading(45);
            System.Threading.Thread.Sleep(100);
            bins.addHeading(100);
            System.Threading.Thread.Sleep(100);
            bins.addHeading(180);
            System.Threading.Thread.Sleep(100);
            bins.addHeading(200);

            List<int> headings = bins.pastHeadings();
            foreach (int heading in headings)
                Console.WriteLine(heading);

            Console.WriteLine("done");
        }
    }
}

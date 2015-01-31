using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Diagnostics;

namespace VantagePro2 {
    class ArchiveManager {
        private String archiveFile;
        private DateTime newestPacketTime;

        public ArchiveManager(String archiveFilename) {
            this.archiveFile = archiveFilename;
            newestPacketTime = findNewestPacketTime();
        }

        public void addPacket(ArchivePacket packet) {
            List<ArchivePacket> list = new List<ArchivePacket>();
            list.Add(packet);
            addPackets(list);
        }

        public void addPackets(List<ArchivePacket> packets) {
            FileStream stream = File.Open(archiveFile, FileMode.Append, FileAccess.Write);
            foreach (ArchivePacket packet in packets) {
                if (packet != null) {
                    if (newestPacketTime.CompareTo(packet.Time) < 0) {
                        stream.Write(packet.Buffer, 0, packet.Buffer.Length);
                        newestPacketTime = packet.Time;
                        Debug.Print("Archived packet with time {0}", packet.Time);
                    }
                    else
                        Debug.Print("Skipping archive of packet with time {0}", packet.Time);
                }
            }
            stream.Close();
        }

        public DateTime findNewestPacketTime() {
            DateTime time = DateTime.Now;
            try {
                FileInfo fileInfo = new FileInfo(archiveFile);
                long fileSize = fileInfo.Length;
                if (fileSize < ArchivePacket.BYTES_PER_RECORD) {
                    time = time.AddYears(-1);
                }
                else {
                    FileStream stream = File.Open(archiveFile, FileMode.Open, FileAccess.Read);
                    stream.Seek(-ArchivePacket.BYTES_PER_RECORD, SeekOrigin.End);
                    byte[] buffer = new byte[ArchivePacket.BYTES_PER_RECORD];
                    stream.Read(buffer, 0, buffer.Length);
                    stream.Close();
                    ArchivePacket packet = new ArchivePacket(buffer, 0, 0.01F, 5, 0.0F, 0);
                    time = packet.Time;
                }
            }
            catch (FileNotFoundException) {
                time = time.AddYears(-1);
            }

            return time;
        }

        public List<ArchivePacket> readPackets(DateTime startTime, DateTime endTime) {
            byte[] buffer = new byte[ArchivePacket.BYTES_PER_RECORD];
            FileStream stream = File.Open(archiveFile, FileMode.Open, FileAccess.Read);
            stream.Seek(-ArchivePacket.BYTES_PER_RECORD, SeekOrigin.End);
            ArchivePacket packet;
            long streamPosition;
            do {
                streamPosition = stream.Position;
                stream.Read(buffer, 0, buffer.Length);
                packet = new ArchivePacket(buffer, 0, 0.01F, 5, 0.0F, 0);
                stream.Seek(-(ArchivePacket.BYTES_PER_RECORD * 2), SeekOrigin.Current);
            } while (startTime.CompareTo(packet.Time) != 0 && streamPosition > 0);

            List<ArchivePacket> list = new List<ArchivePacket>();
            while (stream.Read(buffer, 0, buffer.Length) == buffer.Length) {
                packet = new ArchivePacket(buffer, 0, 0.01F, 5, 0.0F, 0);
                list.Add(packet);
            }

            stream.Close();
            return list;
        }
    }
}

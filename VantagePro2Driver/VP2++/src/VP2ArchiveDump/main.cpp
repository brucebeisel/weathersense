/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#include <iostream>
#include <iomanip>
#include <fstream>
#include "../VP2/VP2Constants.h"
#include "../VP2/Weather.h"
#include "../VP2/ArchivePacket.h"

using namespace std;
using namespace vp2;

bool signalCaught;

int
main(int argc, char *argv[]) {
    if (argc < 2) {
        cerr << "Usage: vp2_dump_archive <filename>" << endl;
    }

    char *file = argv[1];
    char buffer[vp2::ArchivePacket::BYTES_PER_PACKET];

    ifstream stream(file, ifstream::in | ios::binary);

    int record = 0;
    while (true) {
        stream.read(buffer, sizeof(buffer));
        if (!stream) {
            stream.close();
            exit(0);
        }
        ArchivePacket packet(buffer, 0);

        cout << setw(5) << setfill('0') << record << " - " << Weather::formatDateTime(packet.getDateTime()) << endl;
        record++;
    }
}

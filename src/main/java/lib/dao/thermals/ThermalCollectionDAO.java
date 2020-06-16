/*
    igcAnalyse
    Copyright (C) 2020  Olivier Zeyen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lib.dao.thermals;

import lib.thermals.ThermalCollection;

import java.io.IOException;

public interface ThermalCollectionDAO {
    ThermalCollection load(String path) throws IOException, ClassNotFoundException;
    void save(ThermalCollection tc, String path) throws IOException;

    static void main(String[] args) throws IOException, ClassNotFoundException {
        ThermalCollectionDAO dao = new ThermalCollectionCUP();
        ThermalCollection tc = dao.load("res.cup");
        dao.save(tc, "res.2.cup");
    }
}

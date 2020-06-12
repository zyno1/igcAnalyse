# IGCAnalyse

The program tries to extract average circling positions
from igc files. These positions may be close to thermals.

Some results for the PACA region in France:

(These files only contain the 177 most common positions found in about 1GB of data)

[cup file](results/res.cup)

[kmz file](results/res.kmz)

![google earth screenshot](results/google_earth_screenshot_1.png)

![google earth screenshot](results/google_earth_screenshot_2.png)

# Usage

Compiling:
```
cd src
javac Main.java
```

Run:
```
java Main
```

Options:
```
-h
--help             display help screen

--threads <int>    set the number of threads to use

-i <str>           set the path that contains the flight logs

--max-count <int>  set the maximum number of thermals to keep
--min <int>        set the minimum amount of times a thermal has been found to be kept
```

The `-i` options sets the path where the igc flight data is located. The path
will be recursively walked through and every igc file inside the path will
be analyzed.

The `--max-count` option sets the maximum number of thermals to keep in the result
file and the `--min` option sets the minimum number of times a thermal has to be merged
to be kept in the result file.

The results are written to the files `res.cup` and `res.kml`. The `res.cup` file
can be used with a gps and the `res.kml` file can be opened with Google Earth.
Note that the `res.kml` file requires the `cylinder.dae` file that can be found
int the `results` directory to be in the same folder.

Both files can also be zipped together into a single file and then by changing
the extension from `.zip` to `.kmz` the file can be opened with Google Earth
without needing to keep track of 2 files.
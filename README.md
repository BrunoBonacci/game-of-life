# game-of-life

Code from a Clojure Dojo session to build the Conway's Game Of Life.

![pulsar](pulsar.gif)

## Usage

Data files are available under `./data` directory.
You can change the speed by setting the environment variable `RATE`.
If instead of the datafile you pass `-random` as parameter it will
create a random board with some alive cells. You may control the
percentage of living cells with the environment variable `PCT_ALIVE`
(default 30).

```
export RATE=5
lein run [data-file.edn]|-random
```

or

```
RATE=15 PCT_ALIVE=25 lein run -random
```


## License

Copyright © 2018 Bruno Bonacci and others

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

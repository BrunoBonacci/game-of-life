(ns game-of-life.core
  (:require [clojure.string :as str])
  (:gen-class))

(comment
  ;;
  ;; example of a board
  ;; :_ -> empty cell
  ;; :x -> living cell
  ;;
  [:_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :x :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   ]
  )


(defn board
  ([width height]
   (board width height 0))
  ([width height pct-alive]
   (let [n-alive (* width height pct-alive 1/100)
         n-dead  (- (* width height) n-alive)]
     (with-meta
       (->> (concat (repeat n-alive :x) (repeat n-dead :_))
          (shuffle)
          (apply vector))
       {:dim [width height]}))))

;; (board 5 10)
;; (meta (board 5 10))


(defn board-shape
  [board]
  (-> board meta :dim))


;; (board-shape (board 10 20))

;; (board-shape (board 5 5))


(defn posision-shift
  [[w h] i [sx sy]]
  (let [x (mod i w)
        y (quot i w)
        x' (+ x sx)
        y' (+ y sy)
        ;; wrapping
        x' (if (< x' 0)  (+ w x')   x')
        x' (if (>= x' w) (mod x' w) x')
        y' (if (< y' 0)  (+ h y')   y')
        y' (if (>= y' h) (mod y' h) y')]
    (+ x' (* w y'))))


(defn neighbours
  "given a board and linear positions it return the "
  [board i]
  (let [dim (board-shape board)
        loc (partial posision-shift dim i)]
    (map (partial get board)
         [(loc [-1 -1]) (loc [0 -1]) (loc [+1 -1])
          (loc [-1 0])    #_i        (loc [+1 0])
          (loc [-1 +1]) (loc [0 +1]) (loc [+1 +1])])))



(comment
  ;; returns the neighbours of :x
  (neighbours
   ^{:dim [10 10]}
   [:_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :ul :uc :ur :_ :_ :_ :_ :_ :_
    :_ :cl :x :cr :_ :_ :_ :_ :_ :_
    :_ :dl :dc :dr :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    ] 32)

)


(defn cell-transition
  [board cell-index]
  (let [nbr       (neighbours board cell-index)
        num-alive (->> nbr (filter #(= % :x)) count)
        cell      (get board cell-index)]
    (case num-alive
        2 cell
        3 :x
        :_)))


(defn transition
  [board]
  (with-meta
    (mapv (partial cell-transition board) (range (count board)))
    (meta board)))


(defn display
  [board & {:keys [live empty sep]
            :or {live ":x" empty ":_" sep " "}}]
  (let [[w h] (board-shape board)
        fmt {:_ empty
             :x live}]
    (->> board
       (map fmt)
       (partition w)
       (map (partial str/join sep))
       (str/join "\n"))))


(comment

  (->>
   ^{:dim [10 10]}
   [:_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :x :x :_ :_
    :_ :_ :_ :_ :_ :_ :x :_ :x :_
    :_ :_ :_ :_ :_ :_ :x :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
    ]
   (iterate transition)
   (map display)
   (take 50)
   (run! #(println % "\n\n")))

  )


(defn term-display
  [board rate]
  (let [delay (quot 1000 rate)
        clear-screen (str "\033[2J")]
    (loop [board board]
      ;; painting the whole screen at once reduce the flickering
      (print
        (with-out-str
          (println clear-screen "Press Ctrl-C to stop.\n")
          (println (display board :live "##" :empty "__" :sep "|"))
          (println "\n")))
      (flush)
      (Thread/sleep delay)
      (recur (transition board)))))


(defn load-board
  [file]
  (read-string (slurp file)))


(defn opt [name default]
  (or (some-> (System/getenv name) read-string) default))

(defn -main [& [board-file]]
  (term-display
   (if (= "-random" board-file)
     (board 35 35 (opt "PCT_ALIVE" 30))
     (load-board (or board-file "./data/pulsar.edn")))
   (opt "RATE" 5)))

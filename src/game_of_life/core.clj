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


(defn board [width height]
  (with-meta
    (apply vector (repeat (* width height) :_))
    {:dim [width height]}))

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
      (println clear-screen "Press Ctrl-C to stop.\n")
      (println (display board :live "##" :empty "__" :sep "|"))
      (println "\n\n")
      (Thread/sleep delay)
      (recur (transition board)))))


(defn load-board
  [file]
  (read-string (slurp file)))


(defn -main [& [board]]
  (term-display
   (load-board (or board "./data/pulsar.edn"))
   (read-string (or (System/getenv "RATE") "5"))))

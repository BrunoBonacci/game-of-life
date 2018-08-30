(ns game-of-life.core
  (:require [clojure.string :as str])
  (:gen-class))

(comment
  ;;
  ;; example of a board, assumed squared
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


(defn board [size]
  (apply vector (repeat (* size size) :_)))

;; (board 5)

(defn board-shape
  [board]
  (let [e (int (Math/sqrt (count board)))]
    [e e]))


;; (board-shape (board 10))

;; (board 5)


(defn neighbours
  "given a board and linear positions it return the "
  [board i]
  (let [[size _] (board-shape board)]
    (map (partial get board)
         [(- i size 1)  (- i size)  (- i size -1)
          (- i 1)          #_i      (- i -1)
          (+ i size -1) (+ i size)  (+ i size 1)])))



(comment
  ;; returns the neighbours of :x
  (neighbours
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
  (mapv (partial cell-transition board) (range (count board))))


(defn display
  [board & {:keys [live empty sep]
            :or {live ":x" empty ":_" sep " "}}]
  (let [[size _] (board-shape board)
        fmt {:_ empty
             :x live}]
    (->> board
       (map fmt)
       (partition size)
       (map (partial str/join sep))
       (str/join "\n"))))


(comment

  (->>
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
   (run! #(println % "\n\n"))
   )

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

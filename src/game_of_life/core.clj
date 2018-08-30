(ns game-of-life.core
  (:require [clojure.string :as str])
  (:gen-class))

(comment
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
  [board]
  (let [[size _] (board-shape board)]
    (->> board
       (partition size)
       (map (partial str/join " "))
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
      (println clear-screen (display board))
      (Thread/sleep delay)
      (recur (transition board)))))


(defn -main []
  (term-display
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
   3))

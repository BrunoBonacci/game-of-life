(ns game-of-life.core
  (:require [clojure.string :as str]))

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
  [board i]
  (let [[size _] (board-shape board)
        ul (get board (- i size 1))
        uc (get board (- i size))
        ur (get board (- i size -1))
        cl (get board (- i 1))
        cr (get board (- i -1))
        dl (get board (+ i size -1))
        dc (get board (+ i size))
        dr (get board (+ i size 1))]
    [ul uc ur cl cr dl dc dr]))



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
  (let [nbr (neighbours board cell-index)
        alive (->> nbr (filter #(= % :x)) count)
        cell (get board cell-index)]
    (case alive
        2 cell
        3 :x
        :_)))


(defn transition [board]
  (into []
        (map-indexed
         (fn [i _] (cell-transition board i))
         board)))


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

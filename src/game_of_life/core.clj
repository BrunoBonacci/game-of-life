(ns game-of-life.core)

(comment
  [:_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   :_ :_ :_ :_ :_ :_ :_ :_ :_ :_
   ])


(defn board [size]
  (apply vector (repeat (* size size) :_)))

(defn board-shape
  [board]
  (let [e (int (Math/sqrt (count board)))]
    [e e]))


(defn neighbours
  [board i]
  (let [[size _] (board-shape board)
        ]
    ))

(board-shape (board 10))

;; (board 5)

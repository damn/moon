(ns moon.math.vector2-test
  (:require [moon.game :as game]
            [clojure.test :refer :all]))
(set! *unchecked-math* :warn-on-boxed)

(defn nearly-equal? [[x1 y1] [x2 y2]]
  (and (game/nearly-equal? x1 x2)
       (game/nearly-equal? y1 y2)))

(deftest vecs-nearly-equal?
  (is (nearly-equal? [0.0000011 0.0123]
                     [0.000001 0.0123])))

(deftest scale
  (is (nearly-equal? (game/scale [1 3] 0.5)
                     [0.5 1.5]))
  (is (nearly-equal? (game/scale [2 1.2] -3)
                     [-6.0 -3.6000001]))
  (is (nearly-equal? (game/scale [0 0] 10)
                     [0.0 0.0])))

(deftest length
  (is (game/nearly-equal? (game/length [1.2 0.1])
                       1.2041595))
  (is (game/nearly-equal? (game/length [1.2 -0.1])
                       1.2041595)))

(comment

 (clojure.pprint/pprint
  (for [v [[0 1]
           [1 1]
           [1 0]
           [1 -1]
           [0 -1]
           [-1 -1]
           [-1 0]
           [-1 1]]]
    [v
     (game/angle-from-vector v)]))

 )

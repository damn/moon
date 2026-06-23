(ns moon.math.vector2-test
  (:require [moon.number :as math]
            [math.vector2.scale :as scale]
            [math.vector2.length :as length]
            [clojure.test :refer :all]))

(set! *unchecked-math* :warn-on-boxed)

(defn nearly-equal? [[x1 y1] [x2 y2]]
  (and (math/nearly-equal? x1 x2)
       (math/nearly-equal? y1 y2)))

(deftest vecs-nearly-equal?
  (is (nearly-equal? [0.0000011 0.0123]
                     [0.000001 0.0123])))

(deftest scale
  (is (nearly-equal? (scale/f [1 3] 0.5)
                     [0.5 1.5]))
  (is (nearly-equal? (scale/f [2 1.2] -3)
                     [-6.0 -3.6000001]))
  (is (nearly-equal? (scale/f [0 0] 10)
                     [0.0 0.0])))

(deftest length
  (is (math/nearly-equal? (length/f [1.2 0.1])
                           1.2041595))
  (is (math/nearly-equal? (length/f [1.2 -0.1])
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
     (math.vector2.angle-from-vector/f v)]))

 )

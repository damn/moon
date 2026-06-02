(ns clojure.rand.srand)

(defn srand
  ([random] (.nextFloat ^java.util.Random random))
  ([n random] (* n (srand random))))

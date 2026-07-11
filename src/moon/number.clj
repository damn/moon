(ns moon.number)

(def ^:private float-rounding-error (double 0.000001))

(defn nearly-equal?
  ([x y]
   (nearly-equal? x y float-rounding-error))
  ([a b epsilon]
   (<= (Math/abs (- a b))
       epsilon)))

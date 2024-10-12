(ns core.operation
  (:refer-clojure :exclude [apply])
  (:require [clojure.component :refer [defsystem defc]]
            [clojure.math :as math]))

(defn- +? [n]
  (case (math/signum n)
    0.0 ""
    1.0 "+"
    -1.0 ""))

(defsystem value-text "FIXME" [_])

(defn info-text [{value 1 :as operation}]
  (str (+? value) (value-text operation)))

(defsystem apply "FIXME" [_ base-value])
(defsystem order "FIXME" [_])

(defc :op/inc
  {:data :number
   :let value}
  (value-text [_] (str value))
  (apply [_ base-value] (+ base-value value))
  (order [_] 0))

(defc :op/mult
  {:data :number
   :let value}
  (value-text [_] (str (int (* 100 value)) "%"))
  (apply [_ base-value] (* base-value (inc value)))
  (order [_] 1))
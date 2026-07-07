(ns clojure.is-pf-blocked
  (:require [clojure.cell-is-blocked :as blocked?]))

(defn f [this]
  (blocked?/f this :z-order/ground))

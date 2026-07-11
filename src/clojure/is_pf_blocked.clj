(ns clojure.is-pf-blocked
  (:require [moon.cell :as cell]))

(defn f [this]
  (cell/blocked? this :z-order/ground))

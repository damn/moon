(ns moon.cell.is-pf-blocked
  (:require [moon.cell.is-blocked :as blocked?]))

(defn f [this]
  (blocked?/f this :z-order/ground))

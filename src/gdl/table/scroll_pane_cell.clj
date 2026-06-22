(ns gdl.table.scroll-pane-cell
  (:require [gdl.scroll-pane :as scroll-pane]
            [gdl.actor.get-height :refer [get-height]]
            [gdl.actor.get-width :refer [get-width]]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (get-height table))})

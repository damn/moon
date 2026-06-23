(ns gdl.scroll-pane-cell
  (:require [ui.scroll-pane :as scroll-pane]
            [scene2d.actor.get-height :refer [get-height]]
            [gdl.get-width :refer [get-width]]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (get-height table))})

(ns clojure.scenes.scene2d.ui.table.scroll-pane-cell
  (:require [clojure.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [clojure.actor.get-height :refer [get-height]]
            [clojure.actor.get-width :refer [get-width]]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (get-height table))})

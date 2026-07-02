(ns scene2d.ui.table.scroll-pane-cell
  (:require [clojure.gdx.actor.get-height :as get-height]
            [clojure.gdx.actor.get-width :as get-width]
            [scene2d.ui.scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (get-width/f table) buffer)
   :height (min (- scroll-pane-height buffer)
                (get-height/f table))})

(ns scene2d.ui.table.scroll-pane-cell
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (gdx/get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (gdx/get-height table))})

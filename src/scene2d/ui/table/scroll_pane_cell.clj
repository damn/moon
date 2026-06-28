(ns scene2d.ui.table.scroll-pane-cell
  (:require [scene2d.ui.scroll-pane :as scroll-pane])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (Actor/.getWidth table) buffer)
   :height (min (- scroll-pane-height buffer)
                (Actor/.getHeight table))})

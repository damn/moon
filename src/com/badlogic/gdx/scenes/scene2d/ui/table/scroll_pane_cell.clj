(ns com.badlogic.gdx.scenes.scene2d.ui.table.scroll-pane-cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [com.badlogic.gdx.scenes.scene2d.actor.get-height :refer [get-height]]
            [com.badlogic.gdx.scenes.scene2d.actor.get-width :refer [get-width]]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (get-height table))})

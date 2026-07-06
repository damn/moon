(ns scene2d.ui.table.scroll-pane-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [scene2d.ui.scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (actor/get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (actor/get-height table))})

(ns clojure.scroll-pane-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/new table skin)
   :width  (+ (actor/getWidth table) buffer)
   :height (min (- scroll-pane-height buffer)
                (actor/getHeight table))})

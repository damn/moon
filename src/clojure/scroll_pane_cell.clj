(ns clojure.scroll-pane-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui-scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (actor/getWidth table) buffer)
   :height (min (- scroll-pane-height buffer)
                (actor/getHeight table))})

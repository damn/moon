(ns clojure.scroll-pane-cell
  (:require
            [clojure.scene2d.actor.get-height]
            [clojure.scene2d.actor.get-width]
            [clojure.ui-scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (clojure.scene2d.actor.get-width/f table) buffer)
   :height (min (- scroll-pane-height buffer)
                (clojure.scene2d.actor.get-height/f table))})

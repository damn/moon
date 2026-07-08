(ns clojure.scroll-pane-cell
  (:require
            [clojure.actor.get-height]
            [clojure.actor.get-width]
            [clojure.ui-scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (clojure.actor.get-width/f table) buffer)
   :height (min (- scroll-pane-height buffer)
                (clojure.actor.get-height/f table))})

(ns clojure.scroll-pane-cell
  (:require
            [clojure.get-height]
            [clojure.get-width]
            [clojure.ui-scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (clojure.get-width/f table) buffer)
   :height (min (- scroll-pane-height buffer)
                (clojure.get-height/f table))})

(ns clojure.scroll-pane-cell
  (:require
            [gdl.actor :as actor]
            [clojure.ui-scroll-pane :as scroll-pane]))

(defn create [table skin scroll-pane-height buffer]
  {:actor (scroll-pane/create
           {:actor table
            :skin skin})
   :width  (+ (actor/get-width table) buffer)
   :height (min (- scroll-pane-height buffer)
                (actor/get-height table))})

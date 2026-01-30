(ns moon.dev-menu.update-labels.elapsed-time
  (:require [moon.readable :as readable]))

(def item
  {:label "elapsed-time"
   :update-fn (fn [{:keys [ctx/elapsed-time]}]
                (str (readable/number elapsed-time) " seconds"))
   :icon "images/clock.png"})

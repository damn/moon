(ns moon.dev-menu.update-labels.elapsed-time
  (:require [moon.number :as number]))

(def item
  {:label "elapsed-time"
   :update-fn (fn [{:keys [ctx/elapsed-time]}]
                (str (number/readable elapsed-time) " seconds"))
   :icon "images/clock.png"})

(ns clojure.update-labels.elapsed-time
  (:require [clojure.readable :as readable]))

(def item
  {:label "elapsed-time"
   :update-fn (fn [{:keys [ctx/elapsed-time]}]
                (str (readable/f elapsed-time) " seconds"))
   :icon "images/clock.png"})

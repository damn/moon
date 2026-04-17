(ns moon.draw.with-line-width
  (:require [moon.draws :as draws]
            [clojure.graphics.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
    (draws/handle ctx draws)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width)))

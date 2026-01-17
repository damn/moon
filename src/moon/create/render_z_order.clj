(ns moon.create.render-z-order
  (:require [moon.utils :as utils]))

(defn step
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (utils/define-order z-orders)))

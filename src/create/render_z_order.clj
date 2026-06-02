(ns create.render-z-order
  (:require [clojure.order :refer [define-order]]))

(defn step
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (define-order z-orders)))

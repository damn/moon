(ns create.render-z-order
  (:require [clojure.order :refer [define-order]]))

(defn step
  [{:keys [ctx/z-orders]}]
  (define-order z-orders))

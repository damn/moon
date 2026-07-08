(ns clojure.moon.create-render-z-order
  (:require [clojure.define-order :as define-order]
            [clojure.moon.z-orders :refer [z-orders]]))

(def render-z-order
  (define-order/f z-orders))

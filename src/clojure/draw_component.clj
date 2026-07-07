(ns clojure.draw-component
  (:require [clojure.k-render :refer [k->render]]))

(defn draw-component
  [ctx entity k v]
  ((k->render k) v entity ctx))

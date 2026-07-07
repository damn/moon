(ns clojure.mouseover-actor
  (:require [clojure.hit :refer [hit]]
            [clojure.unproject :as unproject]
            [clojure.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (unproject/f (mouse-position ctx)))))

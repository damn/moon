(ns clojure.hit
  (:require [gdl.stage :as stage]))

(defn hit [stage [x y]]
  (stage/hit stage x y true))

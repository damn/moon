(ns clojure.hit
  (:require [clojure.stage :as stage]))

(defn hit [stage [x y]]
  (stage/hit stage x y true))
